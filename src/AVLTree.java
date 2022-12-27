import java.io.NotActiveException;

/**
 * AVLTree
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 */
public class AVLTree {
    private IAVLNode root; // Root of the AVL tree.
    private IAVLNode min; // Minimum key node of the AVL tree.
    private IAVLNode max; // Maximum key node of the AVL tree.

    /**
     * public boolean empty()
     * Returns true if and only if the tree is empty.
     * Time complexity of O(1).
     */
    public boolean empty() { // O(1)
        return root == null || !root.isRealNode(); // The tree is empty if the root is null or if the root is a virtual node.
    }

    /**
     * public String search(int k)
     * Returns the info of an item with key k if it exists in the tree.
     * otherwise, returns null.
     * Time complexity of O(log(n)).
     */
    public String search(int k) {
        if (this.empty()){ // If the tree is empty then return null.
            return null;
        }
        return searchRec(k, root).getValue(); // Return the value of the returned Node. If the Node is virtual then its value is null.
    }

    /**
     * private IAVLNode searchRec(int k, IAVLNode node)
     * Recursively search for the IAVLNode with key k from the given node, if it exists in its subtree.
     * otherwise, returns a virtual node.
     * Time complexity of O(log(n)).
     */
    private IAVLNode searchRec(int k, IAVLNode node) {
        if (k == node.getKey() || !node.isRealNode()) { // Return the node if its key is k or if it's a virtual node.
            return node;
        }
        if (k < node.getKey()) {
            return searchRec(k, node.getLeft()); // If the node's key is bigger than k, recursively call this function for the left son of the node.
        }
        if (k > node.getKey()) {
            return searchRec(k, node.getRight()); // If the node's key is smaller than k, recursively call this function for the right son of the node.
        }
        return node; // SHOULD NEVER GET HERE.
    }

    /**
     * public int insert(int k, String i)
     * Inserts an item with key k and info i to the AVL tree.
     * The tree must remain valid, i.e. keep its invariants.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Returns -1 if an item with key k already exists in the tree.
     * Time complexity of O(log(n)).
     */
    public int insert(int k, String i) {
        IAVLNode node = new AVLNode(k, i);
        return insertNode(node);
    }

    /**
     * private int insertNode(IAVLNode node)
     * Inserts node to the AVL tree.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Returns -1 if an item with key k already exists in the tree.
     * Time complexity of O(log(n)).
     */
    private int insertNode(IAVLNode node) {
        if (this.empty()) {
            root = node;
            min = root;
            max = root;
            return 0;
        }
        IAVLNode curNode = searchRec(node.getKey(), root); // Search for the position to insert the node into.
        if (node.getKey() == curNode.getKey()){ // If a node with the same key is already in the tree, return -1.
            return -1;
        }
        curNode = curNode.getParent();
        boolean isLeft = node.getKey() < curNode.getKey(); // Check if the node should be inserted as a left child of its parent.
        setParentSon(curNode, node, isLeft); // Sets the node to be the son of its parent.
        if (min == null || node.getKey() < min.getKey()){ // Update min if necessary.
            min = node;
        }
        else if (max == null || node.getKey() > max.getKey()){ // Update min if necessary.
            max = node;
        }
        return balanceFromNode(curNode, false); // Run balanceFromNode, not from Join, and return the returned value.
    }

    /**
     * public int delete(int k)
     * Deletes an item with key k from the binary tree, if it is there.
     * The tree must remain valid, i.e. keep its invariants.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Returns -1 if an item with key k was not found in the tree.
     * Time complexity of O(log(n)).
     */
    public int delete(int k) {
        if (this.empty()){ // If the tree is empty then an item with key k isn't in the tree.
            return -1;
        }
        IAVLNode nodeToBeDeleted = searchRec(k, root); // Search for the node with key k.
        if (!nodeToBeDeleted.isRealNode()){ // If the node isn't in the tree.
            return -1;
        }
        if (nodeToBeDeleted == min){ // If the deleted node is the min then update the min to its successor.
            min = successor(min);
        }
        if (nodeToBeDeleted == max){ // If the deleted node is the max then update the max to its predecessor.
            max = predecessor(max);
        }
        return deleteNode(nodeToBeDeleted); // Run the recursive call deleteNode.
    }

    /**
     * private int deleteNode(IAVLNode node)
     * Deletes the given node from the tree.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Time complexity of O(log(n)).
     */
    private int deleteNode(IAVLNode node) {
        boolean isLeft = node.getParent() != null && node == node.getParent().getLeft();  // Check if the node is the left child of its parent.
        if (node.getLeft().isRealNode() && node.getRight().isRealNode()) { // If the node has 2 sons.
            IAVLNode suc = successor(node); // suc can't be null because node has a right son.
            IAVLNode tmpNode = new AVLNode(suc.getKey(), suc.getValue()); // Create a node with the key and value of the successor node.
            tmpNode.setHeight(node.getHeight()); // Set the height of the new node to the height of the current node.
            setParentSon(tmpNode, node.getLeft(), true); // Set the parent and sons of node to be the parent and sons of the duplicated successor node.
            setParentSon(tmpNode, node.getRight(), false);
            setParentSon(node.getParent(), tmpNode, isLeft);
            return deleteNode(suc); // Call this function on the successor node, and delete it instead.
        } else if (node.getLeft().isRealNode()) { // If the node only has a left son.
            IAVLNode left = node.getLeft();
            node = node.getParent();
            setParentSon(node, left, isLeft); // Set the parent of node to be the parent of the left son of node instead.
        } else if (node.getRight().isRealNode()) { // If the node only has a right son.
            IAVLNode right = node.getRight();
            node = node.getParent();
            setParentSon(node, right, isLeft); // Set the parent of node to be the parent of the right son of node instead.
        } else { // If the node is a leaf node.
            node = node.getParent();
            setParentSon(node, new AVLNode(), isLeft); // Set the parent of node to be the parent of a virtual node instead.
        }
        return balanceFromNode(node, false); // Run balanceFromNode, not from Join, and return the returned value.
    }

    /**
     * private int balanceFromNode(IAVLNode nodeToBalanceFrom, boolean cameFromJoin){
     * A method that balances the tree from the given nodeToBalanceFrom.
     * If the method is called from join then it should act a bit differently.
     * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
     * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
     * Time complexity of O(log(n)).
     */
    private int balanceFromNode(IAVLNode nodeToBalanceFrom, boolean cameFromJoin){
        int cntBalanceOp = 0;
        while (nodeToBalanceFrom != null) { // While the current node exists
            int rankLeft = nodeToBalanceFrom.getHeight() - nodeToBalanceFrom.getLeft().getHeight(); // Get the rank between the node and its left son.
            int rankRight = nodeToBalanceFrom.getHeight() - nodeToBalanceFrom.getRight().getHeight(); // Get the rank between the node and its right son.
            if ((rankLeft == 1 && rankRight == 1) || (rankLeft == 2 && rankRight == 1) || (rankLeft == 1 && rankRight == 2)) { // If the current node is balanced.
                updateNodesSize(nodeToBalanceFrom); // Update the sizes of the nodes in the path from the current node to the root.
                if (!cameFromJoin) { // If the method wasn't called from join the tree must be balanced, so return the count of the balance operations.
                    return cntBalanceOp;
                }
                nodeToBalanceFrom = nodeToBalanceFrom.getParent(); // Set the current node's parent as the current node.
            }
            // Balance After Insert
            if ((rankLeft == 0 && rankRight == 1) || (rankLeft == 1 && rankRight == 0)) {
                nodeToBalanceFrom.calculateSize(); // Update the size of the current node.
                promote(nodeToBalanceFrom); // Promote the current node.
                cntBalanceOp++; // Add 1 to the count of balance operations (for promoting).
                nodeToBalanceFrom = nodeToBalanceFrom.getParent(); // Set the current node's parent as the current node.
            }
            if (rankLeft == 0 && rankRight == 2) {
                int rankLeftLeft = nodeToBalanceFrom.getLeft().getHeight() - nodeToBalanceFrom.getLeft().getLeft().getHeight(); // Get the rank between the node's left son and its left son.
                int rankLeftRight = nodeToBalanceFrom.getLeft().getHeight() - nodeToBalanceFrom.getLeft().getRight().getHeight(); // Get the rank between the node's left son and its right son.
                if (rankLeftLeft == 1 && rankLeftRight == 2) {
                    RotateRight(nodeToBalanceFrom); // Rotate right from the current node.
                    demote(nodeToBalanceFrom); // Demote the current node.
                    cntBalanceOp += 2; // Add 2 to the count of balance operations (for rotating and demoting).
                    updateNodesSize(nodeToBalanceFrom); // Update the sizes of the nodes in the path from the current node to the root.
                    if (!cameFromJoin) { // If the method wasn't called from join the tree must be balanced, so return the count of the balance operations.
                        return cntBalanceOp;
                    }
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).

                }
                if (rankLeftLeft == 2 && rankLeftRight == 1) {
                    RotateLeft(nodeToBalanceFrom.getLeft()); // Rotate left from the node's left son.
                    RotateRight(nodeToBalanceFrom); // Rotate right from the current node.
                    demote(nodeToBalanceFrom.getParent().getLeft()); // Demote the node's parent's left son.
                    demote(nodeToBalanceFrom); // Demote the current node.
                    promote(nodeToBalanceFrom.getParent()); // Promote the parent of the current node.
                    cntBalanceOp += 5; // Add 5 to the count of balance operations (for double rotating and 3 promotions and demotions).
                    updateNodesSize(nodeToBalanceFrom); // Update the sizes of the nodes in the path from the current node to the root.
                    if (!cameFromJoin) { // If the method wasn't called from join the tree must be balanced, so return the count of the balance operations.
                        return cntBalanceOp;
                    }
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
                if (rankLeftLeft == 1 && rankLeftRight == 1){
                    nodeToBalanceFrom.calculateSize(); // Update the size of the current node.
                    RotateRight(nodeToBalanceFrom); // Rotate right from the current node.
                    promote(nodeToBalanceFrom.getParent()); // Promote the parent of the current node.
                    cntBalanceOp += 2; // Add 2 to the count of balance operations (for rotating and promoting).
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
            }
            if (rankLeft == 2 && rankRight == 0) {
                int rankRightLeft = nodeToBalanceFrom.getRight().getHeight() - nodeToBalanceFrom.getRight().getLeft().getHeight(); // Get the rank between the node's right son and its left son.
                int rankRightRight = nodeToBalanceFrom.getRight().getHeight() - nodeToBalanceFrom.getRight().getRight().getHeight(); // Get the rank between the node's right son and its right son.
                if (rankRightLeft == 2 && rankRightRight == 1) {
                    RotateLeft(nodeToBalanceFrom); // Rotate right from the current node.
                    demote(nodeToBalanceFrom); // Demote the current node.
                    cntBalanceOp += 2; // Add 2 to the count of balance operations (for rotating and demoting).
                    updateNodesSize(nodeToBalanceFrom); // Update the sizes of the nodes in the path from the current node to the root.
                    if (!cameFromJoin) { // If the method wasn't called from join the tree must be balanced, so return the count of the balance operations.
                        return cntBalanceOp;
                    }
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
                if (rankRightLeft == 1 && rankRightRight == 2) {
                    RotateRight(nodeToBalanceFrom.getRight()); // Rotate right from the node's right son.
                    RotateLeft(nodeToBalanceFrom); // Rotate left from the current node.
                    demote(nodeToBalanceFrom.getParent().getRight()); // Demote the node's parent's right son.
                    demote(nodeToBalanceFrom); // Demote the current node.
                    promote(nodeToBalanceFrom.getParent()); // Promote the parent of the current node.
                    cntBalanceOp += 5; // Add 5 to the count of balance operations (for double rotating and 3 promotions and demotions).
                    updateNodesSize(nodeToBalanceFrom); // Update the sizes of the nodes in the path from the current node to the root.
                    if (!cameFromJoin) { // If the method wasn't called from join the tree must be balanced, so return the count of the balance operations.
                        return cntBalanceOp;
                    }
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
                if (rankRightRight == 1 && rankRightLeft == 1){
                    nodeToBalanceFrom.calculateSize(); // Update the size of the current node.
                    RotateLeft(nodeToBalanceFrom); // Rotate left from the current node.
                    promote(nodeToBalanceFrom.getParent()); // Promote the parent of the current node.
                    cntBalanceOp += 2; // Add 2 to the count of balance operations (for rotating and promoting).
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
            }
            // Balance After Delete
            if (rankLeft == 2 && rankRight == 2) {
                nodeToBalanceFrom.calculateSize(); // Update the size of the current node.
                demote(nodeToBalanceFrom); // Demote the current node.
                cntBalanceOp++; // Add 1 to the count of balance operations (for demoting).
                nodeToBalanceFrom = nodeToBalanceFrom.getParent(); // Set the current node's parent as the current node.
            }
            if (rankLeft == 3 && rankRight == 1) {
                int rankRightLeft = nodeToBalanceFrom.getRight().getHeight() - nodeToBalanceFrom.getRight().getLeft().getHeight(); // Get the rank between the node's right son and its left son.
                int rankRightRight = nodeToBalanceFrom.getRight().getHeight() - nodeToBalanceFrom.getRight().getRight().getHeight(); // Get the rank between the node's right son and its right son.
                if (rankRightLeft == 1 && rankRightRight == 1){
                    RotateLeft(nodeToBalanceFrom);  // Rotate left from the current node.
                    demote(nodeToBalanceFrom); // Demote the current node.
                    promote(nodeToBalanceFrom.getParent()); // Promote the parent of the current node.
                    updateNodesSize(nodeToBalanceFrom); // Update the sizes of the nodes in the path from the current node to the root.
                    cntBalanceOp += 3; // Add 1 to the count of balance operations (for rotating, demoting, and promoting).
                    if (!cameFromJoin) { // If the method wasn't called from join the tree must be balanced, so return the count of the balance operations.
                        return cntBalanceOp;
                    }
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
                if (rankRightLeft == 2 && rankRightRight == 1){
                    nodeToBalanceFrom.calculateSize(); // Update the size of the current node.
                    RotateLeft(nodeToBalanceFrom); // Rotate left from the current node.
                    demote(nodeToBalanceFrom); // Demote the current node twice.
                    demote(nodeToBalanceFrom);
                    cntBalanceOp += 2; // Add 2 to the count of balance operations (for rotating and double demoting).
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
                if (rankRightLeft == 1 && rankRightRight == 2){
                    nodeToBalanceFrom.calculateSize(); // Update the size of the current node.
                    RotateRight(nodeToBalanceFrom.getRight()); // Rotate right from the node's right son.
                    RotateLeft(nodeToBalanceFrom); // Rotate left from the current node.
                    demote(nodeToBalanceFrom); // Demote the current node twice.
                    demote(nodeToBalanceFrom);
                    promote(nodeToBalanceFrom.getParent()); // Promote the parent of the current node.
                    demote(nodeToBalanceFrom.getParent().getRight()); // Demote the node's parent's right son.
                    cntBalanceOp += 5; // Add 5 to the count of balance operations (for double rotating, double demoting, promoting and demoting).
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
            }
            if (rankLeft == 1 && rankRight == 3) {
                int rankLeftLeft = nodeToBalanceFrom.getLeft().getHeight() - nodeToBalanceFrom.getLeft().getLeft().getHeight(); // Get the rank between the node's left son and its left son.
                int rankLeftRight = nodeToBalanceFrom.getLeft().getHeight() - nodeToBalanceFrom.getLeft().getRight().getHeight(); // Get the rank between the node's left son and its right son.
                if (rankLeftLeft == 1 && rankLeftRight == 1){
                    RotateRight(nodeToBalanceFrom); // Rotate right from the current node.
                    demote(nodeToBalanceFrom); // Demote the current node.
                    promote(nodeToBalanceFrom.getParent()); // Promote the parent of the current node.
                    updateNodesSize(nodeToBalanceFrom); // Update the sizes of the nodes in the path from the current node to the root.
                    cntBalanceOp += 3; // Add 3 to the count of balance operations (for double rotating, demoting, and promoting).
                    if (!cameFromJoin) { // If the method wasn't called from join the tree must be balanced, so return the count of the balance operations.
                        return cntBalanceOp;
                    }
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
                if (rankLeftRight == 2 && rankLeftLeft == 1){
                    nodeToBalanceFrom.calculateSize(); // Update the size of the current node.
                    RotateRight(nodeToBalanceFrom); // Rotate right from the current node.
                    demote(nodeToBalanceFrom); // Demote the current node twice.
                    demote(nodeToBalanceFrom);
                    cntBalanceOp += 2; // Add 2 to the count of balance operations (for rotating and double demoting).
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
                if (rankLeftRight == 1 && rankLeftLeft == 2){
                    nodeToBalanceFrom.calculateSize(); // Update the size of the current node.
                    RotateLeft(nodeToBalanceFrom.getLeft()); // Rotate left from the current node's left son.
                    RotateRight(nodeToBalanceFrom); // Rotate right from the current node.
                    demote(nodeToBalanceFrom); // Demote the current node twice.
                    demote(nodeToBalanceFrom);
                    promote(nodeToBalanceFrom.getParent()); // Promote the parent of the current node.
                    demote(nodeToBalanceFrom.getParent().getLeft()); // Demote the current node's parent's left son.
                    cntBalanceOp += 5; // Add 5 to the count of balance operations (for double rotating, double demoting, promoting and demoting).
                    nodeToBalanceFrom = nodeToBalanceFrom.getParent().getParent(); // Set the current node's parent's parent as the current node (because of the rotation).
                }
            }
            cameFromJoin = false; // If the method was called from join, treat it as a regular call for the next nodes.
        }
        return cntBalanceOp; // If the method was called through the whole branch from the initial node to the root, then the tree is balanced.
    }

    /**
     * public String min()
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty.
     * Time complexity of O(1).
     */
    public String min() {
        if (this.empty()){ // If the tree is empty then return null.
            return null;
        }
        return this.min.getValue();
    }

    /**
     * private void updateMin()
     * Searches for the current min of the tree and update the field min.
     * Time complexity of O(log(n)).
     */
    private void updateMin(){
        if (!this.empty()) {
            IAVLNode node = root;
            while (node.getLeft().isRealNode()) { // Go down the left branch of the tree.
                node = node.getLeft();
            }
            min = node;
        }
    }

    /**
     * public String max()
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty.
     * Time complexity of O(1).
     */
    public String max() {
        if (this.empty()){ // If the tree is empty then return null.
            return null;
        }
        return this.max.getValue();
    }

    /**
     * private void updateMax()
     * Searches for the current max of the tree and update the field max.
     * Time complexity of O(log(n)).
     */
    private void updateMax(){
        if (!this.empty()) {
            IAVLNode node = root;
            while (node.getRight().isRealNode()) { // Go down the right branch of the tree.
                node = node.getRight();
            }
            max = node;
        }
    }

    /**
     * public int[] keysToArray()
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     * Time complexity of O(n).
     */
    public int[] keysToArray() {
        int[] output = new int[this.size()];
        if (this.empty()){ // If the tree is empty then return an array of zeroes.
            return output;
        }
        keyToArrayRec(root, 0, output); // Use keyToArrayRec to update the output array.
        return output;
    }

    /**
     * private int keyToArrayRec(IAVLNode node, int curPos, int[] output)
     * A recursive method that updates the output array with the keys in the tree in ascending order.
     * node is the currently checked node, and curPos is the current position in the output array.
     * Returns the current position in the output array.
     * Time complexity of O(n).
     */
    private int keyToArrayRec(IAVLNode node, int curPos, int[] output) {
        if (!node.getLeft().isRealNode() && !node.getRight().isRealNode()) { // If the current node is a leaf node.
            output[curPos] = node.getKey(); // Set the value of the current position in the array to the key of the node.
            return curPos + 1; // Return the next position in the array.
        }
        if (node.getLeft().isRealNode()) { // If the node has a left son.
            curPos = keyToArrayRec(node.getLeft(), curPos, output); // Call this method on the left son of this node, and update the current position.
        }
        output[curPos] = node.getKey(); // Set the value of the current position in the array to the key of the node.
        curPos++; // Update the position in the array.
        if (node.getRight().isRealNode()) { // If the node has a right son.
            curPos = keyToArrayRec(node.getRight(), curPos, output); // Call this method on the right son of this node, and update the current position.
        }
        return curPos; // Return the position in the array.
    }

    /**
     * public String[] infoToArray()
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     * Time complexity of O(n).
     */
    public String[] infoToArray() {
        String[] output = new String[this.size()];
        if (this.empty()){ // If the tree is empty then return an array of nulls.
            return output;
        }
        infoToArrayRec(root, 0, output); // Use infoToArrayRec to update the output array.
        return output;
    }

    /**
     * private int infoToArrayRec(IAVLNode node, int curPos, String[] output)
     * A recursive method that updates the output array with the values in the tree in ascending keys order.
     * node is the currently checked node, and curPos is the current position in the output array.
     * Returns the current position in the output array.
     * Time complexity of O(n).
     */
    private int infoToArrayRec(IAVLNode node, int curPos, String[] output) {
        if (!node.getLeft().isRealNode() && !node.getRight().isRealNode()) { // If the current node is a leaf node.
            output[curPos] = node.getValue(); // Set the value of the current position in the array to the value of the node.
            return curPos + 1; // Return the next position in the array.
        }
        if (node.getLeft().isRealNode()) { // If the node has a left son.
            curPos = infoToArrayRec(node.getLeft(), curPos, output); // Call this method on the left son of this node, and update the current position.
        }
        output[curPos] = node.getValue(); // Set the value of the current position in the array to the value of the node.
        curPos++; // Update the position in the array.
        if (node.getRight().isRealNode()) {
            curPos = infoToArrayRec(node.getRight(), curPos, output); // Call this method on the right son of this node, and update the current position.
        }
        return curPos; // Return the position in the array.
    }

    /**
     * public int size()
     * Returns the number of nodes in the tree.
     * Time complexity of O(1).
     */
    public int size() {
        if (this.empty()){ // If the tree is empty, return 0.
            return 0;
        }
        return root.getSize(); // Return the size of the root Node.
    }

    /**
     * public int getRoot()
     * Returns the root AVL node, or null if the tree is empty
     * Time complexity of O(1).
     */
    public IAVLNode getRoot() {
        return this.root;
    }

    /**
     * public AVLTree[] split(int x)
     * splits the tree into 2 trees according to the key x.
     * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
     * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
     * post condition: none
     * Time complexity of O(log(n)).
     */
    public AVLTree[] split(int x)
    {
        IAVLNode nodeToSplit = searchRec(x, this.root); // Search for the node with the key k.
        AVLTree biggerTree = new AVLTree(); // Create an empty tree for the keys bigger than k.
        AVLTree smallerTree = new AVLTree(); // Create an empty tree for the keys smaller than k.
        if (nodeToSplit.getRight().isRealNode()){ // If the node has a right son.
            biggerTree.root = nodeToSplit.getRight(); // Set the root of biggerTree to the right son of node.
            disconnectFromParent(biggerTree.root); // Disconnect the right son of node from its parent.
        }
        if (nodeToSplit.getLeft().isRealNode()){ // If the node has a right son.
            smallerTree.root = nodeToSplit.getLeft(); // Set the root of smallerTree to the left son of node.
            disconnectFromParent(smallerTree.root); // Disconnect the left son of node from its parent.
        }
        while (nodeToSplit.getParent() != null){ // While there are still nodes in the original tree.
            AVLTree treeToJoin = new AVLTree(); // Create a new tree.
            boolean isLeft = nodeToSplit.getParent().getLeft() == nodeToSplit; // Check if the current node is the left son of its parent.
            nodeToSplit = nodeToSplit.getParent(); // Set the current node to be the parent of the current node.
            IAVLNode duplicatedNode = new AVLNode(nodeToSplit.getKey(), nodeToSplit.getValue()); // Duplicate the current node.
            IAVLNode right = nodeToSplit.getRight(); // Get the right son of the current node.
            IAVLNode left = nodeToSplit.getLeft(); // Get the left son of the current node.
            disconnectFromParent(right); // Disconnect the right son from the current node.
            disconnectFromParent(left); // Disconnect the left son from the current node.
            if (isLeft){ // If the current node is the left son of its parent.
                treeToJoin.root = right; // Set the right son as the root of the tree to join.
                biggerTree.join(duplicatedNode, treeToJoin); // Join the biggerTree tree with the tree to join, with the duplicate of the current node as the node to join from.
            }
            else{ // If the current node is the right son of its parent.
                treeToJoin.root = left; // Set the left son as the root of the tree to join.
                smallerTree.join(duplicatedNode, treeToJoin); // Join the smallerTree tree with the tree to join, with the duplicate of the current node as the node to join from.
            }
        }
        biggerTree.updateMax(); // Update the maximum of the biggerTree tree after the tree is complete.
        biggerTree.updateMin(); // Update the minimum of the biggerTree tree after the tree is complete.
        smallerTree.updateMax(); // Update the maximum of the smallerTree tree after the tree is complete.
        smallerTree.updateMin(); // Update the maximum of the smallerTree tree after the tree is complete.
        return new AVLTree[]{smallerTree, biggerTree}; // Return the trees.
    }

    /**
     * private void disconnectFromParent(IAVLNode node)
     * Disconnects the given node from its parent.
     * Time complexity of O(1).
     */
    private void disconnectFromParent(IAVLNode node){
        IAVLNode parent = node.getParent(); // Get the parent of the node.
        if (parent != null){ // If the parent exists.
            if (parent.getLeft() == node){ // If the node is the left son of its parent then set the parent's left son to a virtual node.
                parent.setLeft(new AVLNode());
            }
            else{ // If the node is the left son of its parent then set the parent's left son to a virtual node.
                parent.setRight(new AVLNode());
            }
            node.setParent(null); // Set the parent of the node to null.
        }
    }

    /**
     * public int join(IAVLNode x, AVLTree t)
     * joins t and x with the tree.
     * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
     * precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
     * post condition: none
     * Time complexity of O(|tree.rank - t.rank|).
     */
    public int join(IAVLNode x, AVLTree t)
    {
        int heightDifference;
        /**
         * If one the trees to join is empty, insert node x to the other tree.
         */
        if (this.empty()){
            this.root = t.getRoot(); // Update the root of the tree.
            this.min = t.min; // Update the min of the tree.
            this.max = t.max; // Update the max of the tree.
            heightDifference = t.getRoot().getHeight() + 1;
            insertNode(x);
            return heightDifference;
        }
        if (t.empty()){
            heightDifference = this.getRoot().getHeight() + 1;
            insertNode(x);
            return heightDifference;
        }
        AVLTree smallerKeysTree;
        AVLTree biggerKeysTree;
        /**
         * Check which of the trees is the tree with bigger values.
         */
        if (this.root.getKey() > x.getKey()){
            smallerKeysTree = t;
            biggerKeysTree = this;
        }
        else{
            smallerKeysTree = this;
            biggerKeysTree = t;
        }

        IAVLNode smallerKeysNode = smallerKeysTree.getRoot();
        IAVLNode biggerKeysNode = biggerKeysTree.getRoot();
        int smallerRank = smallerKeysNode.getHeight();
        int biggerRank = biggerKeysNode.getHeight();
        heightDifference = Math.abs(smallerRank - biggerRank) + 1;
        this.min = smallerKeysTree.min;
        this.max = biggerKeysTree.max;

        if (biggerRank == smallerRank){ // If the trees are balanced (same height) then make x the root, its left son as smallerKeysTree and its right son as biggerKeysTree.
            setParentSon(x, smallerKeysTree.getRoot(), true);
            setParentSon(x, biggerKeysTree.getRoot(), false);
            this.root = x;
            x.setHeight(biggerRank + 1);
        }

        else if (biggerRank > smallerRank){
            while (biggerKeysNode.getHeight() > smallerKeysNode.getHeight() + 2){ // Sets biggerKeysNode as the node (along its parents) with the height of smallerKeysNode+2.
                biggerKeysNode = biggerKeysNode.getLeft();
            }
            setParentSon(x, smallerKeysTree.getRoot(), true); // Sets the left son of x as the root of smallerKeysTree.
            setParentSon(x, biggerKeysNode.getLeft(), false); // Sets the right son of x as the left son of biggerKeysNode.
            setParentSon(biggerKeysNode, x, true); // Sets the left son of biggerKeysNode as x.
            this.root = biggerKeysTree.getRoot(); // Sets the root as the root of the biggerKeysTree.
            x.setHeight(biggerKeysNode.getHeight()); // Sets the height of the tree.
        }
        else if (biggerRank < smallerRank) {
            while (smallerKeysNode.getHeight() > biggerKeysNode.getHeight() + 2) { // Sets smallerKeysNode as the node (along its parents) with the height of biggerKeysNode+2.
                smallerKeysNode = smallerKeysNode.getRight();
            }
            setParentSon(x, smallerKeysNode.getRight(), true); // Sets the left son of x as the right son of smallerKeysNode.
            setParentSon(x, biggerKeysTree.getRoot(), false); // Sets the right son of x as the root of biggerKeysTree.
            setParentSon(smallerKeysNode, x, false); // Sets the right son of smallerKeysNode as x.
            this.root = smallerKeysTree.getRoot(); // Sets the root as the root of the smallerKeysTree.
            x.setHeight(smallerKeysNode.getHeight()); // Sets the height of the tree.
        }
        balanceFromNode(x, true); // Balance the tree after joining the trees.
        return heightDifference;
    }

    /**
     * private void promote(IAVLNode node)
     * Add 1 to the height of the given node, and set the height of the current node to it.
     * Time Complexity of O(1).
     */
    private void promote(IAVLNode node) {
        node.setHeight(node.getHeight() + 1);
    }

    /**
     * private void demote(IAVLNode node)
     * Subtract 1 from the height of the given node, and set the height of the current node to it.
     * Time Complexity of O(1).
     */
    private void demote(IAVLNode node) {
        node.setHeight(node.getHeight() - 1);
    }

    /**
     * private void RotateLeft(IAVLNode node)
     * Rotates left from the given node.
     * Time Complexity of O(1).
     */
    private void RotateLeft(IAVLNode node) {
        IAVLNode parent = node.getParent();
        boolean isLeft = parent != null && parent.getLeft() == node; // Checks if the current node is a left son.
        IAVLNode right = node.getRight();
        setParentSon(node, right.getLeft(), false); // Sets the son of the current node as the left son of the right son of the current node.
        setParentSon(right, node, true); // Sets the son of the right son of the current node as the current node.
        setParentSon(parent, right, isLeft); // Sets the son (based on isLeft) of the parent of the current node as the right son of the current node.
        if (node == this.root){      // If the node is the root
            root = node.getParent(); // update the root with the updated parent of the current node.
        }
    }

    /**
     * private void RotateRight(IAVLNode node)
     * Rotates right from the given node.
     * Time Complexity of O(1).
     */
    private void RotateRight(IAVLNode node) {
        IAVLNode parent = node.getParent();
        boolean isLeft = parent != null && parent.getLeft() == node; // Checks if the current node is a left son.
        IAVLNode left = node.getLeft();
        setParentSon(node, left.getRight(), true); // Sets the left son of the current node as the right son of the left son of the current node.
        setParentSon(left, node, false);// Sets the right son of the left son of the current node as the current node.
        setParentSon(parent, left, isLeft); // Sets the son (based on isLeft) of the parent of the current node as the left son of the current node.
        if (node == this.root){      // If the node is the root
            root = node.getParent(); // update the root with the updated parent of the current node.
        }
    }

    /**
     * private IAVLNode successor(IAVLNode node)
     * Returns the value of the next smallest node after the current node.
     * Should be used to update min, and to find the node that replaces the node deleted in delete.
     * Time Complexity of O(1).
     */
    private IAVLNode successor(IAVLNode node) {
        if (node.getRight().isRealNode()) { // If the node has a right son, go to it.
            node = node.getRight();
            while (node.getLeft().isRealNode()) { // Then, as long as the current has a left son go to it.
                node = node.getLeft();
            }
            return node; // Returns the max of the subtree under the right son of the given node.
        }
        while (node.getParent() != null) {
            if (node == node.getParent().getLeft()) { // If the given node doesn't have a left son, go to the current node's parent as long as it is its left son.
                return node.getParent();
            }
            node = node.getParent();
        }
        return null;
    }

    /**
     * private IAVLNode predecessor(IAVLNode node)
     * Returns the value of the next largest node after the current node.
     * Should be used to update max.
     * Time Complexity of O(1)
     */
    private IAVLNode predecessor(IAVLNode node){
        if (node.getLeft().isRealNode()) {
            node = node.getLeft(); // If the node has a left son, go to it.
            while (node.getRight().isRealNode()) {
                node = node.getRight(); // Then, as long as the current has a right son go to it.
            }
            return node; // Returns the min of the subtree under the left son of the given node.
        }
        while (node.getParent() != null) {
            if (node == node.getParent().getRight()) { // If the given node doesn't have a left son, go to the current node's parent as long as it is its left son.
                return node.getParent();
            }
            node = node.getParent();
        }
        return null;
    }

    /**
     * private void setParentSon(IAVLNode parent, IAVLNode son, boolean isLeft)
     * Sets the son to be the right or left son of the given parent based on isLeft.
     * As well as setting the parent of the given son to be the given parent.
     * Time Complexity of O(1)
     */
    private void setParentSon(IAVLNode parent, IAVLNode son, boolean isLeft){
        if (parent == null){ // If the parent is null this means the node should be the root of the tree.
            this.root = son; // (because the parent of the root is always null).
        }
        else if(isLeft){
            parent.setLeft(son);
        }
        else {
            parent.setRight(son);
        }
        son.setParent(parent);
    }

    /**
     * private void updateNodesSize(IAVLNode node)
     * Updates the sizes of the nodes from the given node to the root.
     * Time Complexity of O(log(n))
     */
    private void updateNodesSize(IAVLNode node){
        while (node != null){
            node.calculateSize();
            node = node.getParent();
        }
    }

    /**
     * public interface IAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */
    public interface IAVLNode {
        public int getKey(); // Returns node's key (for virtual node return -1).

        public String getValue(); // Returns node's value [info], for virtual node returns null.

        public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.

        public void setLeft(IAVLNode node); // Sets left child.

        public IAVLNode getRight(); // Returns right child, if there is no right child return null.

        public void setRight(IAVLNode node); // Sets right child.

        public IAVLNode getParent(); // Returns the parent, if there is no parent return null.

        public void setParent(IAVLNode node); // Sets parent.

        public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.

        public int getHeight(); // Returns the height of the node (-1 for virtual nodes).

        public void setHeight(int height); // Sets the height of the node.

        public int getSize(); // Returns the size of the subtree under the node (including the node itself).

        public void calculateSize(); // Calculates the size of the subtree under the current node, including itself.
    }

    /**
     * public class AVLNode implements IAVLNode
     * Node used in the AVLTree.
     */
    public class AVLNode implements IAVLNode {
        private int key; // The key of the node.
        private String info; // The info of the node.
        private IAVLNode left; // The left son of the node.
        private IAVLNode right; // The right son of the node.
        private IAVLNode parent; // The parent of the node.
        private int height; // The height of the subtree under the node, including itself.
        private int size; // The size of the subtree under the node, including itself.

        /**
         * public AVLNode()
         * Creates a new virtual node by calling AVLNode(key, info).
         * Time complexity of O(1).
         */
        public AVLNode() {
            this(-1, null);
        }

        /**
         * public AVLNode(int key, String info)
         * Constructor for node with given key and value.
         *
         * If the key is -1 creates a virtual node, with height -1, and value = null.
         *
         * If not, sets the key and value to the given key and value.
         * Sets the left and right sons as virtual nodes (as well as setting the sons' parents as the current node).
         * And also sets height as 0, sets size as 1.
         *
         * Time complexity of O(1).
         */
        public AVLNode(int key, String info) {
            this.key = key;
            this.info = info;
            if (this.key != -1) {
                this.left = new AVLNode();
                this.right = new AVLNode();
                this.left.setParent(this);
                this.right.setParent(this);
                this.height = 0;
                this.size = 1;
            } else {
                this.height = -1;
            }
        }

        /**
         * public int getKey()
         * Returns the key of the node.
         * Time complexity of O(1).
         */
        public int getKey() {
            return this.key;
        }

        /**
         * public String getValue()
         * Returns the info of the node.
         * Time complexity of O(1).
         */
        public String getValue() {
            return this.info;
        }

        /**
         * public IAVLNode getLeft()
         * Returns the left node.
         * Time complexity of O(1).
         */
        public IAVLNode getLeft() {
            return this.left;
        }

        /**
         * public void setLeft(IAVLNode node)
         * Updates the left node, and updates the size of the subtree under the current node (including this node).
         * Time complexity of O(1).
         */
        public void setLeft(IAVLNode node) {
            this.left = node;
            calculateSize();
        }

        /**
         * public IAVLNode getRight()
         * Returns the right node.
         * Time complexity of O(1).
         */
        public IAVLNode getRight() {
            return this.right;
        }

        /**
         * public void setRight(IAVLNode node)
         * Updates the right node, and updates the size of the subtree under the current node (including this node).
         * Time complexity of O(1).
         */
        public void setRight(IAVLNode node) {
            this.right = node;
            calculateSize();
        }

        /**
         * public IAVLNode getParent()
         * Returns parent.
         * Time complexity of O(1).
         */
        public IAVLNode getParent() {
            return this.parent;
        }

        /**
         * public void setParent(IAVLNode node)
         * Sets the parent as the given node.
         * Time complexity of O(1).
         */
        public void setParent(IAVLNode node) {
            this.parent = node;
        }

        /**
         * public boolean isRealNode()
         * Returns true if the node is real, i.e. when the key is not -1.
         * Else returns false;
         * Time complexity of O(1).
         */
        public boolean isRealNode(){
            return key != -1;
        }

        /**
         * public int getHeight()
         * Returns the value of height.
         * Time complexity of O(1).
         */
        public int getHeight(){
            return this.height;
        }

        /**
         * public void setHeight(int height)
         * Updates the value of height.
         * Time complexity of O(1).
         */
        public void setHeight(int height){
            this.height = height;
        }

        /**
         * public int getSize()
         * Returns the size of the subtree under the node (including the node itself).
         * Time complexity of O(1).
         */
        public int getSize(){
            return this.size;
        }

        /**
         * public void calculateSize()
         * Calculates the size of the subtree under the current node, including itself.
         * To be used after balancing the tree.
         * Time complexity of O(1).
         */
        public void calculateSize(){
            this.size = this.left.getSize() + this.right.getSize() + 1;
        }
    }
}