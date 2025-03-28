import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.security.Signature;
import java.security.PublicKey;

public class TransactionVerifier {

    //verify with sender's public key + amount
    public static boolean verifySignature(Transaction transaction) {
        try {
            Signature sign = Signature.getInstance("SHA256withECDSA");
            sign.initVerify(transaction.getInput()); //sender's public key

            ByteArrayOutputStream Boutput = new ByteArrayOutputStream();
            DataOutputStream Doutput = new DataOutputStream(Boutput);
            Doutput.writeDouble(transaction.getData().getAmount());
            Doutput.close();
            byte[] amountBytes = Boutput.toByteArray();
            sign.update(amountBytes); //include amount in the final computation

            return sign.verify(transaction.getData().getSignature());
        } catch (Exception e) {
            throw new RuntimeException("Error verifying signature", e);
        }
    }
}