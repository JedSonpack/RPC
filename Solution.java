class Solution {
    public int minDistance(String word1, String word2) {
        int n = word1.length(), m = word2.length();
        int[][] dp = new int[n + 1][m + 1];  // 正确维度
        // 初始化空字符串转换场景
        for (int i = 0; i <= n; i++) dp[i][0] = i;  // 删除i次
        for (int j = 0; j <= m; j++) dp[0][j] = j;  // 插入j次
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {  // 正确索引
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j],    // 删除
                            Math.min(
                                    dp[i][j - 1],    // 插入
                                    dp[i - 1][j - 1]  // 替换
                            )
                    );
                }
            }
        }
        return dp[n][m];
    }
}