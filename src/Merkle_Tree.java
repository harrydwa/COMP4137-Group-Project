import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Merkle_Tree {
    class Node {
        Node parent;
        Node children1;
        Node children2;
        String value;
        int index;
        int height;

        public Node(String value) {
            this.value = value;
            this.parent = null;
            this.children1 = null;
            this.children2 = null;
            this.index = number++;
            this.height = 0;
        }
    }

    private List<Transaction> list;
    private List<String> leavesnodelist = new ArrayList<>();
    private List<Node> NodeList = new ArrayList<>();
    private String root;
    private boolean hashedonce = false;
    public int number = 0;

    public Merkle_Tree(List<Transaction> list) {
        if (list.isEmpty() || list.size() % 2 == 1) {
            throw new IllegalArgumentException("List should not be empty or odd number ");
        } else {
            this.list = list;
            Node node = new Node(null); // create the first node that easy for calculate
            NodeList.add(node);

        }
    }
    public void Addroottofile() throws IOException {
        FileWriter writer =new FileWriter("root.txt");
        PrintWriter out =new PrintWriter(writer);
        out.println(root);
        out.flush();
        out.close();
    }

    public String getRoot() {
        return root;
    }

    public boolean getHashedOnce() {
        return hashedonce;
    }

    public int getsize() {
        return leavesnodelist.size();
    }

    public int getChildren1(int i) {
        if (i >= NodeList.size()||i<1) {
            return 0;
        }

        Node node = NodeList.get(i);
        if (node.children1 == null) {
            return 0;
        }
        return node.children1.index;

    }

    public int getChildren2(int i) {
        if (i >= NodeList.size()) {
            return 0;
        }
        Node node = NodeList.get(i);
        if (node.children2 == null) {
            return 0;
        }
        return node.children2.index;

    }

    public int getParent(int i) {
        if (i >= NodeList.size()) {
            return 0;
        }
        Node node = NodeList.get(i);
        if (node.parent == null) {
            return 0;
        }
        return node.parent.index;
    }
    public int getHeight(int i){
        Node node = NodeList.get(i);
        return node.height;
    }
    public String getValue(int i){
        Node node = NodeList.get(i);
        return node.value;
    }


    private boolean calculateLeavesNode() throws FileNotFoundException {

        if(checkValidForTransaction()){
            System.out.println("Transaction is valid");
        }
        else{

            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            String value = list.get(i).calculateTransactionId();
            System.out.println(list.size());
            leavesnodelist.add(value);
            Node node = new Node(value);
            NodeList.add(node);
        }
        double height = Math.log(NodeList.size() - 1) / Math.log(2);  // using the equation of tree of height H has N =2^H leaf nodes
        for (int i = 1; i < NodeList.size(); i++) {
            NodeList.get(i).height = (int) height;
        }

        hashedonce = true;
        return true ;

    }
    private boolean checkValidForTransaction() throws FileNotFoundException {
        File inputFile = new File("transactions.txt");
        Scanner scanner = new Scanner(inputFile);
        int index = 0;

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\|");
            System.out.println("the result is " + parts[parts.length - 1]);
            String transactionID = parts[parts.length - 1];

            if (!transactionID.equals(list.get(index).calculateTransactionId())) {
                System.out.println("the transaction in index " + index + " is not valid");
                return false;
            }
            index++;
        }
        if (scanner!=null){
            scanner.close();
        }


        return true;
    }

    private List<String> calculateRoot(List<String> ProcessList) throws FileNotFoundException {
        List<String> list = new ArrayList<>();
        List<String> returnlist = new ArrayList<>();
        int currentindex = 0;

        if (ProcessList.size() == 1) { // if the ProcessList list only contain one element , it will be the root
            root = ProcessList.get(0);
            return ProcessList;
        }

        for (int i = 0; i <ProcessList.size(); i += 2) {
            String appendString = ProcessList.get(i) + ProcessList.get(i + 1);
            list.add(appendString);
        }
        int previousHeight = NodeList.get(NodeList.size() - 1).height;
        for (int i = 1; i < NodeList.size(); i++) {
            if (NodeList.get(i).height == previousHeight) {
                currentindex = i;
                break;
            }

        }
        for (int j = 0; j < list.size(); j++) {
            String value = generateSHA256Hash(list.get(j));
            Node node = new Node(value);
            int currentHeight = previousHeight - 1;
            node.height = currentHeight;
            node.children1 = NodeList.get(currentindex);
            node.children2 = NodeList.get(currentindex + 1);
            NodeList.get(currentindex).parent = node;
            NodeList.get(currentindex + 1).parent = node;
            currentindex = currentindex + 2;
            NodeList.add(node);
            returnlist.add(value);
        }
        if (returnlist.isEmpty()) {
            throw new IllegalStateException("returnlist is empty.");
        }
        return calculateRoot(returnlist);
    }

    public  void calculate() throws IOException {// for the valid , we will open a new method that dont contain addroototofile

        if (calculateLeavesNode()) {
            calculateRoot(leavesnodelist);
            Addroottofile();
        }
        else{
            System.out.println("transaction have been modified , will not generate merkle tree ");
        }
    }

    public static String generateSHA256Hash(String inputInString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] inputInBytes = digest.digest(inputInString.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2*inputInString.length());
            for (byte b : inputInBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public void validmerkletree() throws FileNotFoundException {
        leavesnodelist.clear();
        NodeList.clear();
        Node node = new Node(null);
        NodeList.add(node);
        if (calculateLeavesNode()) {
            calculateRoot(leavesnodelist);
            File inputFile = new File("root.txt");
            Scanner scanner = new Scanner(inputFile);
            String line = scanner.nextLine();
            if(line.equals(root)){
                System.out.println("Merkle tree is valid");
            }
            else{
                System.out.println("Merkle tree is not valid");
            }
            scanner.close();
        }
        else{
            System.out.println("transaction have been modified , will not generate merkle tree ");
        }

    }

    public static void main(String[] args) throws Exception {
        Transaction.loadAllTransactions();
        List<Transaction> allTransactions = Transaction.getTransactionList();
        System.out.println(allTransactions.size() + " transactions loaded.");
        Merkle_Tree merkleTree = new Merkle_Tree(allTransactions);

        try {
            // Call the calculate method on the instance
//            merkleTree.calculate();
            merkleTree.validmerkletree();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
