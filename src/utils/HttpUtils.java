package utils;

import java.util.Map;

/**
 * @Author: kingfans
 * @Date: 2018/8/31
 */
public class HttpUtils {

    public static String toServlet(Map<String, String> params, String servletName) {
        String url = "http://47.96.87.126:8654/recharge/" + servletName;
        HttpRequest hr = new HttpRequest();
        if (params != null) {
            hr.addMap(params);
        }
        hr.setMethod("POST");
        hr.setURL(url);
        return hr.Send();
    }
}
