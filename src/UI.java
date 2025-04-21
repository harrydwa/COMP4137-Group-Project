import java.io.File;
import java.util.Scanner;
import java.util.List;

public class UI {
    public static void main(String[] args) throws Exception {
        int option = 0;
        Scanner in = new Scanner(System.in);
        //String[] stringArg = {""};

        System.out.println("Blockchain System");
        while (option != 7) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("1: Create Account");
            System.out.println("2: Check Account Wallet");
            System.out.println("3: Transaction Generation");
            System.out.println("4: Verifiable Merkle Tree Of Transactions");
            System.out.println("5: Mining a Block");
            System.out.println("6: Integrity Verification");
            System.out.println("7: Exit");
            System.out.print("Enter your choice: ");
            option = in.nextInt();

            switch(option) {
                case 1:
                    handleCreateAccount(in);
                    break;
                case 2:
                    handleCheckWallet(in);
                    break;
                case 3:
                    handleTransactionGeneration(in);
                    break;
                case 4:
                    handleMerkleTree();
                    break;
                case 5:
                    handleMining(in);
                    break;
                case 6:
                    handleIntegrityCheck();
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
        System.out.println("Thank you for using! Now exiting!");
        in.close();
    }

    private static void handleCreateAccount(Scanner in) throws Exception {
        System.out.println("\n--- Create Account ---");
        in.nextLine(); // Clear buffer
        System.out.print("Enter username: ");
        String username = in.nextLine().trim();
        User_Wallet.loadAllWallets();

        User_Wallet wallet = User_Wallet.getWalletByName(username);
        if (wallet == null) {
            new User_Wallet(username);
            User_Wallet.loadAllWallets();
            System.out.println("Account created successfully!");
        } else {
            System.out.println("Account already exists!");
        }
        waitForBack(in);
    }

    private static void handleCheckWallet(Scanner in) throws Exception {
        User_Wallet.loadAllWallets();
        Transaction.loadAllTransactions();
        System.out.println("\n--- Check Account Wallet ---");
        in.nextLine(); // Clear buffer
        System.out.print("Enter username: ");
        String username = in.nextLine().trim();

        User_Wallet wallet = User_Wallet.getWalletByName(username);
        if (wallet != null) {
            System.out.println("\nWallet Details:");
            System.out.println("Owner: " + username);
            System.out.printf("Balance: %.2f\n", wallet.getBalance());
            wallet.printTransactionHistory();
        } else {
            System.out.println("Wallet not found!");
        }
        waitForBack(in);
    }

    private static void handleTransactionGeneration(Scanner in) throws Exception {
        User_Wallet.loadAllWallets();
        Transaction.loadAllTransactions();
        System.out.println("\n--- Transaction Generation ---");
        in.nextLine(); // Clear buffer

        // Get sender
        System.out.print("Enter sender username: ");
        String senderName = in.nextLine().trim();
        User_Wallet sender = User_Wallet.getWalletByName(senderName);

        if (sender == null) {
            System.out.println("Sender wallet not found!");
            waitForBack(in);
            return;
        }

        // Get receiver
        System.out.print("Enter receiver username: ");
        String receiverName = in.nextLine().trim();
        User_Wallet receiver = User_Wallet.getWalletByName(receiverName);

        if (receiver == null) {
            System.out.println("Receiver wallet not found!");
            waitForBack(in);
            return;
        }

        // Get amount
        System.out.print("Enter amount: ");
        double amount = in.nextDouble();

        // Check balance
        if (sender.getBalance() < amount) {
            System.out.println("Insufficient balance!");
            waitForBack(in);
            return;
        }

        // Create transaction
        try {
            Transaction tx = sender.createTransaction(receiver.getPublicKey(), amount);
            if (TransactionVerifier.verifySignature(tx)) {
                System.out.println("Transaction created successfully!");
                System.out.println("TX ID: " + tx.getTransactionId());
                Transaction.loadAllTransactions();
            } else {
                System.out.println("Transaction verification failed!");
            }
        } catch (Exception e) {
            System.out.println("Error creating transaction: " + e.getMessage());
        }
        waitForBack(in);
    }

    private static void handleMerkleTree() {
        System.out.println("\n--- Verifiable Merkle Tree ---");
        try {
            Transaction.loadAllTransactions();
            List<Transaction> transactions = Transaction.getTransactionList();

            if (!transactions.isEmpty()) {
                Merkle_Tree merkleTree = new Merkle_Tree(transactions);
                merkleTree.calculate();
                System.out.println("\nMerkle Root: " + merkleTree.getRoot());
            } else {
                System.out.println("No transactions available!");
            }
        } catch (Exception e) {
            System.out.println("Error generating Merkle Tree: " + e.getMessage());
        }
    }

    private static void handleMining(Scanner in) throws Exception {
        System.out.println("\n--- Block Mining ---");
        Transaction.loadAllTransactions();
        List<Transaction> transactions = Transaction.getTransactionList();

        if (transactions.isEmpty()) {
            System.out.println("No transactions to mine!");
            waitForBack(in);
            return;
        }

        // Get previous block hash (simplified example)
        String previousHash = "00000000000000000000000000000000"; // Default for first block
        File blockFile = new File("block1.txt");
        if (blockFile.exists()) {
            Block previousBlock = Block.loadBlock("block1.txt");
            previousHash = previousBlock.getHash();
        }

        // Create block with difficulty
        int difficulty = 4; // Number of leading zeros required
        Block block = new Block(previousHash, transactions, "Mined Block");
        block.getHeader().mineBlock(difficulty);

        // Save mined block
        block.saveBlock("block1.txt");
        System.out.println("\nBlock mined successfully!");
        System.out.println("Hash: " + block.getHash());
        System.out.println("Nonce: " + block.getNonce());

        waitForBack(in);
    }

    private static void handleIntegrityCheck() throws Exception {
        Transaction.loadAllTransactions();
        System.out.println("\n--- Integrity Verification ---");
        try {
            IntegrityTest test = new IntegrityTest();
            test.main(new String[0]);
        } catch (Exception e) {
            System.out.println("Integrity check failed: " + e.getMessage());
        }
    }

    private static void waitForBack(Scanner in) {
        System.out.print("\nPress Enter to continue...");
        in.nextLine();
    }
}