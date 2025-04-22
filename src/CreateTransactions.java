import java.util.List;

public class CreateTransactions {
    public static void main(String[] args) throws Exception {
        // Load existing data
        User_Wallet.loadAllWallets();
        Transaction.loadAllTransactions();
        List<Transaction> allTransactions = Transaction.getTransactionList();

        // Get or create wallets
        User_Wallet alice = User_Wallet.getWalletByName("Alice");
        User_Wallet bob = User_Wallet.getWalletByName("Bob");

        Transaction tx1 = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 50, alice.sign(30));
        Transaction tx2 = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 60, alice.sign(40));
        Transaction tx3 = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 70, alice.sign(30));
        Transaction tx4 = new Transaction(alice.getPublicKey(), bob.getPublicKey(), 80, alice.sign(40));

    }
}
