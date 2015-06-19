/**
 * 
 * tianfei
 * 2015年6月18日下午5:14:50
 */
package cn.com.gps169.common.gis;

/**
 * @author tianfei
 *
 */
public class TestGis {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty("configpath", "F:\\carsmart-svn\\deployment\\che08\\test");
        DeflexionController d = new DeflexionController();
        Coordinate c = d.getDeflexedCoordinate("116.481499", "39.990475", 1);
        System.out.println(c.getLat());
    }

}
