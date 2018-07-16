package utils;

import java.util.*;

/**
 * @author kingfans
 */
public class Constants {
    private static final Properties p = Utils.newProperties("config.properties");
    public static final String CLASS_NAME = "org.sqlite.JDBC";
    public static final String JDBC_DRIVER = p.getProperty("jdbc.driverClassName");
    public static final String JDBC_URL = Constants.p.getProperty("jdbc.url");
    public static final int MAX_POOL_SIZE = Integer.parseInt(p.getProperty("maxPoolSize"));
    public static final int MIN_POOL_SIZE = Integer.parseInt(p.getProperty("minPoolSize"));

}
