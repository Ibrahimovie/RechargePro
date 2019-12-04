package job;

import bean.manager.FrameManager;
import bean.manager.PortManager;
import gnu.io.SerialPort;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import serial.SerialPortUtils;
import swing.RechargeFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static swing.LoginFrame.PORT_NUM;

/**
 * @author kingfans
 */
public class AvailablePortJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            RechargeFrame frame = FrameManager.getFrameByName("recharge");
            ArrayList<String> ports = SerialPortUtils.findPort();
            String[] portList = ports.toArray(new String[0]);
            if (portList.length != PORT_NUM) {
                frame.portChooser.setFont(new Font("Dialog", Font.PLAIN, 12));
                frame.portChooser.setModel(new DefaultComboBoxModel<>(portList));
                SerialPort port = PortManager.getSerialPort();
                int index = 0;
                if (port!=null){
                    String portname = port.getName();
                    for (int i = 0; i < portList.length; i++) {
                        if (portname.contains(portList[i])) {
                            index = i;
                        }
                    }
                }
                frame.portChooser.setSelectedIndex(index);
                PORT_NUM = portList.length;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
