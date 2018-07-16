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
        if (PortManager.getSerialPort() != null) {
            LoginFrame.STATUS_COUNT++;
            if (LoginFrame.STATUS_COUNT >= 9) {
                RechargeFrame frame = FrameManager.getFrameByName("recharge");
                System.out.println("status count reached 9 !!!");
                frame.deviceStatusText.setText("连接异常");
                frame.deviceStatusText.setForeground(Color.red);
                LoginFrame.STATUS_COUNT = 0;
            }
        }
    }
}
