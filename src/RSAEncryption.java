// RSAEncryption.java
import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class RSAEncryption {
    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;

        if (n % 2 == 0 || n % 3 == 0) return false;

        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        }
        return true;
    }

    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static int generateE(int phi) {
        // Starting with 3 as it's the smallest prime that could be used
        for (int e = 3; e < phi; e++) {
            if (gcd(e, phi) == 1) {
                return e;
            }
        }
        return 3;
    }

    public static int multiplicativeInverse(int e, int phi) {
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1 & 2: Input and verify prime numbers
        int p, q;
        do {
            System.out.print("Enter first prime number (up to 1000): ");
            p = scanner.nextInt();
        } while (!isPrime(p) || p > 1000);

        do {
            System.out.print("Enter second prime number (up to 1000): ");
            q = scanner.nextInt();
        } while (!isPrime(q) || q > 1000);

        // Step 3: Generate key pair
        int n = p * q;
        int phi = (p - 1) * (q - 1);
        int e = generateE(phi);
        int d = multiplicativeInverse(e, phi);

        // Step 4: Save keys to file
        try (PrintWriter writer = new PrintWriter("public_key.txt")) {
            writer.println(n);
            writer.println(e);
        } catch (FileNotFoundException ex) {
            System.out.println("Error saving public key");
            return;
        }

        try (PrintWriter writer = new PrintWriter("private_key.txt")) {
            writer.println(n);
            writer.println(d);
        } catch (FileNotFoundException ex) {
            System.out.println("Error saving private key");
            return;
        }

        System.out.println("Public Key (n,e): (" + n + "," + e + ")");
        System.out.println("Private Key (n,d): (" + n + "," + d + ")");

        // Step 5: Input text
        scanner.nextLine(); // Clear buffer
        System.out.print("Enter text to encrypt: ");
        String plaintext = scanner.nextLine();

        // Step 6: Encrypt
        StringBuilder ciphertext = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            BigInteger m = BigInteger.valueOf((int)c);
            BigInteger encrypted = m.modPow(BigInteger.valueOf(e), BigInteger.valueOf(n));
            ciphertext.append(encrypted).append(",");
        }

        // Step 7: Save encryption result
        try (PrintWriter writer = new PrintWriter("ciphertext.txt")) {
            writer.println(ciphertext.toString());
            System.out.println("Encrypted text saved to ciphertext.txt");
        } catch (FileNotFoundException ex) {
            System.out.println("Error saving ciphertext");
        }
    }
}
