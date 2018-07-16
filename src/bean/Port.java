package bean;

import gnu.io.*;

/**
 * @author kingfans
 */
public class Port {
    private String name;
    private SerialPort serialPort;
    private String status;

    public Port(String name, SerialPort serialPort, String status) {
        this.name = name;
        this.serialPort = serialPort;
        this.status = status;
    }

    public String getPortName() {
        return this.name;
    }

    public void setPortName(String name) {
        this.name = name;
    }

    public SerialPort getSerialPort() {
        return this.serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public String getPortStatus() {
        return this.status;
    }

    public void setPortStatus(String status) {
        this.status = status;
    }
}
