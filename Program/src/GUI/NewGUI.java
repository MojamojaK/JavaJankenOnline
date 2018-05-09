package Client;

import java.util.*;
import Client.ClientCommunication;
import Utility.Commands;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class NewGUI extends JFrame implements ActionListener {
    
    public Scanner sc;
    private boolean stopped = false;
    public LinkedList<Character> inbox = new LinkedList<>();
    
    public static void main(String[] args) {
        NewGUI frame = new NewGUI("ジャンケン");
        frame.setVisible(true);

    }

    JButton button1;
    JButton button2;
    JButton button3;
    JButton button4;

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            System.out.println("g");
            addChar('g');
            //dispose();
            //System.exit(0);
        } else if (e.getSource() == button2) {
             System.out.println("c");
            addChar('c');
             //dispose();
             //System.exit(0);
        } else if (e.getSource() == button3) {
            System.out.println("p");
            addChar('p');
            //dispose();
            //System.exit(0); 
        }
    }

    public NewGUI(String title) {     //String title
        {
            setTitle(title);

            setBounds(100, 100, 900, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel labelSubTitle = new JLabel("どれかボタンを押してね");
            super.add("North", labelSubTitle);

            JPanel p = new JPanel();

            ImageIcon icon1 = new ImageIcon("images/rock.jpg");
            button1 = new JButton("rock", icon1);
            ImageIcon icon2 = new ImageIcon("images/scissors.jpg");
            button2 = new JButton("scissors", icon2);
            ImageIcon icon3 = new ImageIcon("images/paper.jpg");
            button3 = new JButton("paper", icon3);
            button4 = new JButton("Start");

            button1.addActionListener(this);
            button2.addActionListener(this);
            button3.addActionListener(this);
            button4.addActionListener(this);

            p.add(button1);
            p.add(button2);
            p.add(button3);
            p.add(button4);


            Container contentPane = getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
        }

    }
    
    public synchronized int inboxSize() {
        return inbox.size();
    }
    
    public synchronized void addChar(char c) {
        inbox.add(c);
    }
    
    public synchronized char getChar() {
        return inbox.pollLast();
    }
    
    public void stop() {
        stopped = true;
    }
    
    public void close() {
        this.sc.close();
    }
}
