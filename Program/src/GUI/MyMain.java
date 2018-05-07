//package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyMain{
  public static void main(String[] args){
    NewGUI2 frame = new NewGUI2("ジャンケン");
    frame.setVisible(true);
    System.out.println(frame.input);   //ここがaになってしまう
  }
}
