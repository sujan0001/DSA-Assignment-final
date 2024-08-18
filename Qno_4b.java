public class Qno_4b {

    // Definition for a binary tree node.
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    // Helper class to store results from the subtree checks
    static class Result {
        boolean isBST;
        int sum;
        int min;
        int max;

        Result(boolean isBST, int sum, int min, int max) {
            this.isBST = isBST;
            this.sum = sum;
            this.min = min;
            this.max = max;
        }
    }

    private static int maxSum = 0;

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(4);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(2);
        root.right.right = new TreeNode(5);
        root.right.right.left = new TreeNode(4);
        root.right.right.right = new TreeNode(6);

        int result = findLargestMagicalGrove(root);
        System.out.println(result);
        TreeNode root1 = null;
        System.out.println("the largest collection of coins that forms a magical grove is: "+findLargestMagicalGrove(root1));

        TreeNode root2 = new TreeNode(10);
        System.out.println("the largest collection of coins that forms a magical grove is: "+findLargestMagicalGrove(root2));

        TreeNode root3 = new TreeNode(10);
        root3.left = new TreeNode(5);
        root3.right = new TreeNode(15);
        root3.left.right = new TreeNode(20);
        root3.right.left = new TreeNode(6);

        System.out.println("the largest collection of coins that forms a magical grove is: "+findLargestMagicalGrove(root3));
    }

    public static int findLargestMagicalGrove(TreeNode root) {
        maxSum = 0;
        postOrderTraversal(root);
        return maxSum;
    }
    private static Result postOrderTraversal(TreeNode node) {
        if (node == null) {
            return new Result(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        Result left = postOrderTraversal(node.left);
        Result right = postOrderTraversal(node.right);


        if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
            int currentSum = node.val + left.sum + right.sum;
            maxSum = Math.max(maxSum, currentSum);
            int currentMin = Math.min(node.val, left.min);
            int currentMax = Math.max(node.val, right.max);
            return new Result(true, currentSum, currentMin, currentMax);
        } else {
            return new Result(false, 0, 0, 0);
        }
   }
}