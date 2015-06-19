package cn.com.gps169.common.gis;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.gps169.common.tool.ConfigUtil;

public class Configuration {

    private static transient final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static Properties defaultProperty;

    static {
        init();
    }

    static void init() {
        defaultProperty = new Properties();
        try {
            String propertyName = "gis.properties";
            defaultProperty = ConfigUtil.getConfigReader().getResourceAsProperties(propertyName);
        } catch (IOException ex) {
            ex.printStackTrace();
            LOGGER.error(String.format("读取配置文件失败。 ErrorMsg:[%s]", ex.getMessage()));
        }
    }

    public static String getAutoNaviServiceUri() {
        return defaultProperty.getProperty("autonavi.service.uri");
    }

    public static String getAutoNaviKey() {
        return defaultProperty.getProperty("autonavi.key");
    }
    
    public static String getRegeoKey() {
        return defaultProperty.getProperty("regeo.key");
    }
    
    public static String getRegeoUri() {
        return defaultProperty.getProperty("regeo.uri");
    }
    
    public static String getRegeoRadiu() {
        return defaultProperty.getProperty("regeo.radiu");
    }

    public static String getGeocodingRange() {
        return defaultProperty.getProperty("autonavi.geocoding.range");
    }

    public static String getGeocodingPoiPattern() {
        return defaultProperty.getProperty("autonavi.geocoding.poiPattern");
    }

    public static String getMapbarDeflexionUri() {
        return defaultProperty.getProperty("mapbar.deflexion.uri");
    }

    public static String getMapbarGeocodingUri() {
        return defaultProperty.getProperty("mapbar.geocoding.uri");
    }
}