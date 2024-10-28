// RSADecryption.java
import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class RSADecryption {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 8: Read private key
        System.out.println("Reading private key from private_key.txt...");
        int n = 0, d = 0;
        try (Scanner fileScanner = new Scanner(new File("private_key.txt"))) {
            n = fileScanner.nextInt();
            d = fileScanner.nextInt();
        } catch (FileNotFoundException ex) {
            System.out.println("Error reading private key");
            return;
        }

        // Step 9: Read ciphertext
        System.out.println("Reading ciphertext from ciphertext.txt...");
        String ciphertext = "";
        try (Scanner fileScanner = new Scanner(new File("ciphertext.txt"))) {
            ciphertext = fileScanner.nextLine();
        } catch (FileNotFoundException ex) {
            System.out.println("Error reading ciphertext");
            return;
        }

        // Step 10: Decrypt and display
        String[] numbers = ciphertext.split(",");
        StringBuilder plaintext = new StringBuilder();

        for (String number : numbers) {
            if (!number.isEmpty()) {
                BigInteger c = new BigInteger(number);
                BigInteger decrypted = c.modPow(BigInteger.valueOf(d), BigInteger.valueOf(n));
                plaintext.append((char)decrypted.intValue());
            }
        }

        System.out.println("Decrypted text: " + plaintext.toString());
    }
}