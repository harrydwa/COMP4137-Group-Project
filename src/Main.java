import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        User_Wallet alice = new User_Wallet();
        User_Wallet bob = new User_Wallet();

        // Alice creates a transaction to Bob
        Transaction tx01 = alice.createTransaction(bob.getPublicKey(), 5.0);
        Transaction tx02= alice.createTransaction(bob.getPublicKey(), 10.0);
        Transaction tx03=bob.createTransaction(alice.getPublicKey(), 15.0);
        Transaction tx04=bob.createTransaction(alice.getPublicKey(), 20.0);




        // Print transaction details
        System.out.println("Transaction ID: " + tx01.getTransactionId());
        System.out.println("Amount: " + tx01.getData().getAmount());
        System.out.println("Sender: " + tx01.getInput());
        System.out.println("Receiver: " + tx01.getOutput());

        // Verify the transaction signature
        boolean isValid = TransactionVerifier.verifySignature(tx01);
        System.out.println("Is signature valid? " + isValid);

        List<Transaction> bytelist = new ArrayList<>();
        bytelist.add(tx01);
        bytelist.add(tx02);
        bytelist.add(tx03);
        bytelist.add(tx04);
        System.out.println();
        Merkle_Tree tree = new Merkle_Tree(bytelist);
        tree.calculate();
        System.out.println("The root of the merkle tree is " +tree.getRoot());//debug
        System.out.println("The children for the index " + 6 + " are: " + tree.getChildren1(6));
        System.out.println("The children for the index " + 6 + " are: " + tree.getChildren2(6));
        System.out.println("The parent for the index " + 6 + " are: " + tree.getParent(6));
        System.out.println("The height for index 1 is "+tree.getHeight(1) );
        System.out.println("The height for index 5 is "+tree.getHeight(5) );
        System.out.println("The height for index 7 is "+tree.getHeight(7) );
        System.out.println("The height is "+tree.getValue(1) );
        System.out.println("The height is "+tree.getValue(2) );
        System.out.println("The height is "+tree.getValue(3) );
        System.out.println("The height is "+tree.getValue(4) );











    }
}