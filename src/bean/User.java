package bean;

/**
 * @author kingfans
 */
public class User {
    private int userId;
    private String userName;
    private String password;
    private String systemPassword;
    private int sectorOrder;
    private int portrateOrder;
    private int portOrder;
    private int deviceOrder;
    private int hasSerialPort;
    private int isDeviceConnected;

    public User(int userId, String userName, String password, String systemPassword, int sectorOrder, int portrateOrder, int portOrder, int deviceOrder, int hasSerialPort, int isDeviceConnected) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.systemPassword = systemPassword;
        this.sectorOrder = sectorOrder;
        this.portrateOrder = portrateOrder;
        this.portOrder = portOrder;
        this.deviceOrder = deviceOrder;
        this.hasSerialPort = hasSerialPort;
        this.isDeviceConnected = isDeviceConnected;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSystemPassword() {
        return this.systemPassword;
    }

    public void setSystemPassword(String systemPassword) {
        this.systemPassword = systemPassword;
    }

    public int getSectorOrder() {
        return this.sectorOrder;
    }

    public void setSectorOrder(int sectorOrder) {
        this.sectorOrder = sectorOrder;
    }

    public int getPortrateOrder() {
        return this.portrateOrder;
    }

    public void setPortrateOrder(int portrateOrder) {
        this.portrateOrder = portrateOrder;
    }

    public int getPortOrder() {
        return this.portOrder;
    }

    public void setPortOrder(int portOrder) {
        this.portOrder = portOrder;
    }

    public int getHasSerialPort() {
        return this.hasSerialPort;
    }

    public void setHasSerialPort(int hasSerialPort) {
        this.hasSerialPort = hasSerialPort;
    }

    public int getDeviceOrder() {
        return this.deviceOrder;
    }

    public void setDeviceOrder(int deviceOrder) {
        this.deviceOrder = deviceOrder;
    }

    public int getIsDeviceConnected() {
        return this.isDeviceConnected;
    }

    public void setIsDeviceConnected(int isDeviceConnected) {
        this.isDeviceConnected = isDeviceConnected;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", systemPassword='" + systemPassword + '\'' +
                ", sectorOrder=" + sectorOrder +
                ", portrateOrder=" + portrateOrder +
                ", portOrder=" + portOrder +
                ", deviceOrder=" + deviceOrder +
                ", hasSerialPort=" + hasSerialPort +
                ", isDeviceConnected=" + isDeviceConnected +
                '}';
    }
}
