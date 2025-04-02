import java.util.ArrayList;
import java.util.List;

public class IntegrityTest {
    public static void main(String[] args) throws Exception {
        // Create wallets for Alice and Bob (test case)
        User_Wallet alice = new User_Wallet();
        User_Wallet bob = new User_Wallet();

        // Create transactions
        Transaction tx1 = alice.createTransaction(bob.getPublicKey(), 5.0);
        Transaction tx2 = alice.createTransaction(bob.getPublicKey(), 10.0);

        // Create a list of transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(tx1);
        transactions.add(tx2);

        // Create a block
        Block block = new Block("0", transactions, "Block Data");

        // Falsify the transaction tx1, trying to change the amount from 5.0 to 100.0
        SimulateFalsifyTransaction(tx1, 100.0);

        // Verify transaction integrity
        System.out.println("Is the transaction being falsified? " + verifyTransactionIntegrity(tx1));
        System.out.println();

        // Falsify a block, trying to change the string data
        SimulateFalsifyBlock(block, "Falsified the original Block Data");

        // Verify block integrity
        System.out.println("Is the block being falsified? " + verifyBlockIntegrity(block));
    }

    public static void SimulateFalsifyTransaction(Transaction transaction, double newAmount) {
        double originalAmount = transaction.getData().getAmount();

        // Falsify the transaction amount
        transaction.getData().setAmount(newAmount);

        System.out.println("We have falsified the transaction amount " + "[" + originalAmount + "]"
                + " of transactionId: " + transaction.getTransactionId() + " to " + "[" + newAmount + "]");
    }

    public static void SimulateFalsifyBlock(Block block, String newData) {
        String originalData = block.getHeader().data;

        // Falsify the block data
        block.getHeader().data = newData;
        block.getHeader().hash = block.getHeader().calculateHash();

        System.out.println("We have falsified the block data " + "[" + originalData + "]" + " of blockHeader: " +
                block.getMerkleRoot() + " to " + "[" + newData + "]");
    }

    public static boolean verifyTransactionIntegrity(Transaction transaction) {
        String originalTransactionId = transaction.getTransactionId();
        String currentTransactionId = transaction.calculateTransactionId();

        System.out.println("Original Transaction ID: " + originalTransactionId);
        System.out.println("Current Transaction ID: " + currentTransactionId);

        return !originalTransactionId.equals(currentTransactionId);
    }

    public static boolean verifyBlockIntegrity(Block block) {
        String originalMerkleRoot = block.getMerkleRoot();

        //calculate hash value of new merkle tree root
        block.getMerkleTree().calculate();
        String currentMerkleRoot = block.getMerkleTree().getRoot();

        System.out.println("Original Merkle Root: " + originalMerkleRoot);
        System.out.println("Current Merkle Root: " + currentMerkleRoot);

        return !originalMerkleRoot.equals(currentMerkleRoot);
    }
}