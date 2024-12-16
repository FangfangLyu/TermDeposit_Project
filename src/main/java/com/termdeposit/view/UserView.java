package com.termdeposit.view;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.event.*;
import java.util.List;
import java.io.File;

public class UserView extends JFrame {
    // new

    // CardLayout object to allow switching between panels (screens)
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // screens to be switched out
    private JPanel mainScreen;
    private JPanel trainScreen;
    private JPanel predictScreen;
    private JPanel addServiceScreen;

    // old
    // private JButton openButton;
    // private JFileChooser fileChooser;
    // private JLabel label;
    // private JMenu file, functions;
    // private JMenuBar menuBar;
    // private JMenuItem input, exit, predict, calc;

    // private JFrame calcScreen;
    // private JButton calcGrowth, calcMinGain;

    // private int serviceInputCount;
    // private List inputUI;

    public UserView() {
        setTitle("Term Deposit Prediction Model");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // set up cardlayout and main screen (screens will switch out on this)
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel();

        // set up four screens that will be switched out when needed
        this.mainScreen = createMainScreen();
        this.trainScreen = createTrainScreen();
        this.predictScreen = createPredictScreen();
        this.addServiceScreen = createAddServiceScreen();

        mainPanel.add(mainScreen, "MainScreen");
        mainPanel.add(trainScreen, "TrainScreen");
        mainPanel.add(predictScreen, "PredictScreen");
        mainPanel.add(addServiceScreen, "AddServiceScreen");

        add(mainPanel);

        cardLayout.show(mainPanel, "MainScreen");
    }

    private JPanel createMainScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel instructionLabel = new JLabel("Welcome to Term Deposit Predictor.\nPlease select an option.",
                JLabel.CENTER);
        JButton trainButton = new JButton("Train Model");
        trainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "TrainScreen");
            }
        });

        panel.add(instructionLabel, BorderLayout.NORTH);
        panel.add(trainButton, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTrainScreen(Manager manager) {

    }

    private JPanel createPredictScreen() {

    }

    private JPanel createAddServiceScreen() {

    }

    // TODO: update render() function in class diagram to this instead
    public void updateMainScreen(boolean allowPrediction, boolean allowAddService) {

    }

    // add action listener methods for buttons
    public void addTrainDefaultListener(ActionListener actionListener) {
        trainDefaultButton.addActionListener(actionListener);
    }

    public void addTrainCustomListener(ActionListener actionListener) {
        // actionListener should trigger upload operation
        trainCustomButton.addActionListener(actionListener);
    }

    // menuBar = new JMenuBar();

    // file = new JMenu("File");

    // input = new JMenuItem("Input");
    // input.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent e) {

    // openButton = new JButton("Select a CSV File");
    // openButton.setBounds(100, 100, 150, 50);
    // openButton.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // fileChooser = new JFileChooser();
    // fileChooser.setDialogTitle("Select a CSV File");
    // fileChooser
    // .setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV
    // Files", "csv"));

    // int userSelectedFile = fileChooser.showOpenDialog(null);
    // if (userSelectedFile == JFileChooser.APPROVE_OPTION) {
    // File csvFile = fileChooser.getSelectedFile();
    // if (csvFile.getName().toLowerCase().endsWith(".csv")) {
    // // process csvFile using manager class
    // } else {
    // JOptionPane.showMessageDialog(null, "Please select a valid CSV file.",
    // "Invalid File",
    // JOptionPane.ERROR_MESSAGE);
    // }

    // }
    // }
    // });
    // add(openButton);
    // }
    // });

    // exit = new JMenuItem("Exit");
    // exit.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // System.exit(0);
    // }
    // });

    // file.add(input);
    // file.add(exit);
    // menuBar.add(file);

    // functions = new JMenu("Functions");

    // predict = new JMenuItem("Predict");
    // predict.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // label.setText("Predict Started");
    // }
    // });

    // calc = new JMenuItem("Calculator");
    // calc.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // label.setText("Calc Pressed");
    // calcScreen = new JFrame();
    // calcScreen.setTitle("Deposit Growth Calculator");
    // calcScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // calcScreen.setSize(300, 200);
    // calcScreen.setLayout(null);

    // calcGrowth = new JButton("Growth");
    // calcGrowth.setBounds(100, 50, 100, 30);
    // calcScreen.add(calcGrowth);

    // calcGrowth.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // label.setText("calcGrowth Clicked");
    // }
    // });

    // calcMinGain = new JButton("Minimum Gain");
    // calcMinGain.setBounds(100, 100, 100, 30);
    // calcScreen.add(calcMinGain);

    // calcMinGain.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // label.setText("calcMinGain Clicked");
    // }
    // });

    // calcScreen.setVisible(true);
    // }
    // });

    // functions.add(predict);
    // functions.add(calc);
    // menuBar.add(functions);

    // label = new JLabel("");
    // label.setBounds(100, 50, 100, 30);
    // add(label);

    // setJMenuBar(menuBar);
    // setVisible(true);
    // }

}