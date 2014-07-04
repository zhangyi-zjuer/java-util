package com.zy.utils;

/**
 * 基础算法
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月27日
 */
public class Algorithm {
	/**
	 * 字符串最长公共子串
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String lcs(String str1, String str2) {
		StringBuilder sb = new StringBuilder();
		int m = str1.length();
		int n = str2.length();

		int[][] opt = new int[m + 1][n + 1];

		for (int i = m - 1; i >= 0; i--) {
			for (int j = n - 1; j >= 0; j--) {
				if (str1.charAt(i) == str2.charAt(j))
					opt[i][j] = opt[i + 1][j + 1] + 1;
				else
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
			}
		}
		int i = 0, j = 0;
		while (i < m && j < n) {
			if (str1.charAt(i) == str2.charAt(j)) {
				sb.append(str1.charAt(i));
				i++;
				j++;
			} else if (opt[i + 1][j] >= opt[i][j + 1])
				i++;
			else
				j++;
		}
		return sb.toString();
	}

	/**
	 * 字符串编辑距离
	 * 
	 * @param target
	 * @param source
	 * @return
	 */
	public static int editDistance(String target, String source) {
		char[] sa;
		int n;
		int p[];
		int d[];
		int _d[];

		sa = target.toCharArray();
		n = sa.length;
		p = new int[n + 1];
		d = new int[n + 1];

		final int m = source.length();
		if (n == 0 || m == 0) {
			if (n == m) {
				return 1;
			} else {
				return 0;
			}
		}

		int i;
		int j;

		char t_j;
		int cost;
		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			t_j = source.charAt(j - 1);
			d[0] = j;

			for (i = 1; i <= n; i++) {
				cost = sa[i - 1] == t_j ? 0 : 1;
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1]
						+ cost);
			}
			_d = p;
			p = d;
			d = _d;
		}
		return p[n];
	}

	/**
	 * 动态规划求解装箱问题
	 * 
	 * @param total
	 *            总体积
	 * @param a
	 *            代表每个箱子体积的数组
	 * @return 装完后的剩余体积，0表示可以完全填满
	 */
	public static int dpBox(int total, int[] a) {
		int[] f = new int[total + 1]; // 下标表示体积，值表示该体积下最多能装下多少
		int n = a.length;
		for (int i = 0; i < n; i++) {
			for (int j = total; j >= 1; j--) {
				if (j < a[i]) {
					f[j] = Math.max(f[j - 1], f[j]);
				} else {
					f[j] = Math.max(f[j], f[j - a[i]] + a[i]);
				}
			}
		}
		return total - f[total];
	}
}
