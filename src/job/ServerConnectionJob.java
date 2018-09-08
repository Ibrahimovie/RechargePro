package job;

import bean.manager.FrameManager;
import com.alibaba.fastjson.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import swing.LoginFrame;
import swing.RechargeFrame;
import utils.HttpUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kingfans
 */
public class ServerConnectionJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        RechargeFrame frame = FrameManager.getFrameByName("recharge");
        Map<String, String> map = new HashMap<>(1);
        map.put("action", "heartBeats");
        String response = HttpUtils.toServlet(map, "CommunityServlet");
        JSONObject jsonObject = JSONObject.parseObject(response);
        if ("0".equals(jsonObject.getString("code"))) {
            frame.serverStatusText.setText("在线");
            frame.serverStatusText.setForeground(Color.blue);
            LoginFrame.SERVER_STATUS_COUNT = 0;
        }
    }
}
