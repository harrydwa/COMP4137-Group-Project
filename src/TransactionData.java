public class TransactionData {
    private double amount;
    private byte[] signature;

    public TransactionData(double amount, byte[] signature) {
        this.amount = amount;
        this.signature = signature;
    }

    public double getAmount() {
        return amount;
    }

    // use for integrity test
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public byte[] getSignature() {
        return signature;
    }
}