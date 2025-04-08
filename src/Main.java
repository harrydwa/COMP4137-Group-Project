public class Main {
    public static void main(String[] args) {
        try {
            // Load existing data
            User_Wallet.loadAllWallets();
            Transaction.loadAllTransactions();

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

            Transaction tx = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 20, alice.sign(10));

            // Print wallet states
            alice.printTransactionHistory();
            bob.printTransactionHistory();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}