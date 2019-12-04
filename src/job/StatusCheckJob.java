package job;

import org.quartz.*;
import bean.manager.*;

import java.awt.*;

import swing.*;

/**
 * @author kingfans
 */
public class StatusCheckJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        RechargeFrame frame = FrameManager.getFrameByName("recharge");
        if (PortManager.getSerialPort() != null) {
            LoginFrame.STATUS_COUNT++;
            if (LoginFrame.STATUS_COUNT >= 9) {
                frame.deviceStatusText.setText("连接异常");
                frame.deviceStatusText.setForeground(Color.red);
                frame.hardwareVersionText.setText("-");
                frame.softwareVersionText.setText("-");
                LoginFrame.STATUS_COUNT = 0;
            }
        }
//        LoginFrame.SERVER_STATUS_COUNT++;
//        if (LoginFrame.SERVER_STATUS_COUNT >= 3) {
//            frame.serverStatusText.setText("离线");
//            frame.serverStatusText.setForeground(Color.red);
//            frame.logoutButton.setEnabled(false);
//            frame.registerButton.setEnabled(false);
//            frame.queryButton.setEnabled(false);
//            frame.rechargeButton.setEnabled(false);
//            frame.rechargeHisButton.setEnabled(false);
//            frame.unclaimedButton.setEnabled(false);
//            frame.prechargeHisButton.setEnabled(false);
//            frame.registerHisButton.setEnabled(false);
//            frame.logoutHisButton.setEnabled(false);
//            LoginFrame.SERVER_STATUS_COUNT = 0;
//        }
    }
}
