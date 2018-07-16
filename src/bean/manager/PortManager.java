package bean.manager;

import java.util.*;

import gnu.io.*;
import serial.*;

/**
 * @author kingfans
 */
public class PortManager {
    private static final HashMap<String, SerialPort> portMap = new HashMap<>(1);
    private static final String PORTNAME = "port";

    public static void addPort(SerialPort port) {
        synchronized (portMap) {
            if (!portMap.isEmpty()) {
                portMap.clear();
            }
            portMap.put(PORTNAME, port);
        }
    }

    public static void removePort() {
        synchronized (portMap) {
            portMap.get(PORTNAME).close();
            portMap.clear();
        }
    }

    public static SerialPort getSerialPort() {
        synchronized (portMap) {
            return portMap.get(PORTNAME);
        }
    }


}
