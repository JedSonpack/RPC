class Solution {
    public int longestValidParentheses(String s) {
        if (s.length() == 0 || s.length() == 1) return 0;
        // dp[i]: 以s[i]结尾
        int[] dp = new int[s.length()];
        int res = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    dp[i] = dp[Math.max(i - 2, 0)] + 2;
                } else {
                    // )
                    if (i - 1 - dp[i - 1] >= 0 && s.charAt(i - 1 - dp[i - 1]) == '(')
                        dp[i] = dp[i - 1 - dp[i - 1]] + dp[i - 1] + 2;
                }
            }
            res = Math.max(res, dp[i]);
        }
        return res;
    }
}