import java.io.*;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

public class User_Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private List<Transaction> transactions;
    private String walletName;
    private static final String WALLET_FILE = "wallets.txt";
    private static Map<PublicKey, User_Wallet> walletRegistry = new HashMap<>();     // Static registry to track all wallets by public key




    public User_Wallet(String name) throws Exception {
        this.walletName = name;
        generateKeyPair();
        this.transactions = new ArrayList<>();
        walletRegistry.put(publicKey, this);
        saveToFile();
    }


    // Constructor for loaded wallets
    public User_Wallet(String name, PrivateKey privKey, PublicKey pubKey) {
        this.walletName = name;
        this.privateKey = privKey;
        this.publicKey = pubKey;
        this.transactions = new ArrayList<>();
        walletRegistry.put(pubKey, this);
    }

    private void saveToFile() throws IOException {
        //transactions is not saved here but will add back to the list of transaction once the transaction txt is loaded
        try (PrintWriter writer = new PrintWriter(new FileWriter(WALLET_FILE, true))) {
            //no need to add X509/PKCS8 as it already returns the key in its standard encoded format (PKCS#8 for private keys, X.509 for public keys)
            String pubKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            writer.println(walletName + "|" + pubKey + "|" + privKey);
        }
    }

    // Modified load method
    public static void loadAllWallets() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(WALLET_FILE))) {
            KeyFactory kf = KeyFactory.getInstance("EC");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                String name = parts[0];
                PublicKey pubKey = kf.generatePublic(
                        new X509EncodedKeySpec(Base64.getDecoder().decode(parts[1])));
                PrivateKey privKey = kf.generatePrivate(
                        new PKCS8EncodedKeySpec(Base64.getDecoder().decode(parts[2])));
                new User_Wallet(name, privKey, pubKey);
            }
        }
    }

    // New method to find wallet by name
    public static User_Wallet getWalletByName(String name) {
        return walletRegistry.values().stream()
                .filter(w -> w.walletName.equals(name))
                .findFirst()
                .orElse(null);
    }


    public void printTransactionHistory() {
        System.out.println("\n=== Transaction History for Wallet " + abbreviateKey(publicKey.getEncoded()) + " ===");
        System.out.printf("Current Balance: %.2f\n", getBalance());

        System.out.println("\nSent Transactions:");
        for (Transaction tx : getTransactionsWhereSender()) {
            System.out.println(tx);
            System.out.println(tx.getTransactionId());
        }

        System.out.println("\nReceived Transactions:");
        for (Transaction tx : getTransactionsWhereReceiver()) {
            System.out.println(tx);
            System.out.println(tx.getTransactionId());
        }
    }

    private String abbreviateKey(byte[] key) {
        String full = Base64.getEncoder().encodeToString(key);
        return full.substring(0, 8) + "...";
    }

    public String getShortAddress() {
        return abbreviateKey(publicKey.getEncoded());
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