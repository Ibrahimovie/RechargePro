package bean;

/**
 * @Author: kingfans
 * @Date: 2018/10/27
 */
public class LogoutCard {

    private String operator;
    private String cardNum;
    private String phone;
    private String username;
    private int cardType;
    private String logoutTime;
    private int balance;
    private int validDay;
    private int chargeTime;
    private int payRate;
    private int powerRate;
    private String community;

    public LogoutCard(String operator, String cardNum, String phone, String username,
                      int cardType, String logoutTime, int balance, int validDay,
                      int chargeTime, int payRate, int powerRate, String community) {
        this.operator = operator;
        this.cardNum = cardNum;
        this.phone = phone;
        this.username = username;
        this.cardType = cardType;
        this.logoutTime = logoutTime;
        this.balance = balance;
        this.validDay = validDay;
        this.chargeTime = chargeTime;
        this.payRate = payRate;
        this.powerRate = powerRate;
        this.community = community;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getValidDay() {
        return validDay;
    }

    public void setValidDay(int validDay) {
        this.validDay = validDay;
    }

    public int getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(int chargeTime) {
        this.chargeTime = chargeTime;
    }

    public int getPayRate() {
        return payRate;
    }

    public void setPayRate(int payRate) {
        this.payRate = payRate;
    }

    public int getPowerRate() {
        return powerRate;
    }

    public void setPowerRate(int powerRate) {
        this.powerRate = powerRate;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    @Override
    public String toString() {
        return "LogoutCard{" +
                "operator='" + operator + '\'' +
                ", cardNum='" + cardNum + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", cardType=" + cardType +
                ", logoutTime='" + logoutTime + '\'' +
                ", balance=" + balance +
                ", validDay=" + validDay +
                ", chargeTime=" + chargeTime +
                ", payRate=" + payRate +
                ", powerRate=" + powerRate +
                ", community='" + community + '\'' +
                '}';
    }
}
