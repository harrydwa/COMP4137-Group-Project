import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Block {

    private BlockHeader header;
    private List<Transaction> transactions;
    private Merkle_Tree merkleTree;

    public Block(String previousHash, List<Transaction> transactions, String data) throws IOException {
        this.transactions = transactions;
        this.header = new BlockHeader(previousHash, data);
        this.merkleTree = new Merkle_Tree(transactions);
        this.merkleTree.calculate();
        this.header.merkleRoot = this.merkleTree.getRoot();
        this.header.hash = this.header.calculateHash();
    }

    public Block(String previousHash, List<Transaction> transactions, String data, String merkleRoot) throws IOException {
        this.transactions = transactions;
        this.header = new BlockHeader(previousHash, data);
        //this.merkleTree = new Merkle_Tree(transactions);
        //this.merkleTree.calculate();
        this.header.merkleRoot = merkleRoot;
        this.header.hash = this.header.calculateHash();
    }

    public String getHash() { return header.hash; }
    public String getPreviousHash() { return header.previousHash; }
    public String getMerkleRoot() { return header.merkleRoot; }
    public long getTimestamp() { return header.timeStamp; }
    public int getNonce() { return header.nonce; }
    public List<Transaction> getTransactions() { return transactions; }
    public BlockHeader getHeader() { return header; }
    public Merkle_Tree getMerkleTree() { return merkleTree; }

    // Save block to a file
    public void saveBlock(String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("HASH:" + header.hash);
            writer.println("PREVIOUS_HASH:" + header.previousHash);
            writer.println("MERKLE_ROOT:" + header.merkleRoot);
            writer.println("TIMESTAMP:" + header.timeStamp);
            writer.println("NONCE:" + header.nonce);
            writer.println("DATA:" + header.data);
            writer.println("TRANSACTIONS:");
            for (Transaction tx : transactions) {
                writer.println(serializeTransaction(tx));
            }
        }
    }

    public void addBlock(String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("HASH:" + header.hash);
            writer.println("PREVIOUS_HASH:" + header.previousHash);
            writer.println("MERKLE_ROOT:" + header.merkleRoot);
            writer.println("TIMESTAMP:" + header.timeStamp);
            writer.println("NONCE:" + header.nonce);
            writer.println("DATA:" + header.data);
            writer.println("TRANSACTIONS:");
            for (int i = 0; i < transactions.size() - 2; i++) {
                writer.println(serializeTransaction(transactions.get(i)));
            }
        }
    }

    // Load block from a file
    public static Block loadBlock(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String hash = null, previousHash = null, merkleRoot = null, data = null;
            long timeStamp = 0;
            int nonce = 0;
            List<Transaction> transactions = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("HASH:")) {
                    hash = line.substring(5);
                } else if (line.startsWith("PREVIOUS_HASH:")) {
                    previousHash = line.substring(14);
                } else if (line.startsWith("MERKLE_ROOT:")) {
                    merkleRoot = line.substring(12);
                } else if (line.startsWith("TIMESTAMP:")) {
                    timeStamp = Long.parseLong(line.substring(10));
                } else if (line.startsWith("NONCE:")) {
                    nonce = Integer.parseInt(line.substring(6));
                } else if (line.startsWith("DATA:")) {
                    data = line.substring(5);
                } else if (line.startsWith("TRANSACTIONS:")) {
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        transactions.add(deserializeTransaction(line));
                    }
                }
            }

            Block block = new Block(previousHash, transactions, data, merkleRoot);
            block.header.hash = hash;
            //block.header.merkleRoot = merkleRoot;
            block.header.timeStamp = timeStamp;
            block.header.nonce = nonce;
            return block;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String serializeTransaction(Transaction tx) {
        return Base64.getEncoder().encodeToString(tx.getInput().getEncoded()) + "," +
                Base64.getEncoder().encodeToString(tx.getOutput().getEncoded()) + "," +
                tx.getData().getAmount() + "," +
                Base64.getEncoder().encodeToString(tx.getData().getSignature());
    }

    private static Transaction deserializeTransaction(String line) throws Exception {
        String[] parts = line.split(",");
        PublicKey sender = deserializePublicKey(parts[0]);
        PublicKey receiver = deserializePublicKey(parts[1]);
        double amount = Double.parseDouble(parts[2]);
        byte[] signature = Base64.getDecoder().decode(parts[3]);

        return new Transaction(sender, receiver, amount, signature);
    }

    private static PublicKey deserializePublicKey(String key) throws Exception {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 encoding for public key: " + key, e);
        } catch (java.security.spec.InvalidKeySpecException e) {
            throw new RuntimeException("Invalid key specification for public key: " + key, e);
        }
    }

}