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
        String dataToHash = previousHash + timeStamp + nonce + data;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(String.format("%02x", b)); // format byte as hex string
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error calculating hash", ex);
        }
    }

    public void mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0'); // "00000" for difficulty 5
        StringBuilder targetBuilder = new StringBuilder("0000"); // 64 hex characters
        for (int i = 0; i < 60; i++) {
            targetBuilder.append('f');
        }
        String target = targetBuilder.toString();

        while (true) {
            hash = calculateHash(); // combined data information

            // Check both prefix and target conditions
            if (hash.substring(0, prefix).equals(prefixString) && // Quick check - just compare first few characters
                    hash.compareTo(target) < 0) {                     // Full check - must compare entire hash string
                return;
            }
            nonce++;
        }
    }

}
