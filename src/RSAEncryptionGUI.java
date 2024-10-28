// RSAEncryptionGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;

public class RSAEncryptionGUI extends JFrame {
    private JTextField primeP, primeQ;
    private JTextArea inputText, outputText;
    private JLabel publicKeyLabel, privateKeyLabel;
    private int n, e, d;

    public RSAEncryptionGUI() {
        setTitle("RSA Encryption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Prime numbers input panel
        JPanel primePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        primeP = new JTextField(10);
        primeQ = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        primePanel.add(new JLabel("First Prime (p):"), gbc);
        gbc.gridx = 1;
        primePanel.add(primeP, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        primePanel.add(new JLabel("Second Prime (q):"), gbc);
        gbc.gridx = 1;
        primePanel.add(primeQ, gbc);

        JButton generateKeys = new JButton("Generate Keys");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        primePanel.add(generateKeys, gbc);

        // Key display panel
        JPanel keyPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        publicKeyLabel = new JLabel("Public Key (n,e): ");
        privateKeyLabel = new JLabel("Private Key (n,d): ");
        keyPanel.add(publicKeyLabel);
        keyPanel.add(privateKeyLabel);

        // Combine prime and key panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(primePanel, BorderLayout.WEST);
        topPanel.add(keyPanel, BorderLayout.CENTER);

        // Text input/output panel
        JPanel textPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        textPanel.setBorder(BorderFactory.createTitledBorder("Message Processing"));

        // Input section
        JLabel inputLabel = new JLabel("Enter text to encrypt:");
        inputText = new JTextArea(5, 40);
        JScrollPane inputScroll = new JScrollPane(inputText);

        // Output section
        JLabel outputLabel = new JLabel("Encrypted text (comma-separated numbers):");
        outputText = new JTextArea(5, 40);
        outputText.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputText);

        textPanel.add(inputLabel);
        textPanel.add(inputScroll);
        textPanel.add(outputLabel);
        textPanel.add(outputScroll);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton encryptButton = new JButton("Encrypt");
        JButton saveButton = new JButton("Save to Files");
        buttonPanel.add(encryptButton);
        buttonPanel.add(saveButton);

        // Add all panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add event listeners
        generateKeys.addActionListener(e -> generateKeyPair());
        encryptButton.addActionListener(e -> encryptText());
        saveButton.addActionListener(e -> saveToFiles());

        pack();
        setLocationRelativeTo(null);
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        }
        return true;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private int generateE(int phi) {
        for (int e = 3; e < phi; e++) {
            if (gcd(e, phi) == 1) {
                return e;
            }
        }
        return 3;
    }

    private int multiplicativeInverse(int e, int phi) {
        int m0 = phi, t, q;
        int x0 = 0, x1 = 1;

        if (phi == 1)
            return 0;

        while (e > 1) {
            q = e / phi;
            t = phi;
            phi = e % phi;
            e = t;
            t = x0;
            x0 = x1 - q * x0;
            x1 = t;
        }

        if (x1 < 0)
            x1 += m0;

        return x1;
    }

    private void generateKeyPair() {
        try {
            int p = Integer.parseInt(primeP.getText());
            int q = Integer.parseInt(primeQ.getText());

            if (!isPrime(p) || !isPrime(q)) {
                JOptionPane.showMessageDialog(this, "Both numbers must be prime!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (p > 1000 || q > 1000) {
                JOptionPane.showMessageDialog(this, "Prime numbers should not exceed 1000!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            n = p * q;
            int phi = (p - 1) * (q - 1);
            e = generateE(phi);
            d = multiplicativeInverse(e, phi);

            publicKeyLabel.setText("Public Key (n,e): (" + n + "," + e + ")");
            privateKeyLabel.setText("Private Key (n,d): (" + n + "," + d + ")");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void encryptText() {
        if (n == 0 || e == 0) {
            JOptionPane.showMessageDialog(this, "Please generate keys first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String plaintext = inputText.getText();
        StringBuilder ciphertext = new StringBuilder();

        for (char c : plaintext.toCharArray()) {
            BigInteger m = BigInteger.valueOf((int)c);
            BigInteger encrypted = m.modPow(BigInteger.valueOf(e), BigInteger.valueOf(n));
            ciphertext.append(encrypted).append(",");
        }

        outputText.setText(ciphertext.toString());
    }

    private void saveToFiles() {
        try {
            // Save public key
            try (PrintWriter writer = new PrintWriter("public_key.txt")) {
                writer.println(n);
                writer.println(e);
            }

            // Save private key
            try (PrintWriter writer = new PrintWriter("private_key.txt")) {
                writer.println(n);
                writer.println(d);
            }

            // Save ciphertext
            try (PrintWriter writer = new PrintWriter("ciphertext.txt")) {
                writer.println(outputText.getText());
            }

            JOptionPane.showMessageDialog(this, "Files saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error saving files!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RSAEncryptionGUI().setVisible(true);
        });
    }
}