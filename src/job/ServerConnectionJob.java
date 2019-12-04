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
//        RechargeFrame frame = FrameManager.getFrameByName("recharge");
//        String response = HttpUtils.toServlet(null, "community", "heartBeats");
//        JSONObject jsonObject = JSONObject.parseObject(response);
//        if ("0".equals(jsonObject.getString("code"))) {
//            frame.serverStatusText.setText("在线");
//            frame.serverStatusText.setForeground(Color.blue);
//            frame.logoutButton.setEnabled(true);
//            frame.registerButton.setEnabled(true);
//            frame.queryButton.setEnabled(true);
//            frame.rechargeButton.setEnabled(true);
//            frame.rechargeHisButton.setEnabled(true);
//            frame.unclaimedButton.setEnabled(true);
//            frame.prechargeHisButton.setEnabled(true);
//            frame.registerHisButton.setEnabled(true);
//            frame.logoutHisButton.setEnabled(true);
//            LoginFrame.SERVER_STATUS_COUNT = 0;
//        }
    }
}
