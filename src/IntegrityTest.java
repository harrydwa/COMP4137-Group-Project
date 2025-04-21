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
            transactions.get(i).getData().setAmount(new Random().nextDouble() * 200);
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
        // Load the original block and store its values
        Block originalBlock = Block.loadBlock("block1.txt");
        String originalHash = originalBlock.getHash();
        String originalMerkleRoot = originalBlock.getMerkleRoot();
        List<Transaction> transactions = originalBlock.getTransactions();

        System.out.println("Original Block Values:");
        System.out.println("  Block Hash: " + originalHash);
        System.out.println("  Merkle Root: " + originalMerkleRoot);

        // Modify a transaction in the block
        System.out.print("\n=== Falsifying Block... ===");
        if (!transactions.isEmpty()) {
            transactions.get(0).getData().setAmount(new Random().nextDouble() * 200);
            System.out.println("\nModified first transaction amount");
        }

        // Create new block with modified transactions
        Block modifiedBlock = new Block(originalBlock.getPreviousHash(), transactions, originalBlock.getHeader().data);

        System.out.println("\nBlock Integrity Check:");
        System.out.println("  Original Block Hash: " + originalHash);
        System.out.println("  Modified Block Hash: " + modifiedBlock.getHash());
        System.out.println("  Block Modified?: " + !originalHash.equals(modifiedBlock.getHash()));

        System.out.println("\nMerkle Root Comparison:");
        System.out.println("  Original Merkle Root: " + originalMerkleRoot);
        System.out.println("  Modified Merkle Root: " + modifiedBlock.getMerkleRoot());
        System.out.println("  Merkle Tree Modified?: " + !originalMerkleRoot.equals(modifiedBlock.getMerkleRoot()));
    }
}