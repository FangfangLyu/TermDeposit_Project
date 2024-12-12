package target.classes.com.termdeposit.model;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.io.File;

public class UserView extends JFrame{
    private JButton button;
    private JLabel label;
    private JMenu file, functions;
    private JMenuBar menuBar;
    private JMenuItem input,exit,predict,calc;

    private int serviceInputCount;
    private List inputUI;

    public UserView() {
        setTitle("Term Deposit Predictor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300,200);
        setLayout(null);

        menuBar = new JMenuBar();

        file = new JMenu("File");

        input = new JMenuItem("Input");
        input.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText("File Open");
            }
        });

        exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        file.add(input);
        file.add(exit);
        menuBar.add(file);

        functions = new JMenu("Functions");
        
        predict = new JMenuItem("Predict");
        predict.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText("Predict Started");
            }
        });

        calc = new JMenuItem("Calculator");
        calc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText("Calc Pressed");
            }
        });

        functions.add(predict);
        functions.add(calc);
        menuBar.add(functions);

        button = new JButton("temp");
        button.setBounds(100,50,100,30);
        add(button);

        label = new JLabel("temp");
        label.setBounds(100,100,100,30);
        add(label);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText("Button Clicked");
            }
        });

        setJMenuBar(menuBar);
        setVisible(true);
    }

    public static void main (String[] args) {
        new UserView();
    }
}