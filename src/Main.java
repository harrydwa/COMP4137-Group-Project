import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Load existing data
            User_Wallet.loadAllWallets();
            Transaction.loadAllTransactions();
            List<Transaction> allTransactions = Transaction.getTransactionList();

            // Get or create wallets
            User_Wallet alice = User_Wallet.getWalletByName("Alice");
            User_Wallet bob = User_Wallet.getWalletByName("Bob");


            // Create new wallets if they don't exist
            if (alice == null) {
                alice = new User_Wallet("Alice");
                System.out.println("Created new wallet: Alice");
            }
            if (bob == null) {
                bob = new User_Wallet("Bob");
                System.out.println("Created new wallet: Bob");
            }


//            Transaction tx3 = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 20, alice.sign(30));
//            Transaction tx4 = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 20, alice.sign(40));
//            //  number of transactions must be the power of 2

            // Print wallet states
            alice.printTransactionHistory();
            bob.printTransactionHistory();
            System.out.println(allTransactions.size() + " transactions loaded.");

            Merkle_Tree merkleTree = new Merkle_Tree(allTransactions);
            merkleTree.calculate();
//            merkleTree.validmerkletree();
            System.out.println(merkleTree.getRoot());

            // Create a block
            Block block = new Block("previousHash", allTransactions, "Block Data");
            block.saveBlock("block1.txt");
            System.out.println("\nBlock saved to file.");

            // Load the block from the file
            Block loadedBlock = Block.loadBlock("block1.txt");
            System.out.println("Loaded Block Details:");
            System.out.println("Hash: " + loadedBlock.getHash());
            System.out.println("Previous Hash: " + loadedBlock.getPreviousHash());
            System.out.println("Merkle Root: " + loadedBlock.getMerkleRoot());
            System.out.println("Timestamp: " + loadedBlock.getTimestamp());
            System.out.println("Nonce: " + loadedBlock.getNonce());
            System.out.println("Transactions: " + loadedBlock.getTransactions().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}