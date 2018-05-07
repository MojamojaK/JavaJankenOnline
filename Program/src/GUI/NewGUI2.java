//package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class NewGUI2 extends JFrame implements ActionListener {
    /*public static void main(String[] args) {
        NewGUI2 frame = new NewGUI2("ジャンケン");
        frame.setVisible(true);

    }*/
    char input='a';

    JButton button1;
    JButton button2;
    JButton button3;
    

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            System.out.println("g");
            input='g';
            setVisible(false);
            dispose();
            //System.exit(0);
        } else if (e.getSource() == button2) {
            System.out.println("c");
            input='c';
            setVisible(false);
             dispose();
             //System.exit(0);
        } else if (e.getSource() == button3) {
            System.out.println("p");
            input='p';
            setVisible(false);
            dispose();
            //System.exit(0);
        }
    }
    

    public NewGUI2(String title) {
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

            button1.addActionListener(this);
            button2.addActionListener(this);
            button3.addActionListener(this);

            p.add(button1);
            p.add(button2);
            p.add(button3);

            Container contentPane = getContentPane();
            contentPane.add(p, BorderLayout.CENTER);
        }

    }
}
