import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Main {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        User_Wallet alice = new User_Wallet();
        User_Wallet bob = new User_Wallet();

        // Alice creates a transaction to Bob
        Transaction tx01 = alice.createTransaction(bob.getPublicKey(), 5.0);

        // Print transaction details
        System.out.println("Transaction ID: " + tx01.getTransactionId());
        System.out.println("Amount: " + tx01.getData().getAmount());
        System.out.println("Sender: " + tx01.getInput());
        System.out.println("Receiver: " + tx01.getOutput());

        // Verify the transaction signature
        boolean isValid = TransactionVerifier.verifySignature(tx01);
        System.out.println("Is signature valid? " + isValid);
    }
}