import java.util.Deque;
import java.util.LinkedList;

public class Qno_5b {
    public static int longestStretch(int[] nums, int k) {
        int n = nums.length;
        if (n == 0) return 0;
        Deque<Integer> minDeque = new LinkedList<>();
        Deque<Integer> maxDeque = new LinkedList<>();

        int left = 0;
        int longest = 0;
        for (int right = 0; right < n; right++) {
            while (!minDeque.isEmpty() && nums[minDeque.peekLast()] >= nums[right]) {
                minDeque.pollLast();
            }
            minDeque.addLast(right);
            while (!maxDeque.isEmpty() && nums[maxDeque.peekLast()] <= nums[right]) {
                maxDeque.pollLast();
            }
            maxDeque.addLast(right);
            while (nums[maxDeque.peekFirst()] - nums[minDeque.peekFirst()] > k) {
                if (minDeque.peekFirst() == left) minDeque.pollFirst();
                if (maxDeque.peekFirst() == left) maxDeque.pollFirst();
                left++;
            }

            longest = Math.max(longest, right - left + 1);
        }

        return longest;
    }

    public static void main(String[] args) {
        // Example 1
        int[] nums1 = {1, 3, 6, 7, 9, 2, 5, 8};
        int k1 = 3;
        System.out.println("Example 1: Longest stretch = " + longestStretch(nums1, k1)); // Output: 4

        // Example 2
        int[] nums2 = {10, 13, 15, 18, 12, 8, 7, 14, 20};
        int k2 = 5;
        System.out.println("Example 2: Longest stretch = " + longestStretch(nums2, k2)); // Output: 3
    }
}

