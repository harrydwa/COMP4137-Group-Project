import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class BlockHeader {

    public String merkleRoot;
    public String previousHash;
    public long timeStamp;
    public int nonce;
    public String hash;
    public String data;

    public BlockHeader(String previousHash, String data) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String dataToHash = previousHash
                + Long.toString(timeStamp)
                + Integer.toString(nonce)
                + merkleRoot
                + data;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error calculating hash", ex);
        }
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b)); // format byte as hex string
        }
        return buffer.toString();

    }

    public String mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        String hash = calculateHash();

        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        this.hash = hash;
        return hash;
    }

}
