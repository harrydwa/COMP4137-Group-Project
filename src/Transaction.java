import java.io.*;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class Transaction {
    private String transactionId;
    private PublicKey input; //sender address
    private PublicKey output; //receiver address
    private TransactionData data; // coin amount and signature
    private static final String TX_FILE = "transactions.txt";
    private static List<Transaction> allTransactions = new ArrayList<>();

    // Public constructor for NEW transactions
    public Transaction(PublicKey input, PublicKey output, double amount, byte[] signature) {
        // Check sender balance before creating transaction
        User_Wallet senderWallet = User_Wallet.getWalletByPublicKey(input);
        if (senderWallet != null) {
            double balance = senderWallet.getBalance();
            if (balance < amount) {
                throw new IllegalArgumentException("Insufficient funds in sender wallet. Balance: "
                        + balance + ", Required: " + amount);
            }
        }

        this.input = input;
        this.output = output;
        this.data = new TransactionData(amount, signature);
        this.transactionId = calculateTransactionId();
        this.addToParticipantWallets();
        allTransactions.add(this); // Add to static list
        saveToFile();
    }

    public static List<Transaction> getTransactionList() {
        return allTransactions;
    }

    // Private constructor for LOADED transactions
    private Transaction(PublicKey input, PublicKey output, double amount, byte[] signature, String transactionId) {
        this.input = input;
        this.output = output;
        this.data = new TransactionData(amount, signature);
        this.transactionId = transactionId;
        this.addToParticipantWallets(); // Link to wallets but don't add to static list
    }

    private void saveToFile() {
        try {
            // First check if this transaction already exists in the file
            File file = new File(TX_FILE);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\\|");
                        if (parts.length > 0 && parts[parts.length - 1].equals(this.transactionId)) {
                            // Transaction already exists in file, don't save again
                            return;
                        }
                    }
                }
            }

            // If we reach here, transaction doesn't exist in file, so save it
            try (PrintWriter writer = new PrintWriter(new FileWriter(TX_FILE, true))) {
                // Remove the size check condition
                String sender = Base64.getEncoder().encodeToString(input.getEncoded());
                String receiver = Base64.getEncoder().encodeToString(output.getEncoded());
                String sig = Base64.getEncoder().encodeToString(data.getSignature());
                writer.println(sender + "|" + receiver + "|" + data.getAmount() + "|" + sig + "|" + transactionId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Transaction> loadAllTransactions() throws Exception {
        allTransactions.clear(); // Clear existing transactions
        KeyFactory kf = KeyFactory.getInstance("EC");
        List<Transaction> loaded = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(TX_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                PublicKey sender = kf.generatePublic(
                        new X509EncodedKeySpec(Base64.getDecoder().decode(parts[0]))
                );
                PublicKey receiver = kf.generatePublic(
                        new X509EncodedKeySpec(Base64.getDecoder().decode(parts[1]))
                );
                double amount = Double.parseDouble(parts[2]);
                byte[] signature = Base64.getDecoder().decode(parts[3]);
                String txId = parts[4];

                Transaction tx = new Transaction(sender, receiver, amount, signature, txId);
                loaded.add(tx);
            }
        }
        allTransactions.addAll(loaded); // Add all loaded transactions
        return allTransactions;
    }

    public static List<Transaction> getAllTransactions() {
        return new ArrayList<>(allTransactions);
    }

    //hashing input, output, amount and signature
    public String calculateTransactionId() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //getEncoded: converts the public key into a byte array
            //update: includes public key in bytes in the final computation
            digest.update(input.getEncoded());
            digest.update(output.getEncoded());

            ByteArrayOutputStream Boutput = new ByteArrayOutputStream();
            DataOutputStream Doutput = new DataOutputStream(Boutput); //write primitive data type (double) as raw bytes
            Doutput.writeDouble(data.getAmount());
            Doutput.close();
            digest.update(Boutput.toByteArray()); //update: includes amount in the final computation
            digest.update(data.getSignature()); //update: includes sign in the final computation

            byte[] hashBytes = digest.digest(); //final computation
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating transaction ID", e);
        }
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Serialize transaction for Merkle tree
    public byte[] serializeTransaction() {
        try {
            ByteArrayOutputStream Boutput = new ByteArrayOutputStream();
            DataOutputStream Doutput = new DataOutputStream(Boutput);

            byte[] inputBytes = input.getEncoded();
            Doutput.writeInt(inputBytes.length);
            Doutput.write(inputBytes);


            byte[] outputBytes = output.getEncoded();
            Doutput.writeInt(outputBytes.length);
            Doutput.write(outputBytes);


            Doutput.writeDouble(data.getAmount());


            byte[] signatureBytes = data.getSignature();
            Doutput.writeInt(signatureBytes.length);
            Doutput.write(signatureBytes);

            return Boutput.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    private void addToParticipantWallets() {
        User_Wallet senderWallet = User_Wallet.getWalletByPublicKey(input);
        User_Wallet receiverWallet = User_Wallet.getWalletByPublicKey(output);

        if (senderWallet != null) {
            senderWallet.addTransaction(this);
        }
        if (receiverWallet != null) {
            receiverWallet.addTransaction(this);
        }
    }


    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public PublicKey getInput() {
        return input;
    }

    public PublicKey getOutput() {
        return output;
    }

    public TransactionData getData() {
        return data;
    }

    public boolean isInput(PublicKey key) {
        return compareKeys(this.input, key);
    }

    public boolean isOutput(PublicKey key) {
        return compareKeys(this.output, key);
    }

    private boolean compareKeys(PublicKey key1, PublicKey key2) {
        return java.util.Arrays.equals(
                key1.getEncoded(),
                key2.getEncoded()
        );
    }
}