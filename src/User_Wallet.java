import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class User_Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public User_Wallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        generateKeyPair();
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
}