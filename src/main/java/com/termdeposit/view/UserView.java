package com.termdeposit.view;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.io.File;

public class UserView extends JFrame{
    private JLabel label;
    private JMenu file, functions;
    private JMenuBar menuBar;
    private JMenuItem input,exit,predict,calc;

    private JFrame calcScreen;
    private JButton calcGrowth, calcMinGain;

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
                calcScreen = new JFrame();
                calcScreen.setTitle("Deposit Growth Calculator");
                calcScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                calcScreen.setSize(300,200);
                calcScreen.setLayout(null);

                calcGrowth = new JButton("Growth");
                calcGrowth.setBounds(100,50,100,30);
                calcScreen.add(calcGrowth);

                calcGrowth.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        label.setText("calcGrowth Clicked");
                    }
                });

                calcMinGain = new JButton("Minimum Gain");
                calcMinGain.setBounds(100,50,100,30);
                calcScreen.add(calcMinGain);

                calcMinGain.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        label.setText("calcMinGain Clicked");
                    }
                });

                calcScreen.setVisible(true);
            }
        });

        functions.add(predict);
        functions.add(calc);
        menuBar.add(functions);

        label = new JLabel("temp");
        label.setBounds(100,100,100,30);
        add(label);

       

        setJMenuBar(menuBar);
        setVisible(true);
    }


}