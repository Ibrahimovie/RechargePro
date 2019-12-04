package utils;

import java.util.Map;

/**
 * @Author: kingfans
 * @Date: 2018/8/31
 */
public class HttpUtils {

    public static String toServlet(Map<String, String> params, String controllerMapping,
                                   String methodMapping) {
        String url = "https://recharge.hzdk.com:7764/" + controllerMapping + "/" + methodMapping;
        HttpRequest hr = new HttpRequest();
        if (params != null) {
            hr.addMap(params);
        }
        hr.setMethod("POST");
        hr.setURL(url);
        return hr.Send();
    }


}
