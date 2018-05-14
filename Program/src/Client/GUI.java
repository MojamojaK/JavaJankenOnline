package Client;
//package Client;

import Client.ClientCommunication;
import Utility.Commands;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.LinkedList;


public class GUI extends JFrame implements ActionListener {

    private JButton startButton;

    private JButton button1;
    private JButton button2;
    private JButton button3;

    private LinkedList<Character> inbox = new LinkedList<>();

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            System.out.println("g");
            addChar(Commands.Rock);
            //System.exit(0);
        } else if (e.getSource() == button2) {
            System.out.println("c");
            addChar(Commands.Scissors);
            //System.exit(0);
        } else if (e.getSource() == button3) {
            System.out.println("p");
            addChar(Commands.Paper);
            //System.exit(0);
        } else if (e.getSource() == startButton){
            addChar(Commands.Start);
        }
    }
    

    public GUI(String title) throws IOException {
        {
            String[] args = new String[1];
            args[0] = "localhost";

            setTitle(title);

            setBounds(100, 100, 900, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel labelSubTitle = new JLabel("どれかボタンを押してね");
            super.add("North", labelSubTitle);

            JPanel p = new JPanel();

            startButton = new JButton("Start Game");

            ImageIcon icon1 = new ImageIcon("./../images/rock.jpg");
            button1 = new JButton("rock", icon1);
            ImageIcon icon2 = new ImageIcon("./../images/scissors.jpg");
            button2 = new JButton("scissors", icon2);
            ImageIcon icon3 = new ImageIcon("./../images/paper.jpg");
            button3 = new JButton("paper", icon3);

            button1.addActionListener(this);
            button2.addActionListener(this);
            button3.addActionListener(this);
            startButton.addActionListener(this);

            p.add(button1);
            p.add(button2);
            p.add(button3);
            p.add(startButton);

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
}
