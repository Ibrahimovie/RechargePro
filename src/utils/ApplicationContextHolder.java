package utils;

import org.springframework.context.*;
import org.springframework.context.support.*;

/**
 * @author kingfans
 */
public class ApplicationContextHolder {
    public static final ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return ApplicationContextHolder.applicationContext;
    }

    static {
        applicationContext = new ClassPathXmlApplicationContext(new String[]{"spring-config.xml", "spring-dao.xml"});
    }
}
