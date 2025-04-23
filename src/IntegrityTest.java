import java.util.List;
import java.util.Random;

public class IntegrityTest {
    public static void main(String[] args) {
        try {
            System.out.println("Test 1: Falsified Transactions\n");
            testFalsifiedTransactions();

            System.out.println("\nTest 2: Falsified Block");
            testFalsifiedBlock();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testFalsifiedTransactions() throws Exception {
        // Load transactions and create initial Merkle tree
        User_Wallet.loadAllWallets();
        Transaction.loadAllTransactions();
        List<Transaction> transactions = Transaction.getTransactionList();

        // Store original Merkle root
        Merkle_Tree originalMerkleTree = new Merkle_Tree(transactions);
        originalMerkleTree.calculate();
        String originalMerkleRoot = originalMerkleTree.getRoot();

        // Store original transaction hashes
        String[] originalHashes = new String[transactions.size()];
        for (int i = 0; i < transactions.size(); i++) {
            originalHashes[i] = transactions.get(i).getTransactionId();
        }

        System.out.println("\nOriginal Merkle Root: " + originalMerkleRoot);

        // Modify transactions and compare hashes
        System.out.print("\n=== Modifying Transaction... ===");
        for (int i = 0; i < transactions.size(); i++) {

            // Modify transaction amount
            Double newAmount = new Random().nextDouble() * 200;
            transactions.get(i).getData().setAmount(newAmount);
            System.out.print("\nModified amount of " + transactions.get(i).getTransactionId() + " to " + newAmount + ".");

            String newHash = transactions.get(i).calculateTransactionId();

            System.out.println("\nTransaction " + i + ":");
            System.out.println("  Original Hash: " + originalHashes[i]);
            System.out.println("  Modified Hash: " + newHash);
            System.out.println("  Transaction Modified?: " + !originalHashes[i].equals(newHash));
        }

        System.out.println();

        // Create new Merkle tree with modified transactions
        Merkle_Tree modifiedMerkleTree = new Merkle_Tree(transactions);
        modifiedMerkleTree.calculate();
        String modifiedMerkleRoot = modifiedMerkleTree.getRoot();

        System.out.println("\nMerkle Root Comparison:");
        System.out.println("  Original Root: " + originalMerkleRoot);
        System.out.println("  Modified Root: " + modifiedMerkleRoot);
        System.out.println("  Merkle Tree Modified?: " + !originalMerkleRoot.equals(modifiedMerkleRoot));
    }

    private static void testFalsifiedBlock() throws Exception {
        System.out.println("\n=== Falsifying Block... ===");

        // Load the original block and store its values
        Block originalBlock = Block.loadBlock("block1.txt");
        BlockHeader originalHeader = originalBlock.getHeader();
        String originalHash = originalBlock.getHash();
        List<Transaction> transactions = originalBlock.getTransactions();

        // Calculate Merkle root for the original block to get the original root
        Merkle_Tree OriginalMerkleTree = new Merkle_Tree(transactions);
        OriginalMerkleTree.calculate();
        String originalMerkleRoot = OriginalMerkleTree.getRoot();
        originalHeader.merkleRoot = originalMerkleRoot;

        System.out.println("\nOriginal Block Values:");
        System.out.println("  Block Hash: " + originalHash);
        System.out.println("  Merkle Root: " + originalMerkleRoot);
        System.out.println("  Timestamp: " + originalHeader.timeStamp);
        System.out.println("  Nonce: " + originalHeader.nonce);
        System.out.println("  Data: " + originalHeader.data);

        System.out.println("\n=== Modifying Block Information... ===");

        // Modify transaction amount
        if (!transactions.isEmpty()) {
            Double newAmount = new Random().nextDouble() * 200;

            transactions.get(0).getData().setAmount(newAmount);
            System.out.println("Modified first amount of first transaction to " + newAmount + ".\n");
        }

        // Create modified block with different values
        Block modifiedBlock = new Block(originalBlock.getPreviousHash(), transactions, "Modified Data");
        BlockHeader modifiedHeader = modifiedBlock.getHeader();
        modifiedHeader.timeStamp = System.currentTimeMillis();
        modifiedHeader.nonce = originalHeader.nonce + 1;

        // Calculate hash value of new Merkle root for modified block
        Merkle_Tree modifiedMerkleTree = new Merkle_Tree(transactions);
        modifiedMerkleTree.calculate();
        modifiedHeader.merkleRoot = modifiedMerkleTree.getRoot();

        System.out.println("\nBlock Integrity Check:");
        System.out.println("  Original Block Hash: " + originalHash);
        System.out.println("  Modified Block Hash: " + modifiedBlock.getHash());
        System.out.println("  Block Modified?: " + !originalHash.equals(modifiedBlock.getHash()));

        System.out.println("\nMerkle Root Comparison:");
        System.out.println("  Original Merkle Root: " + originalMerkleRoot);
        System.out.println("  Modified Merkle Root: " + modifiedHeader.merkleRoot);
        System.out.println("  Merkle Tree Modified?: " + !originalMerkleRoot.equals(modifiedHeader.merkleRoot));

        System.out.println("\nOther Modified Values:");
        System.out.println("  Timestamp Modified?: " + (originalHeader.timeStamp != modifiedHeader.timeStamp));
        System.out.println("  Nonce Modified?: " + (originalHeader.nonce != modifiedHeader.nonce));
        System.out.println("  Data Modified?: " + !originalHeader.data.equals(modifiedHeader.data));
    }
}