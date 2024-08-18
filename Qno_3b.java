import java.util.Arrays;

public class Qno_3b {

    public static int[] rearrangePassengers(int[] arr, int k) {
        int n = arr.length;

        for (int i = 0; i < n; i += k) {
            // Determine the end index of the current chunk
            int end = Math.min(i + k - 1, n - 1);
            // Reverse the elements in the current chunk
            reverse(arr, i, end);
        }

        return arr;
    }

    // Helper function to reverse elements in the array from index 'start' to 'end'
    private static void reverse(int[] arr, int start, int end) {
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }

    public static void main(String[] args) {
        // Example 1
        int[] arr1 = {1, 2, 3, 4, 5};
        int k1 = 2;
        System.out.println("Input: " + Arrays.toString(arr1) + ", k = " + k1);
        int[] result1 = rearrangePassengers(arr1, k1);
        System.out.println("Output: " + Arrays.toString(result1));

        // Example 2
        int[] arr2 = {1, 2, 3, 4, 5};
        int k2 = 3;
        System.out.println("Input: " + Arrays.toString(arr2) + ", k = " + k2);
        int[] result2 = rearrangePassengers(arr2, k2);
        System.out.println("Output: " + Arrays.toString(result2));
    }
}
