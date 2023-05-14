/* autogenerated by Processing revision 1292 on 2023-05-10 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Processing extends PApplet{

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

  private textBox user, password;
  private PImage menuImage;

  private String menu;

  private String message;
  private boolean isInGame;

  private int level;

  private boolean registerMenu, isReady;
public void setup(){
  frameRate(30);
  this.menuImage = loadImage("images/space.jpg");
  this.menu = "startMenu";
  this.isInGame = false;
  this.message = "";
  this.user = new textBox(0, height*0.3f, width, width*0.1f,"Username:");
  this.password = new textBox(0, height*0.45f, width, width*0.1f,"Password:");
  this.isReady = false;
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
  text("Exit",width*0.5f,height*0.65f); // Botão não funcional ainda
  fill(112,128,144);
  rect(width*0.45f, height*0.7f, width*0.1f, height*0.1f);
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
  fill(255,160,122);
  text(message, width*0.5f, height*0.80f);
  fill(206, 235, 251);
  triangle(width*0.05f, 0, 0, width*0.025f, width*0.05f, width*0.05f);
  fill(112,128,144);
  rect(width*0.45f, height*0.65f, width*0.1f, height*0.1f);
}

private void waitingMenu(){
  background(this.menuImage);
  strokeWeight(10);
  fill(75,37,109);
  rect(width*0.45f, height*0.5f, width*0.1f, height*0.1f);
  textSize(width*0.05f);
  fill(188, 255, 18);
  text("Username: "+this.user.getText(),width*0.45f,height*0.15f);
  text("Level: " + this.level,width*0.45f,height*0.2f);
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
}

private void game(){
  background(this.menuImage);
}

public void draw(){
  try {
    this.getClass().getDeclaredMethod(this.menu).invoke(this);
  } catch (Exception e){e.printStackTrace();}
}

  public void settings() {
    size(800, 800);
  }

  private float triangleArea(float x1, float y1, float x2, float y2, float x3, float y3){
    return Math.abs((x1*(y2-y3) + x2*(y3-y1)+ x3*(y1-y2))/2.0f);
  }

  public void mousePressed(){
    if(!isInGame){
      if(Objects.equals(this.menu, "startMenu")){
        this.user.reset();
        this.password.reset();
        if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.3f && mouseY < height*0.3f + height*0.1f){
          this.menu = "logRegMenu";
          this.registerMenu = false;
        } else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.5f && mouseY < height*0.5f + height*0.1f){
          this.menu = "logRegMenu";
          this.registerMenu = true;
        }
      } else if(Objects.equals(this.menu, "logRegMenu")){
        this.user.select(mouseX, mouseY);
        this.password.select(mouseX,mouseY);
        if(triangleArea(width*0.05f, 0, 0, width*0.025f, width*0.05f, width*0.05f) ==
          triangleArea(mouseX, mouseY, 0, width*0.025f, width*0.05f, width*0.05f) +
          triangleArea(width*0.05f, 0, mouseX, mouseY, width*0.05f, width*0.05f) +
          triangleArea(width*0.05f, 0, 0, width*0.025f, mouseX, mouseY))
            this.menu = "startMenu";
        else if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.65f && mouseY < height*0.65f + height*0.1f){
          String username = this.user.getText();
          String password = this.password.getText();
          this.menuImage = loadImage("images/space2.jpg");
          this.menu = "waitingMenu";
        }
      } else if(Objects.equals(this.menu, "waitingMenu")){
        if(mouseX > width*0.45f && mouseX < width*0.45f + width*0.1f && mouseY > height*0.5f && mouseY < height*0.5f + height*0.1f){
          this.isReady = !this.isReady;
          this.menuImage = loadImage("images/space3.jpg");
          this.menu = "game";
        }
      }
    }
  }

  public void keyPressed(){
    if(isInGame) {
      switch (this.key) {
        case ('a'):
          //new Communicator();
          break;

        case ('w'):
          break;

        case ('d'):
          break;

      }
    } else{
      if(this.user.active){
        this.user.keyPressed(key);
      } else if(this.password.active){
        this.password.keyPressed(key);
      }
    }
  }

  public static void main(String[] args) {
    //if(args.length < 2)
    //    System.exit(1);

    //String host = args[0];
    //int port = Integer.parseInt(args[1]);

    try{
      //Socket socket = new Socket(host, port);
      //ConnectionManager cm = ConnectionManager.start(socket);

      String[] processingArgs = {"Processing"};
      PApplet.runSketch(processingArgs, new Processing());
    } catch(Exception e){
      e.printStackTrace();
      System.exit(0);
    }
  }
}
