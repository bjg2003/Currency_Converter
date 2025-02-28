package com.Springboot.CurrencyConverter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;

public class CurrencyConverterGUI extends JFrame {
    private JComboBox<String> fromCurrency, toCurrency;
    private JTextField amountField, resultField;
    private JButton convertButton, historyButton;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public CurrencyConverterGUI() {
        setTitle("Currency Converter");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setLayout(new BorderLayout(10, 10));

        // Main Panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // UI Components
        JLabel title = new JLabel("Currency Converter", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(title, gbc);

        // From Currency
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("From Currency:"), gbc);
        fromCurrency = new JComboBox<>(new String[]{"USD", "INR", "EUR", "GBP", "JPY", "CAD"});
        gbc.gridx = 1;
        panel.add(fromCurrency, gbc);

        // To Currency
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("To Currency:"), gbc);
        toCurrency = new JComboBox<>(new String[]{"USD", "INR", "EUR", "GBP", "JPY", "CAD"});
        gbc.gridx = 1;
        panel.add(toCurrency, gbc);

        // Amount Input
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Amount:"), gbc);
        amountField = new JTextField();
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        // Convert Button
        convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 14));
        convertButton.setBackground(new Color(70, 130, 180));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(convertButton, gbc);

        // Converted Amount
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Converted Amount:"), gbc);
        resultField = new JTextField();
        resultField.setEditable(false);
        gbc.gridx = 1;
        panel.add(resultField, gbc);

        // History Button
        historyButton = new JButton("Show History");
        historyButton.setFont(new Font("Arial", Font.BOLD, 14));
        historyButton.setBackground(new Color(34, 139, 34));
        historyButton.setForeground(Color.WHITE);
        historyButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(historyButton, gbc);

        // Table for Conversion History
        tableModel = new DefaultTableModel(new String[]{"From", "To", "Amount", "Converted", "Timestamp"}, 0);
        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(20);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Conversion History"));
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button Click Event for Conversion
        convertButton.addActionListener(e -> convertCurrency());

        // Button Click Event for History
        historyButton.addActionListener(e -> fetchHistory());

        setVisible(true);
    }

    private void convertCurrency() {
        try {
            String from = (String) fromCurrency.getSelectedItem();
            String to = (String) toCurrency.getSelectedItem();
            String amount = amountField.getText();

            // API Call
            String urlStr = "http://localhost:8080/api/convert?from=" + from + "&to=" + to + "&amount=" + amount;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();

            resultField.setText(response);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Conversion Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchHistory() {
        try {
            String urlStr = "http://localhost:8080/api/history";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();

            // Parse JSON response
            JSONArray jsonArray = new JSONArray(response);
            tableModel.setRowCount(0); // Clear table

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                tableModel.addRow(new Object[]{
                        obj.getString("fromCurrency"),
                        obj.getString("toCurrency"),
                        obj.getDouble("amount"),
                        obj.getDouble("convertedAmount"),
                        obj.getString("timestamp")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching history: " + ex.getMessage(), "History Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CurrencyConverterGUI::new);
    }
}
