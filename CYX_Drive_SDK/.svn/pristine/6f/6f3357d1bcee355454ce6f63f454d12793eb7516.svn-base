package com.cwits.cyx_drive_sdk.util;

public class FractionTool {
	/** 急加速扣分/次 */
	private static double acceA = 2;
	/** 急加速扣分次数 */
	private static int acceN = 20;
	/** 急减速扣分/次 */
	private static double deceA = 4;
	/** 急减速扣分次数 */
	private static int deceN = 10;
	/** 急转弯扣分/次 */
	private static double turnA = 2;
	/** 急转弯扣分次数 */
	private static int turnN = 20;
	/** 超速扣分/次 */
	private static double speedA = 10;
	/** 超速扣分次数 */
	private static int speedN = 15;
	/** 驾驶行为总分 */
	private static int driveTotal = 90;

	/**
	 * 计算某天评级分数
	 * 
	 * @param args
	 *            [急加速,急减速,急转弯,超速,里程,里程参数]
	 * @return 分数
	 */
	public static int getGrade(double[] args) {
		double acce = args[0];// 急加速
		double dece = args[1];// 急减速
		double turn = args[2];// 急转弯
		double speed = args[3];// 超速
		double mile = args[4];// 里程
		double mileN = args[5];// 里程参数
		int drivePlus = driveTotal;// 驾驶累计减分
		if (mile < mileN && acce == 0 && dece == 0 && turn == 0 && speed == 0)
			return -1;
		// 1.驾驶行为扣分
		drivePlus -= getMinus(acceA, acceN, acce);
		drivePlus -= getMinus(deceA, deceN, dece);
		drivePlus -= getMinus(turnA, turnN, turn);
		drivePlus -= getMinus(speedA, speedN, speed);
		if (drivePlus < 0)
			drivePlus = 0;
		else if (drivePlus > driveTotal)
			drivePlus = 0;
		return drivePlus;
	}

	/**
	 * 计算每一项扣分
	 * 
	 * @param a
	 *            首次扣分
	 * @param t
	 *            限制总次数
	 * @param n
	 *            发生次数
	 * @return
	 */
	public static int getMinus(double a, int t, double n) {
		if (t == 0 || t == 1)
			return driveTotal;
		if (n < 0)
			n = 0;
		double d = 2.0 * (driveTotal - a * t) / (t * t - t);// 计算等差
		double s = a * n + n * (n - 1) * d / 2;
		if (s > driveTotal || s < 0)
			s = 90.0;
		return (int) s;
	}
}