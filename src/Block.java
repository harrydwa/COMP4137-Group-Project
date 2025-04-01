import java.util.List;

public class Block {

    private BlockHeader header;
    private List<Transaction> transactions;
    private Merkle_Tree merkleTree;

    public Block(String previousHash, List<Transaction> transactions, String data) {
        this.transactions = transactions;
        this.header = new BlockHeader(previousHash, data);
        this.merkleTree = new Merkle_Tree(transactions);
        this.merkleTree.calculate();
        this.header.merkleRoot = this.merkleTree.getRoot();
        this.header.hash = this.header.calculateHash();
    }

    public String getHash() { return header.hash; }
    public String getPreviousHash() { return header.previousHash; }
    public String getMerkleRoot() { return header.merkleRoot; }
    public long getTimestamp() { return header.timeStamp; }
    public int getNonce() { return header.nonce; }
    public List<Transaction> getTransactions() { return transactions; }


}