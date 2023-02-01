package com.zkthinke.modules.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther SONGXF
 * @date 2021/3/29 11:28
 */
@Slf4j
public class LatitudeLongitideUtils {

    public static Double calculateDistance(String p1Lon,String p1Lat,String p2Lon,String p2Lat){
        GlobalCoordinates source = new GlobalCoordinates(Double.parseDouble(p1Lat), Double.parseDouble(p1Lon));
        GlobalCoordinates target = new GlobalCoordinates(Double.parseDouble(p2Lat), Double.parseDouble(p2Lon));

        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere,source, target);
        return geoCurve.getEllipsoidalDistance();
    }

    /**
     * L4 = IF(C4<>"",(7915.70447*LOG(TAN(RADIANS(45+ABS(J4)/2))*EXP(0.081991787/2*LN((1-0.081991787*SIN(ABS(J4)*PI()/180))/(1+0.081991787*SIN(ABS(J4)*PI()/180))))))*(IF(J4>=0,1,-1)),"")
     */
    public static Double calculateMP(Double latitude) {
        double temp = 0.081991787 * Math.sin(Math.abs(Math.toRadians(latitude)));
        return 7915.70447 * Math.log10(Math.tan(Math.toRadians(45 + Math.abs(latitude) / 2)) * Math.exp(0.081991787 / 2 * Math.log((1 - temp) / (1 + temp)))) * (latitude >= 0 ? 1 : -1);
    }

    /**
     * O4 = IF(D5="","",IF(AND(ABS(K5-K4)>180,(K5-K4)<0),(360+(K5-K4)),IF(AND(ABS(K5-K4)>180,(K5-K4)>0),(360-(K5-K4)),(K5-K4))))
     */
    public static Double calculateDLong(Double nextLongitude, Double longitude) {

        if (Math.abs(nextLongitude - longitude) > 180 && nextLongitude - longitude < 0) {
            return 360 + (nextLongitude - longitude);
        } else if (Math.abs(nextLongitude - longitude) > 180 && nextLongitude - longitude > 0) {
            return 360 - (nextLongitude - longitude);
        } else {
            return nextLongitude - longitude;
        }
    }

    /**
     * 计算方位角
     * 单位：度 °
     * R4 = IF(AND(O4>=0,M4>0),DEGREES(ATAN(ABS((O4*60)/M4))),"")
     * S4 = IF(AND(O4>=0,M4<0),(180-DEGREES(ATAN(ABS((O4*60)/M4)))),"")
     * T4 = IF(AND(O4<0,M4<0),(180+DEGREES(ATAN(ABS((O4*60)/M4)))),"")
     * U4 = IF(AND(O4<0,M4>0),(360-DEGREES(ATAN(ABS((O4*60)/M4)))),"")
     * V4 = IF(AND(O4>0,M4=0),90,"")
     * W4 = IF(AND(O4<0,M4=0),270,"")
     *
     * P4 = IF(C5<>"",SUM(R4:W4),"")
     */
    public static Double calculateTrueCourse(Double dLong, Double dmp) {
        double degrees = Math.toDegrees(Math.atan(Math.abs((dLong * 60) / dmp)));
        if (Double.isNaN(degrees)||Double.isInfinite(degrees)){
            return 0D;
        }
        if (dLong >= 0 && dmp > 0) {//R4
            return degrees;
        } else if (dLong >= 0 && dmp < 0) {//S4
            return 180 - degrees;
        } else if (dLong < 0 && dmp < 0) {//T4
            return 180 + degrees;
        } else if (dLong < 0 && dmp > 0) {//U4
            return 360 - degrees;
        } else if (dLong > 0 && Math.abs(dmp) < 0.000000001) {//V4 == 0
            return 90D;
        } else if (dLong < 0 && Math.abs(dmp) < 0.000000001) {//W4 == 0
            return 270D;
        } else {
            return null;
        }
    }


    /**
     * 计算航距
     * 单位：海里
     * Q4 = IF(C5<>"",(IF(OR(P4=90,P4=270),ABS(O4*60*COS(ABS(RADIANS(J4)))),(N4*60)/COS(RADIANS(P4)))),"")
     */
    public static Double calculateRange(Double trueCourse, Double dLong, Double latitude, Double dLat) {
        if (trueCourse == 90 || trueCourse == 270) {
            return Math.abs(dLong * 60 * Math.cos(Math.abs(Math.toRadians(latitude))));
        } else {
            return (dLat * 60) / Math.cos(Math.toRadians(trueCourse));
        }
    }

    /**
     * 特殊格式的角度转换
     * angle 的 万位，千位，百位是度  十位、个位是分
     * 转换为 度
     * 例：3230 转换为 32.5
     */
    public static String angleConvert(String angle) {
        BigDecimal a = new BigDecimal(angle);
        BigDecimal bd100 = new BigDecimal(100);
        BigDecimal bd60 = new BigDecimal(60);
        //度
        BigDecimal du = a.divide(bd100, 0, RoundingMode.FLOOR);
        //分
        BigDecimal fen = a.subtract(du.multiply(bd100));
        return du.add(fen.divide(bd60, 12, RoundingMode.HALF_UP)).toString();
    }

    /**
     * 特殊格式的角度转换
     * angle  度
     * 转换为 万位，千位，百位是度  十位、个位是分
     * 例：32.5 转换为 3230
     */
    public static String angleConvertReverse(String angle) {
        BigDecimal a = new BigDecimal(angle);
        BigDecimal bd100 = new BigDecimal(100);
        BigDecimal bd60 = new BigDecimal(60);
        //度
        BigDecimal du = a.setScale(0, RoundingMode.FLOOR);
        //分
        BigDecimal fen = a.subtract(du).multiply(bd60);
        return du.multiply(bd100).add(fen).toString();
    }


    public static String parse(String latitudeOrLongitide){

        String[] split = latitudeOrLongitide.split("\\.");
        String duFen = split[0];

        String du = duFen.substring(0,duFen.length() - 2);
        String fen = latitudeOrLongitide.substring(du.length());

        int x = Integer.valueOf(du);
        double y = Double.valueOf(fen) / 60;

        return String.valueOf(x+y);
    }

    public static Map<String,String> format(Double longitide,Double latitude){

        Map<String,String> map = new HashMap<>();
        String longitideFlag = longitide>0?"E":"W";
        String latitudeFlag = latitude>0?"N":"S";

        String longitide_change = change(longitide);
        String latitude_change = change(latitude);
        map.put("longitide",longitide_change+longitideFlag);
        map.put("latitude",latitude_change+latitudeFlag);
        return map;
    }

    public static String change(Double longitideOrLatitude){
        double dTemp = Math.floor(longitideOrLatitude);
        double longitideResult = longitideOrLatitude-dTemp;
        double fenmiao = longitideResult * 60;

        double fenTemp = Math.floor(fenmiao);
        double fenResult = fenmiao-fenTemp;

        BigDecimal bg = new BigDecimal(String.valueOf(fenResult));
        double longback = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        String longitide_value = dTemp + "°" + longback+"'";

        return longitide_value;
    }

    /**
     * 根据经纬度,真航向计算电子围栏的点
     * @auther: SONGXF
     * @date: 2021/3/30 16:35
     */
    public static Map<String,String> mathLeftRight(Double longitide, Double latitude , Double true_course,double radius){
        Map<String,String> map = new HashMap<>();

        /*double leftLongitide = longitide - Constant.RADIUS *Math.cos(true_course * (Math.PI / 180)) / (Constant.ARC * Math.cos(latitude * (Math.PI / 180)) * 2 * Math.PI / 360);
        double leftLatitude = latitude + Constant.RADIUS*Math.sin(true_course * (Math.PI / 180)) / (Constant.ARC * 2 * Math.PI / 360);

        double rightLongitide = longitide + Constant.RADIUS*Math.cos(true_course * (Math.PI / 180)) / (Constant.ARC * Math.cos(latitude * (Math.PI / 180)) * 2 * Math.PI / 360);
        double rightLatitude = latitude - Constant.RADIUS*Math.sin(true_course * (Math.PI / 180)) / (Constant.ARC * 2 * Math.PI / 360);
*/
        GlobalCoordinates source = new GlobalCoordinates(latitude, longitide);
        GlobalCoordinates globalCoordinates_left = getGlobalCoordinates(source, true_course-90, radius);
        GlobalCoordinates globalCoordinates_right = getGlobalCoordinates(source, true_course+90, radius);

        map.put("leftLongitude",String.valueOf(globalCoordinates_left.getLongitude()));
        map.put("leftLatitude",String.valueOf(globalCoordinates_left.getLatitude()));
        map.put("rightLongitude",String.valueOf(globalCoordinates_right.getLongitude()));
        map.put("rightLatitude",String.valueOf(globalCoordinates_right.getLatitude()));

        return map;
    }

    public static GlobalCoordinates getGlobalCoordinates(GlobalCoordinates startGlobalCoordinates, double startAngle, double distance){
        return new GeodeticCalculator().calculateEndingGlobalCoordinates(Ellipsoid.Sphere, startGlobalCoordinates, startAngle, distance);

    }

    public static boolean isInCircle(double radius,double lat1, double lng1, double lat2, double lng2) {
        GlobalCoordinates source = new GlobalCoordinates(lat1, lng1);
        GlobalCoordinates target = new GlobalCoordinates(lat2, lng2);

        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        //Ellipsoid.WGS84
        //Ellipsoid.Sphere
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere,source, target);

        double ellipsoidalDistance = geoCurve.getEllipsoidalDistance();
        return ellipsoidalDistance<=radius;
    }

    /**
     * 判断点是否在多边形内
     *
     * @param point 测试点
     * @param pts   多边形的点
     * @return boolean
     * @throws
     * @Title: IsPointInPoly
     */
    public static boolean isInPolygon(Point2D.Double point, List<Point2D.Double> pts) {

        int N = pts.size();
        boolean boundOrVertex = true;
        int intersectCount = 0;//交叉点数量
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point2D.Double p1, p2;//临近顶点
        Point2D.Double p = point; //当前点

        p1 = pts.get(0);
        for (int i = 1; i <= N; ++i) {
            if (p.equals(p1)) {
                return boundOrVertex;
            }

            p2 = pts.get(i % N);
            if (p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)) {
                p1 = p2;
                continue;
            }

            //射线穿过算法
            if (p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)) {
                if (p.y <= Math.max(p1.y, p2.y)) {
                    if (p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)) {
                        return boundOrVertex;
                    }

                    if (p1.y == p2.y) {
                        if (p1.y == p.y) {
                            return boundOrVertex;
                        } else {
                            ++intersectCount;
                        }
                    } else {
                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;
                        if (Math.abs(p.y - xinters) < precision) {
                            return boundOrVertex;
                        }

                        if (p.y < xinters) {
                            ++intersectCount;
                        }
                    }
                }
            } else {
                if (p.x == p2.x && p.y <= p2.y) {
                    Point2D.Double p3 = pts.get((i + 1) % N);
                    if (p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)) {
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;
        }
        if (intersectCount % 2 == 0) {//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }
    }
}
