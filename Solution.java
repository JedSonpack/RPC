class Solution {
    public int findKthLargest(int[] nums, int k) {
        // 其实是找到下标为 nums.length - k
        return QS(nums, 0, nums.length - 1, nums.length-k);
    }

    private int QS(int[] nums, int l, int r, int k) {
        if (l == r)
            return nums[k];
        int i = l - 1;
        int j = r + 1;
        int x = nums[(l + r) >> 1];
        while (i < j) {
            do i++; while (nums[i] < x);
            do j--; while (nums[j] > x);
            if (i < j) {
                int t = nums[i];
                nums[i] = nums[j];
                nums[j] = t;
            }
        }
        // 第k大
        // k = 1
        if (k <= j) return QS(nums, l, j, k);
        else return QS(nums, j + 1, r, k);
    }
}