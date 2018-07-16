package bean;

/**
 * @author kingfans
 */
public class Card {
    private String cardNum;
    private int cardType;
    private String phone;
    private String username;
    private int balance;
    private int topUp;
    private int validDay;
    private String lastTime;
    private String startTime;
    private int rechargeTime;
    private int payRate;
    private int powerRate;

    public Card(String cardNum) {
        this.cardNum = cardNum;
    }

    public int getCardType() {
        return this.cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getValidDay() {
        return this.validDay;
    }

    public void setValidDay(int validDay) {
        this.validDay = validDay;
    }

    public String getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getRechargeTime() {
        return this.rechargeTime;
    }

    public void setRechargeTime(int rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public int getPayRate() {
        return this.payRate;
    }

    public void setPayRate(int payRate) {
        this.payRate = payRate;
    }

    public int getPowerRate() {
        return this.powerRate;
    }

    public void setPowerRate(int powerRate) {
        this.powerRate = powerRate;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTopUp() {
        return this.topUp;
    }

    public void setTopUp(int topUp) {
        this.topUp = topUp;
    }

    public void updateCardInfo(int cardType, int balance, int validDay, String lastTime, String startTime, int rechargeTime, int payRate, int powerRate) {
        this.cardType = cardType;
        this.balance = balance;
        this.validDay = validDay;
        this.lastTime = lastTime;
        this.startTime = startTime;
        this.rechargeTime = rechargeTime;
        this.payRate = payRate;
        this.powerRate = powerRate;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNum='" + cardNum + '\'' +
                ", cardType=" + cardType +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                ", topUp=" + topUp +
                ", validDay=" + validDay +
                ", lastTime='" + lastTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", rechargeTime=" + rechargeTime +
                ", payRate=" + payRate +
                ", powerRate=" + powerRate +
                '}';
    }
}
