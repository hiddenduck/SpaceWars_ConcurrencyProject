-module(login_manager).
-export([start/0, 
		create_account/2, 
		close_account/2, 
		login/2, 
		logout/1,
		online/0,
		test/0,
		handle/2,
		level_up/1,
		level_down/1,
		start_match/2]).


start() ->
	case file:read_file("passwords") of
		{ok, <<>>} -> Passwords = #{};
		{ok, PassBin} -> Passwords = erlang:binary_to_term(PassBin)
	end,
	case file:read_file("levels") of
		{ok, <<>>} -> Levels = #{};
		{ok, LevelsBin} -> Levels = erlang:binary_to_term(LevelsBin)
	end,
	register(?MODULE, spawn(fun() -> loop(Passwords) end)), 
	register(levels_manager, spawn(fun() -> loop_levels(Levels) end)),
	{Passwords, Levels}.
	%se o file_manager for um processo pode ser que não funcione se for ultrapassado
	%register(file_manager, spawn(fun() -> file_manager() end)).

invoke(Request) ->
	?MODULE ! {Request, self()},
	receive {Res, ?MODULE} -> Res end.


create_account(Username, Passwd) ->
	invoke({create_account, Username, Passwd}).
	
level_up(Username) ->
	levels_manager ! {level_up, Username, self()},
	receive {Status, New_Level, levels_manager} -> {Status, New_Level} end.

level_down(Username) ->
	levels_manager ! {level_down, Username, self()},
	receive {Status, New_Level, levels_manager} -> {Status, New_Level} end.

start_match(FstUsername, SecUsername) ->
	levels_manager ! {start_match, FstUsername, SecUsername, self()},
	receive {Status, FstLevel, SecLevel, levels_manager} -> {Status, FstLevel, SecLevel} end.

close_account(Username, Passwd) ->
	invoke({close_account, Username, Passwd}).

login(Username, Passwd) ->
	invoke({login, Username, Passwd}).

logout(Username) ->
	invoke({logout, Username}).

online() ->
	invoke(online).

handle(Request, Map) ->
	case Request of 
		{create_account, Username, Passwd} -> 
			case maps:find(Username, Map) of
				error -> 
					levels_manager ! {set_level, Username, self()},
					{ok, 1, Map#{Username => Passwd}};
				_ ->
					{user_exists, 0, Map}
			end;
		{close_account, Username, Passwd} -> 
			case maps:find(Username, Map) of
				{ok, Passwd} -> 
					{ok, maps:remove(Username, Map)};
				_ ->
					{invalid, Map}
			end;
		{login, Username, Passwd} ->
			case maps:find(Username, Map) of
				{ok, Passwd} -> 
					{ok, Map};
				_ ->
					{invalid, Map}
			end
		%{logout, Username} -> 
		%	case maps:find(Username, Map) of
		%		{ok, {Passwd, true}} -> 
		%			{ok, maps:update(Username, {Passwd, false}, Map)};
		%		_ ->
		%			{invalid, Map}
		%	end
		%Não faz sentido ser aqui que se regista quem está ou não online, isso é problema do servidor, isto vai ser só gestão de contas
		%Só devia ser permitido chegar a estas funções se já estiver online.
		%online ->
		%	Res = [User || {User, {_, true}} <- maps:to_list(Map)], 
		%	{Res, Map}
	end.


loop(Map) ->
	receive
		{{create_account, Username, Passwd}, From} ->
			{Res, Level, NextState} = handle({create_account, Username, Passwd}, Map),
			From ! {{Res,Level}, ?MODULE},
			loop(NextState);

		{Request, From} ->
			{Res, NextState} = handle(Request, Map),
			From ! {Res, ?MODULE},
			loop(NextState);
		stop ->
			file:write_file("passwords", erlang:term_to_binary(Map))
	end.

loop_levels(Map) ->
	receive
		{set_level, Username, From} ->
			case maps:find(Username, Map) of
				error ->
					From ! {ok,1, levels_manager}, 
					loop_levels(maps:put(Username, {1, 0}, Map));
				{ok, {Level,_}} ->
					From ! {user_exists, Level, levels_manager},
					loop_levels(Map)
			end;
		{level_up, Username, From} ->
			case maps:find(Username, Map) of
				{ok, {Level,_}} ->
					From ! {ok, Level+1, levels_manager}, 
					loop_levels(maps:put(Username, {Level+1, 0}, Map));
				error ->
					From ! {invalid_user, 0, levels_manager},
					loop_levels(Map)
			end;
		{level_down, Username, From} ->
			case maps:find(Username, Map) of
				{ok, {1,_}} ->
					From ! {ok,1, levels_manager}, 
					loop_levels(Map);
				{ok, {Level,_}} ->
					From ! {ok, Level-1, levels_manager},
					loop_levels(maps:put(Username, {Level-1, 0}, Map));
				error ->
					From ! {invalid_user, 0, levels_manager},
					loop_levels(Map)
			end;
		{start_match, FstUsername, SecUsername, From} ->
			case maps:find(FstUsername, Map) of 
				error ->
					From ! {invalid_fstuser, 0, 0, levels_manager},
					loop_levels(Map);
				{ok, {Level, _}} ->
					case maps:find(SecUsername, Map) of 
						error ->
							From ! {invalid_secuser, 0, 0, levels_manager},
							loop_levels(Map);
						{ok, {Level, _}} ->
							From ! {ok, Level, Level, levels_manager},
							loop_levels(Map);
						{ok, {OtherLevel, _}} ->
							From ! {different_levels, Level, OtherLevel, levels_manager},
							loop_levels(Map)

					end
			end;
		{end_match, Winner, Loser, From} ->
			case maps:find(Winner, Map) of 
				error ->
					From ! {invalid_winner, 0, 0, levels_manager},
					loop_levels(Map);
				{ok, {Level, Wins}} ->
					case maps:find(Loser, Map) of 
						error ->
							From ! {invalid_loser, 0, 0, levels_manager},
							loop_levels(Map);
						{ok, {OtherLevel, _}} ->
							if Wins+1 == Level*2 -> NewLevel = Level+1, NewWins = 0 ;
							   true -> NewLevel = Level, NewWins = Wins+1
							end,
							From ! {ok, NewLevel, OtherLevel, levels_manager},
							loop_levels(Map#{Winner => {NewLevel, NewWins}})
					end
			end;
		stop ->
			file:write_file("levels", erlang:term_to_binary(Map))
	end.


stop() ->
	?MODULE ! stop,
	levels_manager ! stop,
	ok.

test() ->
	start(),
	A = create_account("hugo_rocha", "pw123"),
	B = create_account("hugo_rocha_sec", "secondpw"),
	K = login("hugo_rocha", "pw123"),
	L = login("hugo_rocha_sec", "secondpw"),
	C = level_up("hugo_rocha"),
	D = level_up("hugo_rocha"),
	E = level_up("hugo_rocha"),
	F = level_up("hugo_rocha"),
	G = create_account("hugo_rocha", "xd"),
	H = level_up("hugo_rochada"),
	I = level_down("hugo_rocha"),
	level_up("hugo_rocha_sec"),
	level_up("hugo_rocha_sec"),
	level_up("hugo_rocha_sec"),
	J = start_match("hugo_rocha", "hugo_rocha_sec"),
	Z = stop(),
	{A,B,K,L,C,D,E,F,G,H,I,J,Z}.