package com.termdeposit.view;

import javax.swing.*;
import javax.swing.border.Border;

import com.termdeposit.controller.Manager;

import javafx.scene.shape.Path;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.event.*;
import java.util.List;
import java.util.HashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class UserView extends JFrame {
    // new

    // CardLayout object to allow switching between panels (screens)
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // screens to be switched out
    private JPanel mainScreen1;
    private JPanel mainScreen2;
    private JPanel trainScreen;
    private JPanel predictScreen;
    private JPanel addServiceScreen;

    private ActionListener quitListener;

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

    public UserView(Manager manager) {
        setTitle("Term Deposit Prediction Model");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // set up cardlayout and main screen (screens will switch out on this)
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        this.manager = manager;


        // set up four screens that will be switched out when needed
        this.mainScreen1 = createMainScreen();
        this.mainScreen2 = updateMainScreen(true);
        this.trainScreen = createTrainScreen(manager);
        this.predictScreen = createPredictScreen(manager);
        this.addServiceScreen = createAddServiceScreen();

        mainPanel.add(mainScreen1, "MainScreen1");
        mainPanel.add(mainScreen2, "MainScreen2");
        mainPanel.add(trainScreen, "TrainScreen");
        mainPanel.add(predictScreen, "PredictScreen");
        mainPanel.add(addServiceScreen, "AddServiceScreen");

        add(mainPanel);

        cardLayout.show(mainPanel, "MainScreen1");

        this.quitListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };

    }

    private ActionListener createMoveListener(String screenName) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, screenName);
            }
        };
    }

    private JPanel createMainScreen() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JLabel instructionLabel = new JLabel("Welcome to Term Deposit Predictor.\nPlease select an option.",
                JLabel.CENTER);

        JButton trainButton = new JButton("Train Model");
        JButton quitButton = new JButton("Quit Program");

        // add actionListener for moving to training screen
        trainButton.addActionListener(createMoveListener("TrainScreen"));

        // add actionListener for quitting program
        quitButton.addActionListener(quitListener);

        panel.add(instructionLabel);
        panel.add(trainButton);
        panel.add(quitButton);

        return panel;
    }

    private JPanel createTrainScreen(Manager manager) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 15));

        JLabel label = new JLabel("Select a Training Option", JLabel.CENTER);

        // go back to previous screen, allowing user to access Quit
        JButton previousButton = new JButton("Previous Screen");
        previousButton.addActionListener(createMoveListener("MainScreen1"));

        JButton trainDefaultButton = new JButton("Train From Default CSV");
        trainDefaultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String defaultFileUrl = "data/train.csv";
                //defaultFileUrl = "data/fakeTrain.csv";
                System.out.println(defaultFileUrl);
                try {
                    manager.startImputation(true, true, defaultFileUrl); // trainingData, not testing

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Training model on default data failed.");
                    ex.printStackTrace();
                }

                endTrainingScreen();

            }

        });

        JButton trainCustomButton = new JButton("Train From Upload CSV");
        trainCustomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // let user know about features to contain in CSV
                HashMap<String, String> featureDatatype = manager.getFeatureList();
                String message = createFieldMessage(featureDatatype);

                JOptionPane.showMessageDialog(null, "CSV file should contain the following fields:\n" + message,
                        "CSV header information", JOptionPane.INFORMATION_MESSAGE);

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select a CSV file for training");

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {

                    // get the selected file
                    File selectedFile = fileChooser.getSelectedFile();

                    try {
                        manager.startImputation(false, true, selectedFile.getAbsolutePath()); // trainingData on the
                                                                                              // absolute path

                    } catch (IOException ex) {
                        // Handle file I/O error
                        JOptionPane.showMessageDialog(null, "Failed to save the file to a temporary location", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        // Handle other exceptions
                        JOptionPane.showMessageDialog(null, "Failed to train model", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }

                    endTrainingScreen();
                }
                cardLayout.show(mainPanel, "PredictScreen");

            }
        });

        panel.add(label);
        panel.add(trainDefaultButton);
        panel.add(trainCustomButton);
        panel.add(previousButton);

        return panel;
    }

    private void endTrainingScreen() {
        JOptionPane.showMessageDialog(null, "Predictive model trained and validated.\nMoving to main screen...");
        this.cardLayout.show(mainPanel, "mainScreen2");
    }

    private JPanel createPredictScreen(Manager manager) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(16, 2, 10, 10)); // Grid layout for form fields

        // Labels for each input field
        JLabel ageLabel = new JLabel("Age:");
        JLabel jobLabel = new JLabel("Job:");
        JLabel maritalLabel = new JLabel("Marital Status:");
        JLabel educationLabel = new JLabel("Education:");
        JLabel defaultLabel = new JLabel("Has Credit Default?");
        JLabel balanceLabel = new JLabel("Balance:");
        JLabel housingLabel = new JLabel("Has Housing Loan?");
        JLabel loanLabel = new JLabel("Has Personal Loan?");
        JLabel contactLabel = new JLabel("Contact Communication Type:");
        JLabel dayLabel = new JLabel("Last Contact Day of Month:");
        JLabel monthLabel = new JLabel("Last Contact Month:");
        JLabel campaignLabel = new JLabel("Number of Contacts During Campaign:");
        JLabel pdaysLabel = new JLabel("Days Since Last Contact:");
        JLabel previousLabel = new JLabel("Number of Contacts Before Campaign:");
        JLabel poutcomeLabel = new JLabel("Previous Outcome:");

        // Input fields for each label
        JTextField ageField = new JTextField();
        JTextField balanceField = new JTextField();
        JTextField dayField = new JTextField();
        JTextField campaignField = new JTextField();
        JTextField pdaysField = new JTextField();
        JTextField previousField = new JTextField();
        
        //default values
        ageField.setText("30");  // Default value for age
        balanceField.setText("1500");  // Default value for balance
        dayField.setText("15");  // Default value for day
        campaignField.setText("0");  // Default value for campaign
        pdaysField.setText("-1");  // Default value for pdays (use 999 for no contact)
        previousField.setText("0");  // Default value for previous

        // ComboBoxes for categorical variables
        
        String[] jobOptions = {"management", "services", "blue-collar", "technician", "admin.", "retired","self-employed", "housemaid", "entrepreneur", "student", "unemployed","unknown", "entrepreneur"};
        JComboBox<String> jobField = new JComboBox<>(jobOptions);

        String[] maritalOptions = { "Single", "Married", "Divorced" };
        JComboBox<String> maritalField = new JComboBox<>(maritalOptions);

        String[] educationOptions = { "Primary", "Secondary", "Tertiary", "Unknown" };
        JComboBox<String> educationField = new JComboBox<>(educationOptions);

        String[] defaultOptions = { "Yes", "No" };
        JComboBox<String> defaultField = new JComboBox<>(defaultOptions);

        String[] housingOptions = { "Yes", "No" };
        JComboBox<String> housingField = new JComboBox<>(housingOptions);

        String[] loanOptions = { "Yes", "No" };
        JComboBox<String> loanField = new JComboBox<>(loanOptions);

        String[] contactOptions = { "Cellular", "Telephone", "Unknown" };
        JComboBox<String> contactField = new JComboBox<>(contactOptions);

        String[] monthOptions = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        JComboBox<String> monthField = new JComboBox<>(monthOptions);

        String[] poutcomeOptions = { "Success", "Failure", "other", "unknown" };
        JComboBox<String> poutcomeField = new JComboBox<>(poutcomeOptions);

        // Add the fields to the panel
        panel.add(ageLabel);
        panel.add(ageField);

        panel.add(jobLabel);
        panel.add(jobField);

        panel.add(maritalLabel);
        panel.add(maritalField);

        panel.add(educationLabel);
        panel.add(educationField);

        panel.add(defaultLabel);
        panel.add(defaultField);

        panel.add(balanceLabel);
        panel.add(balanceField);

        panel.add(housingLabel);
        panel.add(housingField);

        panel.add(loanLabel);
        panel.add(loanField);

        panel.add(contactLabel);
        panel.add(contactField);

        panel.add(dayLabel);
        panel.add(dayField);

        panel.add(monthLabel);
        panel.add(monthField);

        panel.add(campaignLabel);
        panel.add(campaignField);

        panel.add(pdaysLabel);
        panel.add(pdaysField);

        panel.add(previousLabel);
        panel.add(previousField);

        panel.add(poutcomeLabel);
        panel.add(poutcomeField);

        // Add a submit button to perform the prediction
        JButton predictButton = new JButton("Predict");
        predictButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Gather input data
                    int age = Integer.parseInt(ageField.getText());
                    String job = jobField.getSelectedItem().toString();
                    String marital = maritalField.getSelectedItem().toString();
                    String education = educationField.getSelectedItem().toString();
                    String creditDefault = defaultField.getSelectedItem().toString();
                    double balance = Double.parseDouble(balanceField.getText());
                    String housing = housingField.getSelectedItem().toString();
                    String loan = loanField.getSelectedItem().toString();
                    String contact = contactField.getSelectedItem().toString();
                    int day = Integer.parseInt(dayField.getText());
                    String month = monthField.getSelectedItem().toString();
                    int campaign = Integer.parseInt(campaignField.getText());
                    int pdays = Integer.parseInt(pdaysField.getText());
                    int previous = Integer.parseInt(previousField.getText());
                    String poutcome = poutcomeField.getSelectedItem().toString();

                    // Validate inputs - check if the numeric fields are empty or invalid
                    if (job == null || marital == null || education == null || creditDefault == null || housing == null
                            || loan == null || contact == null || month == null || poutcome == null) {
                        JOptionPane.showMessageDialog(null, "Please fill in all the fields.");
                        return; // Don't proceed with prediction if any field is missing
                    }

                    // Send data to the Manager for prediction (assuming you have a method in
                    // Manager for this)
                    boolean prediction = manager.predictionTriggered(age, job, marital, education, creditDefault,
                            balance,
                            housing, loan, contact, day, month, campaign,
                            pdays, previous, poutcome);

                    // Show the result to the user
                    String resultMessage = prediction ? "The customer will likely subscribe."
                            : "The customer will likely not subscribe.";
                    JOptionPane.showMessageDialog(null, resultMessage);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numeric values.");
                }
            }
        });
        JButton addButton = new JButton("AdditionalService");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                cardLayout.show(mainPanel, "createAddServiceScreen");

            }
        });
        
        // Add the prediction button
        panel.add(predictButton);
        panel.add(addButton);
        
        return panel;
    }

    private JPanel createAddServiceScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(16, 2, 10, 10)); // Grid layout for form fields
        
        return panel;
        JPanel panel = new JPanel();

    }

    // TODO: update render() function in class diagram to this instead
    private JPanel updateMainScreen(boolean triggerPrediction) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        JLabel instructionLabel = new JLabel("Model now trained.\nPrediction Available now.\nPlease select an option.",
                JLabel.CENTER);

        JButton trainButton = new JButton("Retrain Model?");
        JButton predictButton = new JButton("Prediction Service");
        JButton quitButton = new JButton("Quit Program");

        // add actionListener for moving to training screen
        trainButton.addActionListener(createMoveListener("TrainScreen"));

        predictButton.addActionListener(createMoveListener("PredictScreen"));

        // add actionListener for quitting program
        quitButton.addActionListener(quitListener);

        panel.add(instructionLabel);
        panel.add(trainButton);
        panel.add(predictButton);
        panel.add(quitButton);

        return panel;
    }

    }

    private static String createFieldMessage(HashMap<String, String> featureDatatype) {
        StringBuilder builder = new StringBuilder();

        for (HashMap.Entry<String, String> entry : featureDatatype.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return builder.toString();
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