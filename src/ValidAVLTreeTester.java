import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ValidAVLTreeTester {

    public static void testRandomTrees(int treeSize, int amountOfTests){
        List<Integer> lst = new ArrayList<>();
        for (int i = 0; i < treeSize; i++) {
            lst.add(i);
        }
        Random r = new Random();
        for (int i = 0; i < amountOfTests; i++) {
            Collections.shuffle(lst);
            AVLTree tree = new AVLTree();
            for (int num : lst) {
                tree.insert(num, Integer.toString(num));
            }
            ValidAVLTreeTester.isTreeAVL(tree);
            int rInt = r.nextInt(lst.size());
            AVLTree[] arr = tree.split(rInt);
            ValidAVLTreeTester.isTreeAVL(arr[0]);
            ValidAVLTreeTester.isTreeAVL(arr[1]);
            AVLTree tmpTree = new AVLTree();
            tmpTree.insert(rInt, Integer.toString(rInt));
            arr[1].join(tmpTree.getRoot(), arr[0]);
            ValidAVLTreeTester.isTreeAVL(arr[1]);
        }
        System.out.println("DONE!");
    }
    public static void isTreeAVL(AVLTree tree){
        if (tree.getRoot() != null) {
            AVLTree.IAVLNode min = tree.getRoot();
            while (min.getLeft().isRealNode()) {
                min = min.getLeft();
            }
            if (!min.getValue().equals(tree.min())) {
                System.out.println("ERROR: min() does not return the value of the actual minimum node");
            }
            AVLTree.IAVLNode max = tree.getRoot();
            while (max.getRight().isRealNode()) {
                max = max.getRight();
            }
            if (!max.getValue().equals(tree.max())) {
                System.out.println("ERROR: max() does not return the value of the actual maximum node");
            }
            isNodeValid(tree.getRoot());
        }
    }
    private static void isNodeValid(AVLTree.IAVLNode node){
        if (node.getLeft().isRealNode()){
            if (node.getLeft().getKey() > node.getKey()){
                System.out.println("ERROR: The Tree isn't sorted. The left son of Node " + node.getKey() + " has the key " + node.getLeft().getKey());
            }
            isNodeValid(node.getLeft());
        }
        if (node.getRight().isRealNode()){
            if (node.getRight().getKey() < node.getKey()){
                System.out.println("ERROR: The Tree isn't sorted. The right son of Node " + node.getKey() + " has the key " + node.getRight().getKey());
            }
            isNodeValid(node.getRight());
        }
        if (node.getRight().getParent() != node){
            System.out.println("ERROR: The right son of node " + node.getKey() + " points to " + node.getRight().getParent().getKey() + " as its parent instead");
        }
        if (node.getLeft().getParent() != node){
            System.out.println("ERROR: The left son of node " + node.getKey() + " points to " + node.getLeft().getParent().getKey() + " as its parent instead");
        }
        int rankLeft = node.getHeight() - node.getLeft().getHeight();
        int rankRight = node.getHeight() - node.getRight().getHeight();
        if (!((rankLeft == 1 && rankRight == 1) || (rankLeft == 2 && rankRight == 1) || (rankLeft == 1 && rankRight == 2))){
            System.out.println("ERROR: The tree isn't balanced at node " + node.getKey() + " with left rank of " + rankLeft + " and right rank of " + rankRight);
        }
        if (node.getSize() != node.getLeft().getSize() + node.getRight().getSize() + 1){
            System.out.println("ERROR: The size of " + node.getKey() + " is set to " + node.getSize() + " while his left son's size is " + node.getLeft().getSize() + " and his right son's size is " + node.getRight().getSize());
            System.out.println(node.getValue());
        }
    }
}
