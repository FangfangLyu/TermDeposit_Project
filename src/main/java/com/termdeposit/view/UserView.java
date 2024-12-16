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
    // buttons for user to select if they want to train model on default dataset
    // or on their own dataset via csv file
    private JButton trainDefaultButton, trainCustomButton;
    private JLabel instructionsLabel;
    private JButton predictionButton;
    private JButton addServiceButton;

    // CardLayout object to allow switching between panels (screens)
    private CardLayout cardLayout;
    private JPanel mainPanel

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

        // set up buttons and labels
        this.trainDefaultButton = new JButton("Train Model from Default Dataset");
        this.trainCustomButton = new JButton("Train Model from Custom Dataset");
        this.instructionsLabel = new JLabel(
                "Select an option to train a model off of our Kaggle dataset or your own dataset.", JLabel.CENTER);

        // set up cardlayout and main screen (screens will switch out on this)
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel();

        // set up four screens that will be switched out when needed
        mainPanel.add(createMainScreen(), "MainScreen");
        mainPanel.add(createTrainScreen(), "TrainScreen");
        mainPanel.add(createPredictScreen(), "PredictScreen");
        mainPanel.add(createAddServiceScreen(), "AddServiceScreen");

        // set up panel to hold buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.add(trainDefaultButton);
        panel.add(trainCustomButton);

        // add panel and instructions to JFrame
        add(panel, BorderLayout.CENTER);
        add(instructionsLabel, BorderLayout.NORTH);
    }

    private JPanel createMainScreen() {

    }

    private JPanel createTrainScreen() {

    }

    private JPanel createPredictScreen() {

    }

    private JPanel createAddServiceScreen() {

    }

    // add action listener methods for buttons
    public void addTrainDefaultListener(ActionListener actionListener) {
        trainDefaultButton.addActionListener(actionListener);
    }

    public void addTrainCustomListener(ActionListener actionListener) {
        // actionListener should trigger upload operation
        trainCustomButton.addActionListener(actionListener);
    }

    // TODO: update render() in class diagram
    public void render(String message, boolean allowPrediction, boolean allowAddService) {
        this.instructionsLabel = new JLabel(message);

        if (allowPrediction) {
            this.predictionButton = new JButton("Start Prediction");
        }

        if (allowAddService) {
            this.addServiceButton = new JButton("Additional Service");
        }
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