package utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.impl.ServiceImpl;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class HttpRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    private String url = "http://";
    private String method = "GET";
    private Map<String, String> paramMap = new HashMap<>();
    private HttpClient httpClient;

    /**
     * 构造函数
     */
    public HttpRequest() {
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        int socketTimeout = 5000;
        httpClient.getParams().setIntParameter("http.socket.timeout", socketTimeout);
        int connTimeout = 5000;
        httpClient.getParams().setIntParameter("http.connection.timeout", connTimeout);
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     */


    public void addParam(String key, String value) {
        paramMap.put(key, value);
    }

    public void addMap(Map<String, String> fields) {
        Iterator<Entry<String, String>> it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> map = it.next();
            paramMap.put(map.getKey(), map.getValue());
        }
    }

    /**
     * 发送请求
     */
    public String Send() {
        if ("POST".equals(this.method)) {
            return SendPost(this.url);
        } else if ("GET".equals(this.method)) {
            return SendGet(this.url);
        } else if ("PUT".equals(this.method)) {
            return SendPut(this.url);
        } else if ("DELETE".equals(this.method)) {
            return SendDelete(this.url);
        }
        return "";
    }

    /**
     * 发送GET请求
     *
     * @param urlstr
     * @return
     */
    public String SendGet(String urlstr) {

        GetMethod http = new GetMethod(urlstr);

        String response = null;

        try {
            httpClient.executeMethod(http);
            if (http.getStatusCode() != HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                response = http.getResponseBodyAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            http.releaseConnection();
        }

        return response;
    }

    /**
     * 发送POST请求
     *
     * @param urlstr
     * @return
     */
    public String SendPost(String urlstr) {
        PostMethod http = new PostMethod(urlstr);
        if (!paramMap.isEmpty()) {
            for (Entry<String, String> entry : paramMap.entrySet()) {
                http.addParameter(entry.getKey(), entry.getValue());
            }
        }
        String response = null;
        long start = System.currentTimeMillis();
        boolean isSuccess = true;
        try {
            httpClient.executeMethod(http);
            if (http.getStatusCode() != HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                response = http.getResponseBodyAsString();
            }
        } catch (IOException e) {
            isSuccess = false;
            //把发送失败的消息 存到sqlite中
            String action = paramMap.get("action");
            String[] actionArray = {"register", "logoff", "addRechargeHis", "addWithdrawHis", "updateBalance"};
            List<String> list = Arrays.asList(actionArray);
            if (action!=null){
                if (list.contains(action)) {
                    String content=Utils.MapToJsonStringDou(paramMap);
                    ServiceImpl.getInstance().addPushMsg(urlstr, content);
                }
            }
        } finally {
            http.releaseConnection();
        }

        String status = "FAILURE";
        String statusCode = "null";
        if (isSuccess) {
            status = "SUCCESS";
            statusCode = String.valueOf(http.getStatusCode());
        }
        String httpLogger = "HttpRequest " + status + ":\" 'method=Post"
                + "', 'statusCode=" + statusCode
                + "', 'url=" + urlstr
                + "', 'paramMap=" + paramMap
                + "', 'response=" + response
                + "', 'usedTime=" + (System.currentTimeMillis() - start) + "ms'\"";
        return response;
    }

    /**
     * 发送PUT请求
     *
     * @param urlstr
     * @return
     */
    public String SendPut(String urlstr) {
        PutMethod http = new PutMethod(urlstr);
        String response = null;
        try {
            httpClient.executeMethod(http);
            if (http.getStatusCode() != HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                response = http.getResponseBodyAsString();
            } else {
            }
        } catch (IOException e) {
        } finally {
            http.releaseConnection();
        }
        return response;
    }

    /**
     * 发送DELETE请求
     *
     * @param urlstr
     * @return
     */
    public String SendDelete(String urlstr) {

        DeleteMethod http = new DeleteMethod(urlstr);

        String response = null;

        try {
            httpClient.executeMethod(http);
            if (http.getStatusCode() != HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                response = http.getResponseBodyAsString();
            } else {
            }
        } catch (IOException e) {
        } finally {
            http.releaseConnection();
        }

        return response;
    }

    /**
     * 设置请求URL
     *
     * @param url
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     * 设置请求方式
     *
     * @param post
     */
    public void setMethod(String post) {
        this.method = post;
    }


}
