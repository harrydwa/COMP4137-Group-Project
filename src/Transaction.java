import java.io.*;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;


public class Transaction {
    private String transactionId;
    private PublicKey input; //sender address
    private PublicKey output; //receiver address
    private TransactionData data; // coin amount and signature

    public Transaction(PublicKey input, PublicKey output, double amount, byte[] signature) {
        this.input = input;
        this.output = output;
        this.data = new TransactionData(amount, signature);
        this.transactionId = calculateTransactionId(); // hash of the transaction
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
        for (byte b :bytes) {
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
}