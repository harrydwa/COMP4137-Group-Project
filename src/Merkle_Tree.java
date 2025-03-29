import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if (i >= NodeList.size()) {
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


    private void calculateLeavesNode() {
        for (int i = 0; i < list.size(); i++) {
            String value = list.get(i).calculateTransactionId();
            leavesnodelist.add(value);
            Node node = new Node(value);
            NodeList.add(node);
        }
        double height = Math.log(NodeList.size() - 1) / Math.log(2);  // using the equation of tree of height H has N =2^H leaf nodes
        for (int i = 1; i < NodeList.size(); i++) {
            NodeList.get(i).height = (int) height;
        }

        hashedonce = true;

    }

    private List<String> calculateRoot(List<String> ProcessList) {
        List<String> list = new ArrayList<>();
        List<String> returnlist = new ArrayList<>();
        int currentindex = 0;
        if (ProcessList.size() == 1) { // if the ProcessList list only contain one element , it will be the root
            root = ProcessList.get(0);
            return ProcessList;
        }
        for (int i = 0; i < ProcessList.size(); i += 2) {// change string to byte
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
        return calculateRoot(returnlist);
    }

    public void calculate() {
        calculateLeavesNode();
        calculateRoot(leavesnodelist);
    }

    public static String generateSHA256Hash(String inputInString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] inputInBytes = digest.digest(inputInString.getBytes());

            StringBuilder hexString = new StringBuilder();
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
}
