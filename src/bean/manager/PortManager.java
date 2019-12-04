package bean.manager;

import java.util.*;
import gnu.io.*;

/**
 * @author kingfans
 */
public class PortManager {
    private static final HashMap<String, SerialPort> PORT_MAP = new HashMap<>(1);
    private static final String PORTNAME = "port";

    public static void addPort(SerialPort port) {
        synchronized (PORT_MAP) {
            if (!PORT_MAP.isEmpty()) {
                PORT_MAP.clear();
            }
            PORT_MAP.put(PORTNAME, port);
        }
    }

    public static void removePort() {
        synchronized (PORT_MAP) {
            if (PORT_MAP.get(PORTNAME)!=null){
                PORT_MAP.get(PORTNAME).close();
            }
            PORT_MAP.clear();
        }
    }

    public static SerialPort getSerialPort() {
        synchronized (PORT_MAP) {
            return PORT_MAP.get(PORTNAME);
        }
    }


}
