package job;

import org.quartz.*;
import bean.manager.*;
import commands.*;
import serial.*;
import exception.*;
import gnu.io.*;
import bean.*;

/**
 * @author kingfans
 */
public class PortConnectionJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        SerialPort port = PortManager.getSerialPort();
        User user = UserManager.getUser();
        int sectorOrder = user.getSectorOrder();
        if (port != null) {
            try {
                SerialPortUtils.sendToPort(port, CommandUtils.isConnectCommand(sectorOrder));
            } catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure sendDataToSerialPortFailure) {
                sendDataToSerialPortFailure.printStackTrace();
            }
        }
    }
}
