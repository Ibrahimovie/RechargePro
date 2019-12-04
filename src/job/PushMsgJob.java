package job;

import com.alibaba.fastjson.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import service.Service;
import service.impl.ServiceImpl;
import swing.LoginFrame;
import utils.HttpUtils;
import utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kingfans
 */
public class PushMsgJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
//        String response = HttpUtils.toServlet(null, "community", "heartBeats");
//        JSONObject jsonObject = JSONObject.parseObject(response);
//        if ("0".equals(jsonObject.getString("code"))) {
//            List<Map<String, Object>> pushMsgList = ServiceImpl.getInstance().getPushMsgList();
//            for (int i = 0; i < pushMsgList.size(); i++) {
//                try {
//                    Map<String, Object> msgMap = pushMsgList.get(i);
//                    int msgId = (int) msgMap.get("id");
//                    String url = (String) msgMap.get("url");
//                    String content = (String) msgMap.get("content");
//                    Map<String, String> contentMap = Utils.StringToMapDou(content);
//                    String resp = HttpUtils.toServletWithUrl(contentMap, url);
//                    JSONObject json = JSONObject.parseObject(resp);
//                    String code = json.getString("code");
//                    if ("0".equals(code)) {
//                        ServiceImpl.getInstance().deletePushMsg(msgId);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
