import java.util.Scanner;

public class UI {
    public static void main(String[] args) throws Exception {
        int option = 0;
        Scanner in = new Scanner(System.in);
        String back;
        String[] stringArg = {""};

        System.out.println("Blockchain System");
        while (option != 6) {
            back = "";
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("1: Create Account");
            System.out.println("2: Check Account Wallet");
            System.out.println("3: Transaction Generation");
            System.out.println("4: Verifiable Merkle Tree Of Transactions");
            System.out.println("5: Integrity Verification");
            System.out.println("6: Exit");
            System.out.println("Enter the indicating number: ");
            option = in.nextInt();

            if (option == 1) {
                System.out.println("Create Account");

                //Create account

                while (!back.equals("back")) {
                    System.out.println("Type 'back' to return home screen: ");
                    back = in.next();
                    option = 0;
                }
            } else if (option == 2) {
                System.out.println("Check Account Wallet");

                //Check Account Wallet

                while (!back.equals("back")) {
                    System.out.println("Type 'back' to return home screen: ");
                    back = in.next();
                    option = 0;
                }
            } else if (option == 3) {
                System.out.println("Transaction Generation");

                //Transaction Generation
                Main transaction = new Main();
                transaction.main(stringArg);

                while (!back.equals("back")) {
                    System.out.println("Type 'back' to return home screen: ");
                    back = in.next();
                    option = 0;
                }
            } else if (option == 4) {
                System.out.println("Verifiable Merkle Tree Of Transaction");

                //Verifiable Merkle Tree Of Transaction

                while (!back.equals("back")) {
                    System.out.println("Type 'back' to return home screen: ");
                    back = in.next();
                    option = 0;
                }
            } else if (option == 5) {
                System.out.println("Integrity Verification");

                //Integrity Verification
//                IntegrityTest test = new IntegrityTest();
//                test.main(stringArg);

                while (!back.equals("back")) {
                    System.out.println("Type 'back' to return home screen: ");
                    back = in.next();
                    option = 0;
                }
            }
        }
        System.out.println("Thank you for using! Now exiting!");
    }
}
