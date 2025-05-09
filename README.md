COMP4137 Blockchain Technology and Applications Programming Project: Implementation of a Mini Blockchain

Group 11
22230343 Ho Li Ming
22236902 Chu Wai Ki
22230424 Chan Yin Tat
22232605 Keut Chun Ting
22233806 Yip Hong Sun

Abstract
Implement the following basic components of blockchain: 
(1) Transaction generation 
(2) Verifiable Merkle tree of transactions 
(3) Construction of blockchain 
(4) Integrity verification of transactions and blockchains.

This artifact contains code and datasets to reproduce the experiment results.

Software architecture:
Wallet Management: User_Wallet class for key pair generation and balance tracking
Transaction System: Creation, signing, and verification of value transfers
Block Processing: Block creation with headers and proof-of-work mining
Verification: Merkle tree implementation and integrity verification system
Txt file for data storage 

Reproducibility:
Running UI.java to test different components

Artifact Dependencies and Requirements

RAM: 4GB minimum (8GB or more recommended)
CPU: Any modern dual-core processor (2GHz+)
Storage: 1GB available space
Network: Basic internet connection for multi-user testing

Operating System: Windows 10/11, macOS 10.14+, Linux (Ubuntu 18.04+)
Java: JDK 8 (1.8) or newer
Dependencies: None (uses only standard Java libraries)
java.security (for MessageDigest, KeyPairGenerator)
java.nio (for file operations)
java.util (for collections)

Datasets are provided with the project (txt files)

Artifact Installation and Deployment 

Prerequisites

Install Java JDK 8 (1.8) or newer
Download from Oracle or use OpenJDK
Verify installation with java -version

Download
Clone or download the project files
git clone [repository-url] or download and extract the zip file

Time estimate: Installation takes 1-5 minutes, depending on Java installation status.

Running the Application
Clone the project in a Java environment and run UI.java 

The command-line interface will display options:
1: Create Account
2: Check Account Wallet
3: Transaction Generation
4: Verifiable Merkle Tree Of Transactions
5: Mining a Block
6: Integrity Verification
7: Exit

First-Time Setup:
Create at least two user wallets (option 1)
Generate transactions between users (option 3)
Verify transactions with the Merkle tree (option 4)
Mine a block to finalize transactions (option 5)

Time estimate: Deployment takes 3-5 minutes after compilation.

Reproducibility of Experiments

Workflow: 

System User Interface:
Display UI menu
Get user input
Option 1: Create Account (Wallet Creation)
Option 2: Check Wallet Amount (Checking a wallet)
Option 3: Transaction Generation (Transaction Creation)
Option 4: Verification of Merkle Tree (Merkle Tree)
Option 5: Mining a Block (Block Mining workflow)
Option 6: Integrity Test (Integrity Test workflow)
Option 7: Exit

User Wallet Workflow
Wallet Creation:
User selects "Create Account" (option 1)
Enters a username
System generates EC keypair (secp256k1 curve)
Wallet details saved to wallets.txt in format: username|publicKey|privateKey

Wallet Loading:
User_Wallet.loadAllWallets() loads all wallets at program startup
Reconstructs key pairs from Base64-encoded strings
Registers wallets in static registry for lookup

Balance Management:
Each wallet starts with 1000.0 balance
Outgoing transactions decrease the balance
Incoming transactions increase the balance

Transaction Workflow
Transaction Creation:
User selects "Transaction Generation" (option 3)
Enters sender username, receiver username, and amount
System verifies sufficient funds
Creates transaction with:
Sender's public key
Receiver's public key
Amount
Digital signature (SHA256withECDSA)
Saves transaction to transaction pool

Transaction Verification:
Each transaction's signature is verified
The transaction ID is calculated using SHA-256 hashing
Displayed to user: "Transaction created successfully! TX ID: [hash]"
Transaction Processing:

When checking a wallet (option 2), users see:
Current balance
Transaction history (both sent and received)
Transaction IDs
Wallet identifier (abbreviated public key)

This forms the foundation of the blockchain's value transfer system, with cryptographic security ensuring transaction authenticity.

Merkle tree :
First , we need to create a merkle object  and contain a list that contain all transactions.
Second ,we need to call the merkle tree method caluclate().
Third, we can calculate the leaves nodes after we valid each transaction.
Fourth , we can calcualte the each non-leaaf node and root node floor by recursion .
Fifth , we will store the root in the file for later check .
Sixth , we can use the method  validmerkletree() to valid the root by calcualte the root again to check whether they are the same .

Block workflow:
Input the previous hash, all transactions and data to create a block. In the block , it will gerenate a block header which include
hash, merkle tree root, previous hash, timestamp, nonce and data. There is a calculate hash function to create the hash using SHA-256. 
Also, there is a mining block function for creating block.
After a block created, it saves in the txt.file for record and it can get the block information by load block function. 

Block Mining workflow:
First, load all transactions.
Second, get the previous hash of the previous block and create a block header.
Then, mine with Proof-of-Work(PoW)
After that, the new block wll be generated and save to a new file.

Integrity Test workflow:
1. Test falsified transactions:
   Get hash of original transactions data
   Modify the transactions data
   Get hash of the modified data
   Compare the hashes of both original transactions data and modified transactions data
2. Test falsified block:
   Get original block data
   Modify the block data
   Get modified block
   Compare the hash value of both original block and modified block
Total Execution Time: 5-15 minutes


References:
General usage:
Comp 3015 Data communication and networking for data output (Data outputstream and Byte [] output stream)
https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html for Java cryptography library

Generating key + Key algorithm: 
https://blog.csdn.net/qq_36319965/article/details/122564782
[https://jenkov.com/tutorials/java-cryptography/index.html](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/security/KeyPairGenerator.html)
https://www.jianshu.com/p/676a0eb33d31
https://blog.csdn.net/mafei852213034/article/details/53319908 for X509EncodedKeySpec、PKCS8EncodedKeySpec for public and private key storage and retrieval

Digital signature:
https://metamug.com/article/security/sign-verify-digital-signature-ecdsa-java.html#google_vignette

Hashing Transaction 
https://blog.csdn.net/cherry_chenrui/article/details/99412886
https://learn.microsoft.com/en-us/dotnet/api/java.security.messagedigest.update?view=net-android-34.0
https://medium.com/@AlexanderObregon/what-is-sha-256-hashing-in-java-0d46dfb83888 for converting bytes array to hexadecimal string 

Verification:
https://jenkov.com/tutorials/java-cryptography/index.html#verifying-a-signature

Convert String to Byte and sha-256:
https://www.baeldung.com/java-string-to-byte-array

calculate log base 2 of an Integer in Java:
https://www.geeksforgeeks.org/how-to-calculate-log-base-2-of-an-integer-in-java/

Concept about Merkle tree:
https://medium.com/@vinayprabhu19/merkel-tree-in-java-b45093c8c6bd
BytesToHex and sha-256:
https://www.baeldung.com/sha-256-hashing-java

Implementing a Block:
https://www.baeldung.com/java-blockchain

