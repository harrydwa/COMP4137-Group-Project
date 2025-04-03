import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class User_Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private List<Transaction> transactions;

    // Static registry to track all wallets by public key
    private static Map<PublicKey, User_Wallet> walletRegistry = new HashMap<>();


    public User_Wallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        generateKeyPair();
        this.transactions = new ArrayList<>();
        walletRegistry.put(publicKey, this); // Auto-register on creation
    }

    private void generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("EC"); // Elliptic Curve
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        keyGenerator.initialize(ecSpec,new SecureRandom());
        KeyPair keyPair = keyGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    //sign with private key + amount
    public byte[] sign(double amount) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initSign(privateKey);

        ByteArrayOutputStream Boutput = new ByteArrayOutputStream();
        DataOutputStream Doutput = new DataOutputStream(Boutput);
        Doutput.writeDouble(amount);
        Doutput.close();
        byte[] amountBytes = Boutput.toByteArray();
        ecdsaSign.update(amountBytes);

        return ecdsaSign.sign();

    }

    public Transaction createTransaction(PublicKey receiver, double amount) throws NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        byte[] signature = this.sign(amount);
        return new Transaction(this.publicKey, receiver, amount, signature);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public static User_Wallet getWalletByPublicKey(PublicKey publicKey) {
        return walletRegistry.get(publicKey);
    }

    private boolean isSender(Transaction transaction) {
        return compareKeys(transaction.getInput(), this.publicKey);
    }

    private boolean isReceiver(Transaction transaction) {
        return compareKeys(transaction.getOutput(), this.publicKey);
    }

    private boolean compareKeys(PublicKey key1, PublicKey key2) {
        return java.util.Arrays.equals(
                key1.getEncoded(),
                key2.getEncoded()
        );
    }

    public List<Transaction> getTransactionsWhereSender() {
        return transactions.stream()
                .filter(tx -> tx.isInput(this.publicKey))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsWhereReceiver() {
        return transactions.stream()
                .filter(tx -> tx.isOutput(this.publicKey))
                .collect(Collectors.toList());
    }

    public double getBalance() {
        double received = getTransactionsWhereReceiver().stream()
                .mapToDouble(tx -> tx.getData().getAmount())
                .sum();

        double sent = getTransactionsWhereSender().stream()
                .mapToDouble(tx -> tx.getData().getAmount())
                .sum();

        return received - sent;
    }
}