/* autogenerated by Processing revision 1292 on 2023-05-10 */
import processing.core.*;
import java.net.Socket;
import java.util.*;
import java.io.IOException;
import java.util.List;

public class Processing extends PApplet{
  private ConnectionManager connectionManager;
  public Processing(ConnectionManager connectionManager){
    super();
    this.connectionManager = connectionManager;
  }

  private class textBox{
    private final float x, y, widthBox, heightBox;
    private final String title;

    private boolean active;
    private StringBuilder text;
    private textBox(float x, float y, float width, float height, String title){
      this.x = x;
      this.y = y;
      this.widthBox = width;
      this.heightBox = height;
      this.title = title;
      this.text = new StringBuilder();
      this.active = false;
    }

    public void draw(){
      fill(206, 235, 251);
      textSize(width*0.05f);
      textAlign(LEFT);
      strokeWeight(this.active ? 3 : 0);
      text(title, this.x,  this.y - height*0.01f);
      rect(this.x, this.y, this.widthBox, this.heightBox);
      fill(0);
      textSize(width*0.08f);
      text(this.text.toString(), this.x, this.y+this.heightBox);
      strokeWeight(0);
    }

    public void select(int x, int y){
      this.active = x > this.x && x < this.x + this.widthBox && y > this.y && y < this.y + this.heightBox;
    }

    public void keyPressed(char key){
      if(this.active) {
        if (key == BACKSPACE) {
          if(this.text.length()>0)
            this.text.deleteCharAt(this.text.length() - 1);
        } else if (key >= 'a' && key <= 'z' || key >= 'A' && key <= 'Z' || key >= '0' && key <= '9') {
          this.text.append(key);
        }
      }
    }

    public void reset(){
      this.text = new StringBuilder();
      this.active = false;
    }

    public String getText(){
      return this.text.toString();
    }
  }

  private class Moving{
    private String[] keys;

    private boolean changed;

    public Moving(){
      this.keys = new String[3];
      this.keys[0] = "f";
      this.keys[1] = "f";
      this.keys[2] = "f";
      this.changed = false;
    }

    public void keyPressed(int index){
      if(this.keys[index].equals("f")) {
        this.keys[index] = "t";
        this.changed = true;
      }
    }

    public void keyReleased(int index){
      if(this.keys[index].equals("t")) {
        this.keys[index] = "f";
        this.changed = true;
      }
    }

    public String getMessage(){
      if(this.keys[0].equals(this.keys[2]))
        return "f:"+this.keys[1]+":f";
      else
        return this.keys[0]+":"+this.keys[1]+":"+this.keys[2];
    }
  }

  private textBox user, password;
  private PImage menuImage;

  private String menu;

  private String message;
  private boolean isInGame;

  private int level;

  private GameState gameState;

  private boolean registerMenu, isReady;

  private Communicator[] communicators;

  private Map<Character, Triple> colorMap;

  private List<String> topNames;

  private List<String> topLevels;
  private int topMinLimit;

  private int topMaxLimit;

  private Moving moving;
  public void setup(){
    frameRate(30);
    this.menuImage = loadImage("images/space.jpg");
    this.menu = "startMenu";
    this.isInGame = false;
    this.message = "";
    this.user = new textBox(0, height*0.3f, width, width*0.1f,"Username:");
    this.password = new textBox(0, height*0.45f, width, width*0.1f,"Password:");
    this.isReady = false;
    this.gameState = new GameState();
    this.communicators = new Communicator[5]; // Pos, enemyPos, box, point, game

    this.communicators[0] = new CommunicatorPos(this.connectionManager, this.gameState, "");
    this.communicators[0].start();
    this.communicators[1] = new CommunicatorPos(this.connectionManager, this.gameState, "E");
    this.communicators[1].start();
    this.communicators[2] = new CommunicatorBox(this.connectionManager, this.gameState);
    this.communicators[2].start();
    this.communicators[3] = new CommunicatorPoint(this.connectionManager, this.gameState);
    this.communicators[3].start();
    this.communicators[4] = new CommunicatorGame(this.connectionManager, this.gameState);
    this.communicators[4].start();

    this.moving = new Moving();
    this.colorMap = new HashMap<>();
    this.colorMap.put('r', new Triple(255,0,0));
    this.colorMap.put('g', new Triple(34,139,34));
    this.colorMap.put('b', new Triple(0,191,255));

    this.topNames = new ArrayList<>();
    this.topLevels = new ArrayList<>();
  }

  private void startMenu(){
    background(this.menuImage);
    textAlign(CENTER, CENTER);
    fill(206, 235, 251);
    textSize(width*0.1f);
    text("Welcome to SpaceWars",width/2.0f,height*0.05f);
    strokeWeight(3);
    textSize(width*0.05f);
    text("Login",width*0.5f,height*0.25f);
    fill(112,128,144);
    rect(width*0.45f, height*0.3f, width*0.1f, height*0.1f);
    fill(206, 235, 251);
    textSize(width*0.05f);
    text("Register",width*0.5f,height*0.45f);
    fill(112,128,144);
    rect(width*0.45f, height*0.5f, width*0.1f, height*0.1f);
    fill(206, 235, 251);
    textSize(width*0.05f);
    text("Exit",width*0.5f,height*0.65f);
    fill(112,128,144);
    rect(width*0.45f, height*0.7f, width*0.1f, height*0.1f);
    strokeWeight(0);
  }

  private void logRegMenu(){
    background(this.menuImage);
    textAlign(CENTER, CENTER);
    fill(206, 235, 251);
    textSize(width*0.1f);
    text(this.registerMenu ? "Register Menu" : "Login Menu",width/2.0f,height*0.05f);
    this.user.draw();
    this.password.draw();
    textAlign(CENTER, CENTER);
    if(Objects.equals(this.message, "ok"))
      fill(124,252,0);
    else
      fill(255,160,122);
    text(message, width*0.5f, height*0.80f);
    fill(206, 235, 251);
    triangle(width*0.05f, 0, 0, height*0.025f, width*0.05f, height*0.05f);
    fill(112,128,144);
    rect(width*0.45f, height*0.65f, width*0.1f, height*0.1f);
  }

  private void loggedMenu(){
    background(this.menuImage);
    textAlign(CENTER, CENTER);
    fill(206, 235, 251);
    triangle(width*0.05f, 0, 0, height*0.025f, width*0.05f, height*0.05f);
    fill(206, 235, 251);
    textSize(width*0.05f);
    text("Welcome " + this.user.getText(),width/2.0f,height*0.05f);
    text("Level: " + this.level,width/2.0f,height*0.1f);
    strokeWeight(3);
    textSize(width*0.05f);
    text("Play",width*0.5f,height*0.25f);
    fill(112,128,144);
    rect(width*0.45f, height*0.3f, width*0.1f, height*0.1f);
    fill(206, 235, 251);
    textSize(width*0.05f);
    text("LeaderBoard",width*0.5f,height*0.45f);
    fill(112,128,144);
    rect(width*0.45f, height*0.5f, width*0.1f, height*0.1f);
    fill(206, 235, 251);
    textSize(width*0.05f);
    text("Delete Account",width*0.5f,height*0.65f);
    fill(112,128,144);
    rect(width*0.45f, height*0.7f, width*0.1f, height*0.1f);
    strokeWeight(0);
  }

  private void topMenu(){
    background(this.menuImage);
    fill(206, 235, 251);
    triangle(width*0.05f, 0, 0, height*0.025f, width*0.05f, height*0.05f);
    textSize(width*0.1f);
    text("Page " + (this.topMinLimit/8+1), width*0.5f, height*0.025f);
    textSize(width*0.05f);
    for(int i=this.topMinLimit; i<topMaxLimit; i++){
      text(this.topNames.get(i)+ " " + this.topLevels.get(i), width*0.5f, height*0.2f+height*0.06f*(i%8));
    }

    if(this.topMaxLimit < this.topNames.size())
      triangle(width*0.95f, (float) height, (float) width, height*0.975f, width*0.95f, height*0.95f);
    if(this.topMinLimit>=7)
      triangle(width*0.05f, (float) height, 0, height*0.975f, width*0.05f, height*0.95f);
  }

  private void waitingMenu(){
    background(this.menuImage);
    fill(206, 235, 251);
    triangle(width*0.05f, 0, 0, height*0.025f, width*0.05f, height*0.05f);
    strokeWeight(10);
    fill(75,37,109);
    rect(width*0.45f, height*0.5f, width*0.1f, height*0.1f);
    textSize(width*0.05f);
    fill(188, 255, 18);
    textAlign(CENTER, CENTER);
    text("Username: "+this.user.getText(),width*0.5f,height*0.15f);
    text("Level: " + this.level,width*0.5f,height*0.2f);

    textSize(width*0.08f);
    if(isReady){
      strokeWeight(8);
      fill(50,205,50);
      text("Ready", width*0.5f,  height*0.42f);
      fill(0);
      line(width*0.45f, height*0.5f, width*0.45f+width*0.1f, height*0.5f+height*0.1f);
      line(width*0.45f+height*0.1f, height*0.5f, width*0.45f, height*0.5f+height*0.1f);
    } else {
      fill(220,20,60);
      text("Not Ready", width*0.5f,  height*0.42f);
    }
    strokeWeight(0);
    GameState gameCopy;
    this.gameState.lrw.writeLock().lock();
    try {
      gameCopy = this.gameState.copy();
    } finally {
      this.gameState.lrw.writeLock().unlock();
    }
    if(Objects.equals(gameCopy.gameStatus, "h")) {
      this.menuImage = loadImage("images/loading.jpg");
      this.menu = "loadingMenu";
    } else if(Objects.equals(gameCopy.gameStatus, "s")){
      this.menu = "game";
      this.gameState.point = "0";
      this.gameState.enemyPoint = "0";
      this.gameState.boxes = new HashSet<>();
      this.isInGame = true;
    } else if(Objects.equals(gameCopy.gameStatus, "a")){
      this.gameState.lrw.readLock().lock();
      try{
        this.gameState.gameStatus = "i";
      } finally {
        this.gameState.lrw.readLock().unlock();
      }
      this.isReady = false;
    }
  }

  private void loadingMenu(){
    background(this.menuImage);
    text("Loading", width*0.5f, height*0.5f);
    GameState gameCopy;
    this.gameState.lrw.writeLock().lock();
    try {
      gameCopy = this.gameState.copy();
    } finally {
      this.gameState.lrw.writeLock().unlock();
    }
    if(Objects.equals(gameCopy.gameStatus, "s")) {
      this.menuImage = loadImage("images/space3.jpg");
      this.menu = "game";
      this.gameState.point = "0";
      this.gameState.enemyPoint = "0";
      this.gameState.boxes = new HashSet<>();
      this.isInGame = true;
    } else if(Objects.equals(gameCopy.gameStatus, "a")){
      this.isReady = false;
      this.menu = "waitingMenu";
    }
  }

  private void deleteMenu(){
    background(this.menuImage);
    textAlign(CENTER, CENTER);
    fill(206, 235, 251);
    textSize(width*0.1f);
    text("Confirm Password",width/2.0f,height*0.05f);
    this.password.draw();
    fill(206, 235, 251);
    triangle(width*0.05f, 0, 0, height*0.025f, width*0.05f, height*0.05f);
    fill(112,128,144);
    rect(width*0.45f, height*0.65f, width*0.1f, height*0.1f);
    textSize(width*0.05f);
    fill(255,160,122);
    text(message, width*0.3f, height*0.80f);
  }

  private void game() throws IOException{
    GameState gameDraw;
    this.gameState.lrw.writeLock().lock();
    try {
      gameDraw = this.gameState.copy();
    } finally {
      this.gameState.lrw.writeLock().unlock();
    }

    String[] statusArg = gameDraw.gameStatus.split(":", 2);
    if(Objects.equals(statusArg[0], "w")) {
      background(this.menuImage);
      textSize(width*0.1f);
      fill(255,255,0);
      text("Victory", width * 0.5f, height * 0.5f);
      this.isInGame = false;
      this.level = Integer.parseInt(statusArg[1]);
      fill(206, 235, 251);
      triangle(width*0.05f, 0, 0, height*0.025f, width*0.05f, height*0.05f);
    } else if(Objects.equals(statusArg[0], "l")){
      background(this.menuImage);
      textSize(width*0.1f);
      fill(255,0,0);
      text("Defeat", width*0.5f, height*0.5f);
      this.isInGame = false;
      fill(206, 235, 251);
      triangle(width*0.05f, 0, 0, height*0.025f, width*0.05f, height*0.05f);
    } else {
      background(this.menuImage);
      fill(119,136,153);
      rect(width*0.0625f,height*0.1f, 700, 700);
      textSize(width*0.05f);
      if(Objects.equals(gameDraw.gameStatus, "g"))
        fill(255, 255, 0);
      else
        fill(255);
      text(gameDraw.point + ":" + gameDraw.enemyPoint, width*0.5f, height*0.05f);

      fill(255, 16, 240);
      circle(width*0.0625f + gameDraw.posX, height*0.1f + gameDraw.posY, 30);
      fill(251, 255, 22);
      circle(width*0.0625f + gameDraw.enemyPosX, height*0.1f+ gameDraw.enemyPosY, 30);

      strokeWeight(6);
      fill(0);
      pushMatrix();
      translate(width*0.0625f + gameDraw.posX, height*0.1f + gameDraw.posY);
      rotate(gameDraw.alfa);
      line(0,0, 13.5f, 0);
      popMatrix();
      pushMatrix();
      translate(width*0.0625f + gameDraw.enemyPosX, height*0.1f + gameDraw.enemyPosY);
      rotate(gameDraw.enemyAlfa);
      line(0,0, 13.5f, 0);
      popMatrix();
      strokeWeight(0);

      Triple triple;
      for (Triple box : gameDraw.boxes) {
        triple = this.colorMap.get(box.chars[0]);
        fill(triple.floats[0], triple.floats[1], triple.floats[2]);
        circle(width*0.0625f + box.floats[0], height*0.1f + box.floats[1], 30);
      }

      if(this.moving.changed && !(this.moving.keys[0].equals(this.moving.keys[2]) && this.moving.keys[0].equals("t"))){
        this.connectionManager.send("move", this.moving.getMessage());
        this.moving.changed = false;
      }
    }
  }

  public void draw(){
    try {
      this.getClass().getDeclaredMethod(this.menu).invoke(this);
    } catch (Exception e){
      e.printStackTrace();
      exit();
    }
  }

  public void settings() {
    size(800, 800);
  }

  private float triangleArea(float x1, float y1, float x2, float y2, float x3, float y3){
    return Math.abs((x1*(y2-y3) + x2*(y3-y1)+ x3*(y1-y2))/2.0f);
  }

  private boolean isInsideTriangle(float x1, float y1, float x2, float y2, float x3, float y3){
    return triangleArea(x1, y1, x2, y2, x3, y3) ==
            triangleArea(mouseX, mouseY, x2, y2, x3, y3) +
                    triangleArea(x1, y1, mouseX, mouseY, x3, y3) +
                    triangleArea(x1, y1, x2, y2, mouseX, mouseY);
  }

  private boolean isInsideBox(float x, float y, float width, float height){
    return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
  }

  public void mousePressed(){
    if(!this.isInGame){
      if(Objects.equals(this.menu, "game")){
        if(isInsideTriangle(width*0.05f, 0, 0, width*0.025f, width*0.05f, width*0.05f)) {
          this.menuImage = loadImage("images/space2.jpg");
          this.isReady = false;
          this.menu = "waitingMenu";
        }
      }
      else if(Objects.equals(this.menu, "startMenu")){
        if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.3f && mouseY < height*0.3f + height*0.1f){
          this.menu = "logRegMenu";
          this.registerMenu = false;
        } else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.5f && mouseY < height*0.5f + height*0.1f){
          this.menu = "logRegMenu";
          this.registerMenu = true;
        } else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.7f && mouseY < height*0.7f + height*0.1f){
          try {
            this.connectionManager.close();
            for (Communicator communicator : this.communicators) {
              communicator.join();
            }

          } catch (java.io.IOException|java.lang.InterruptedException e){
            e.printStackTrace();
          }
          exit();
        }
      } else if(Objects.equals(this.menu, "logRegMenu")){
        this.user.select(mouseX, mouseY);
        this.password.select(mouseX,mouseY);
        if(isInsideTriangle(width*0.05f, 0, 0, width*0.025f, width*0.05f, width*0.05f)) {
          this.message = "";
          this.user.reset();
          this.password.reset();
          this.menu = "startMenu";
        }
        else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.65f && mouseY < height*0.65f + height*0.1f){
          String username = this.user.getText();
          String password = this.password.getText();

          if(this.registerMenu){
            try {
              this.connectionManager.send("register", username + ":" + password);
            } catch (IOException e){
              e.printStackTrace();
            }
            try {
              this.message = this.connectionManager.receive("register");
            } catch (IOException|InterruptedException e){
              e.printStackTrace();
            }
          } else {
            try {
              this.connectionManager.send("login", username + ":" + password);
            } catch (IOException e){
              e.printStackTrace();
            }
            try {
              this.message = this.connectionManager.receive("login");
            } catch (IOException|InterruptedException e){
              e.printStackTrace();
            }
            String[] messageArgs = this.message.split(":", 2);
            if(Objects.equals(messageArgs[0], "ok")) {
              this.level = Integer.parseInt(messageArgs[1]);
              this.menuImage = loadImage("images/space2.jpg");
              this.menu = "loggedMenu";
            }
          }
        }
      } else if(Objects.equals(this.menu, "waitingMenu")){
        if(isInsideTriangle(width*0.05f, 0, 0, width*0.025f, width*0.05f, width*0.05f)) {
          if(this.isReady) {
            try {
              this.connectionManager.send("ready", Boolean.toString(false));
            } catch (IOException e) {
              e.printStackTrace();
            }
            try {
              this.message = this.connectionManager.receive("ready");
            } catch (IOException | InterruptedException e) {
              e.printStackTrace();
            }
            if (Objects.equals(this.message, "ok")) {
              this.isReady = false;
              this.menu = "loggedMenu";
            }
          } else {
            this.menu = "loggedMenu";
          }
        }
        else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.5f && mouseY < height*0.5f + height*0.1f){
          try{
            this.connectionManager.send("ready", Boolean.toString(!this.isReady));
          } catch (IOException e){
            e.printStackTrace();
          }
          try{
            this.message = this.connectionManager.receive("ready");
          } catch (IOException|InterruptedException e){
            e.printStackTrace();
          }
          if(Objects.equals(this.message, "ok"))
            this.isReady = !this.isReady;
        }
      } else if(Objects.equals(this.menu, "loggedMenu")){
        if(isInsideTriangle(width*0.05f, 0, 0, width*0.025f, width*0.05f, width*0.05f)) {
          try {
            this.connectionManager.send("logout", "");
          } catch (IOException e){
            e.printStackTrace();
          }

          try{
            this.message = this.connectionManager.receive("logout");
          } catch (IOException|InterruptedException e){
            e.printStackTrace();
          }

          if(Objects.equals(this.message, "ok")) {
            this.message = "";
            this.user.reset();
            this.password.reset();
            this.menuImage = loadImage("images/space.jpg");
            this.menu = "startMenu";
          }
        } else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.3f && mouseY < height*0.3f + height*0.1f){
          this.isReady = false;
          this.menu = "waitingMenu";
        } else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.5f && mouseY < height*0.5f + height*0.1f){
          try {
            this.topMinLimit = 0;
            this.connectionManager.send("top", "");
            String leaders = this.connectionManager.receive("top");
            this.topNames = new ArrayList<>();
            this.topLevels = new ArrayList<>();
            if(!Objects.equals(leaders, "")){
              String[] namesWin = leaders.split(":");
              this.topMaxLimit = Math.min(8, namesWin.length);
              for(String nameWin: namesWin) {
                String[] stats = nameWin.split("_");
                this.topNames.add(stats[0]);
                this.topLevels.add(stats[1]);
              }
            }
          } catch (IOException|InterruptedException e){
            e.printStackTrace();
          }
          this.menu = "topMenu";
        } else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.7f && mouseY < height*0.7f + height*0.1f){
          this.password.reset();
          this.message = "";
          this.menu = "deleteMenu";
        }
      } else if(Objects.equals(this.menu, "topMenu")){
        if(isInsideTriangle(width*0.05f, 0, 0, width*0.025f, width*0.05f, width*0.05f)){
          this.menu = "loggedMenu";
        } else if(this.topMaxLimit < this.topNames.size() && isInsideTriangle(width*0.95f, (float) height, (float) width, height*0.975f, width*0.95f, height*0.95f)){
          this.topMinLimit += 8;
          this.topMaxLimit = Math.min(this.topMaxLimit+8, this.topNames.size());
        } else if(this.topMinLimit>=8 && isInsideTriangle(width*0.05f, (float) height, 0, height*0.975f, width*0.05f, height*0.95f)){
          this.topMaxLimit = Math.max(this.topMaxLimit-8, 8);
          this.topMinLimit -= 8;
        }
      } else if(Objects.equals(this.menu, "deleteMenu")){
        this.password.select(mouseX,mouseY);
        if(isInsideBox(width*0.45f, height*0.65f, width*0.1f, height*0.1f)){
          try{
            this.connectionManager.send("close",password.getText());
          } catch (IOException e){
            e.printStackTrace();
          }
          try{
            this.message = this.connectionManager.receive("close");
          } catch (IOException|InterruptedException e){
            e.printStackTrace();
          }
          if(Objects.equals(this.message, "ok")){
            this.message = "";
            this.user.reset();
            this.password.reset();
            this.menuImage = loadImage("images/space.jpg");
            this.menu = "startMenu";
          }
        } else if(isInsideTriangle(width*0.05f, 0, 0, width*0.025f, width*0.05f, width*0.05f)){
          this.menu = "loggedMenu";
        }
      }
    }
  }

  public void keyPressed(){
    if(isInGame) {
      switch (key) {
        case ('a') -> this.moving.keyPressed(0);
        case ('w') -> this.moving.keyPressed(1);
        case ('d') -> this.moving.keyPressed(2);
      }
    }else{
      if(this.user.active){
        this.user.keyPressed(key);
      } else if(this.password.active){
        this.password.keyPressed(key);
      }
    }
  }

  public void keyReleased(){
    switch (this.key) {
      case ('a') -> this.moving.keyReleased(0);
      case ('w') -> this.moving.keyReleased(1);
      case ('d') -> this.moving.keyReleased(2);
    }
  }

  public static void main(String[] args) {
    if(args.length < 2)
      System.exit(1);

    String host = args[0];
    int port = Integer.parseInt(args[1]);

    try{
      Socket socket = new Socket(host, port);
      ConnectionManager cm = ConnectionManager.start(socket);

      String[] processingArgs = {"Processing"};
      PApplet.runSketch(processingArgs, new Processing(cm));
    } catch(Exception e){
      e.printStackTrace();
      System.exit(0);
    }
  }
}
