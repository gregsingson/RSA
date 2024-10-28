// RSADecryptionGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class RSADecryptionGUI extends JFrame {
    private JTextArea inputText, outputText;
    private JTextField nField, dField;

    public RSADecryptionGUI() {
        setTitle("RSA Decryption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Key input panel
        JPanel keyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        nField = new JTextField(10);
        dField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        keyPanel.add(new JLabel("n:"), gbc);
        gbc.gridx = 1;
        keyPanel.add(nField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        keyPanel.add(new JLabel("d:"), gbc);
        gbc.gridx = 1;
        keyPanel.add(dField, gbc);

        JButton loadKey = new JButton("Load Key from File");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        keyPanel.add(loadKey, gbc);

        // Text input/output panel
        JPanel textPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        textPanel.setBorder(BorderFactory.createTitledBorder("Message Processing"));

        // Input section
        JLabel inputLabel = new JLabel("Enter or load encrypted text (comma-separated numbers):");
        inputText = new JTextArea(5, 40);
        JScrollPane inputScroll = new JScrollPane(inputText);

        // Output section
        JLabel outputLabel = new JLabel("Decrypted text:");
        outputText = new JTextArea(5, 40);
        outputText.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputText);

        textPanel.add(inputLabel);
        textPanel.add(inputScroll);
        textPanel.add(outputLabel);
        textPanel.add(outputScroll);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton loadCiphertext = new JButton("Load Ciphertext");
        JButton decryptButton = new JButton("Decrypt");
        buttonPanel.add(loadCiphertext);
        buttonPanel.add(decryptButton);

        // Add all panels to frame
        add(keyPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add event listeners
        loadKey.addActionListener(e -> loadKeyFromFile());
        loadCiphertext.addActionListener(e -> loadCiphertextFromFile());
        decryptButton.addActionListener(e -> decryptText());

        pack();
        setLocationRelativeTo(null);
    }

    private void loadKeyFromFile() {
        try (Scanner scanner = new Scanner(new File("private_key.txt"))) {
            nField.setText(scanner.nextLine());
            dField.setText(scanner.nextLine());
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading private key file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCiphertextFromFile() {
        try (Scanner scanner = new Scanner(new File("ciphertext.txt"))) {
            inputText.setText(scanner.nextLine());
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading ciphertext file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decryptText() {
        try {
            int n = Integer.parseInt(nField.getText());
            int d = Integer.parseInt(dField.getText());

            String[] numbers = inputText.getText().split(",");
            StringBuilder plaintext = new StringBuilder();

            for (String number : numbers) {
                if (!number.isEmpty()) {
                    BigInteger c = new BigInteger(number);
                    BigInteger decrypted = c.modPow(BigInteger.valueOf(d), BigInteger.valueOf(n));
                    plaintext.append((char)decrypted.intValue());
                }
            }

            outputText.setText(plaintext.toString());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for n and d!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RSADecryptionGUI().setVisible(true);
        });
    }
}