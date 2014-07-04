package com.zy.utils;

import java.util.ArrayList;
import java.util.List;

import com.zy.utils.model.Point;
import com.zy.utils.model.Vector;

/**
 * 经纬度操作
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月24日
 */
public class GeoUtils {
	private static final double EARTH_RADIUS = 6372797;// 地球半径

	/**
	 * 获取平面两点的距离
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static float getDistance(Point p1, Point p2) {
		float x = p1.x - p2.x;
		float y = p1.y - p2.y;

		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 计算向量夹角
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double getAngle(Vector v1, Vector v2) {
		double n1 = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
		double n2 = Math.sqrt(v2.x * v2.x + v2.y * v2.y);

		double tmp = v1.x * v2.x + v1.y * v2.y;

		tmp /= n1 * n2;

		if (tmp > 1) {
			tmp = 1;
		}

		if (tmp < -1) {
			tmp = -1;
		}
		return Math.acos(tmp);
	}

	/**
	 * 获取两点间的向量
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Vector getVector(Point p1, Point p2) {
		return new Vector(p1.x - p2.x, p1.y - p2.y);
	}

	/**
	 * 获取点集中距离点point最远的点
	 * 
	 * @param point
	 * @param points
	 * @return
	 */
	public static Point getFarthestPoint(Point sPoint, List<Point> points) {
		double maxLen = 0;
		double tmpLen = 0;
		Point farthestPoint = null;

		for (Point dPoint : points) {

			float x = dPoint.x - sPoint.x;
			float y = dPoint.y - sPoint.y;
			tmpLen = Math.sqrt(x * x + y * y);

			if (maxLen < tmpLen) {
				maxLen = tmpLen;
				farthestPoint = dPoint;
			}
		}

		return farthestPoint;
	}

	/**
	 * 获取边界点 <br>
	 * 1. 任取一点，在点集中获取与改点距离最远的点，这个点一定是边界点 <br>
	 * 2. 以任取的点与边界点为向量，在点集中查找点，使该点与边界点形成的向量夹角最大，则该点也为边界点，以此类推直到选取的边界点已经存在
	 * 
	 * @param points
	 * 
	 * @return
	 */
	public static List<Point> getBorderPoints(List<Point> points) {

		if (points.size() < 4) {
			return points;
		}

		List<Point> borderPoints = new ArrayList<Point>();

		Point farthestPoint = getFarthestPoint(points.get(0), points);
		Point sPoint = farthestPoint;
		Point dPoint = points.get(0);
		Point maxAnglePoint = null;

		while (maxAnglePoint == null
				|| !maxAnglePoint.toString().equals(farthestPoint.toString())) {
			Vector sVector = getVector(dPoint, sPoint);
			double maxAngle = 0;

			for (Point point : points) {
				Vector dVector = getVector(point, sPoint);
				double angle = getAngle(sVector, dVector);

				if (maxAngle <= angle) {
					maxAngle = angle;
					maxAnglePoint = point;
				}
			}

			borderPoints.add(maxAnglePoint);
			dPoint = sPoint;
			sPoint = maxAnglePoint;
		}

		return borderPoints;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 获取 地理位置的距离
	 * 
	 * @param g1
	 * @param g2
	 * @return
	 */
	public static double getGeoDistance(Point g1, Point g2) {

		double lng1 = g1.x;
		double lat1 = g1.y;
		double lng2 = g2.x;
		double lat2 = g2.y;
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		return s;
	}

	/**
	 * 计算点到线段的距离
	 * 
	 * @param pA
	 *            线段端点
	 * @param pB
	 *            线段端点
	 * @param sP
	 *            点
	 * @return
	 */
	public static double getNearestDistance(Point pA, Point pB, Point sP) {

		double a, b, c;
		a = getGeoDistance(pB, sP);
		if (a <= 0.00001)
			return 0.0;
		b = getGeoDistance(pA, sP);
		if (b <= 0.00001)
			return 0.0;
		c = getGeoDistance(pA, pB);
		if (c <= 0.00001)
			return a;

		if (a * a >= b * b + c * c)
			return b;
		if (b * b >= a * a + c * c)
			return a;

		double l = (a + b + c) / 2;
		double s = Math.sqrt(l * (l - a) * (l - b) * (l - c));
		return 2 * s / c;
	}

	/**
	 * 获取点包围圈的距离
	 * 
	 * @param border
	 * @param p
	 * @return
	 */
	public static double getDistanceOfBorder(List<Point> border, Point p) {
		double minDistance = 100000000000.0;
		int n = border.size();

		for (int i = 0; i < n; i++) {
			Point pA = border.get(i);
			Point pB = border.get((i + 1) % n);
			double d = getNearestDistance(pA, pB, p);
			if (d < minDistance) {
				minDistance = d;
			}
		}

		return minDistance;
	}

	/**
	 * 判断点是否在多边形内
	 * 
	 * @param border
	 * @param p
	 * @return
	 */
	public static boolean containsPoint(List<Point> border, Point p) {
		int n = border.size();
		int nCross = 0;
		for (int i = 0; i < n; ++i) {
			Point p1 = border.get(i);
			Point p2 = border.get((i + 1) % n);

			// 求解 y=p.y 与 p1 p2 的交点
			if (p1.y == p2.y) { // p1p2 与 y=p0.y平行
				continue;
			}
			if (p.y < Math.min(p1.y, p2.y)) { // 交点在p1p2延长线上
				continue;
			}
			if (p.y >= Math.max(p1.y, p2.y)) { // 交点在p1p2延长线上
				continue;
			}
			// 求交点的 X 坐标
			float x = (p.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
			if (x > p.x) { // 只统计单边交点
				nCross++;
			}
		}
		// 单边交点为偶数，点在多边形之外
		return (nCross % 2 == 1);
	}

	/**
	 * 计算多边形面积
	 * 
	 * @param border
	 * @return
	 */
	public static double getArea(List<Point> border) {
		double area = 0.00;
		double centerX = 0.00;
		double centerY = 0.00;
		int n = border.size();

		for (int i = 0; i < n; i++) {
			centerX += border.get(i).x;
			centerY += border.get(i).y;
		}

		centerX /= n;
		centerY /= n;

		Point centerP = new Point((float) centerX, (float) centerY);

		for (int i = 0; i < border.size(); i++) {
			Point p1 = border.get(i);
			Point p2 = border.get((i + 1) % n);
			double a = getGeoDistance(p1, centerP);
			double b = getGeoDistance(p2, centerP);
			double c = getGeoDistance(p1, p2);
			double l = (a + b + c) / 2;
			double s = Math.sqrt(l * (l - a) * (l - b) * (l - c));

			area += s;

		}
		area = area / 2.00;

		return area;
	}
}
