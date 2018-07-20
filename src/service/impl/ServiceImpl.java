package service.impl;

import service.*;
import org.springframework.context.*;
import dao.*;

import java.util.*;

import utils.*;

/**
 * @author kingfans
 */
public class ServiceImpl implements Service {

    private static ServiceImpl service = new ServiceImpl();

    private ServiceImpl() {
    }

    private static ApplicationContext ctx = ApplicationContextHolder.getApplicationContext();
    private static UserDao userDao = (UserDao) ctx.getBean("userDao");
    private static CardDao cardDao = (CardDao) ctx.getBean("cardDao");
    private static RechargeDao rechargeDao = (RechargeDao) ctx.getBean("rechargeDao");

    public static ServiceImpl getInstance() {
        return service;
    }

    //USER DAO
    public String getAdminPassword() {
        return userDao.getAdminPassword();
    }

    public ArrayList<String> getSubUsername() {
        return userDao.getSubUsername();
    }

    public boolean isUserExist(String username) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("username", username);
        return userDao.isUserExist(param);
    }

    public Map<String, Object> getUserInfo(String username) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("username", username);
        return userDao.getUserInfo(param);
    }

    public void updateSectorOrder(int user_id, String username, int sector_id) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("user_id", user_id);
        param.put("username", username);
        param.put("sector_id", sector_id);
        userDao.updateSectorOrder(param);
    }

    public void updateSubSectorOrder(int sector_id) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("sector_id", sector_id);
        userDao.updateSubSectorOrder(param);
    }

    public void updatePortrateOrder(int user_id, String username, int portrate_id) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("user_id", user_id);
        param.put("username", username);
        param.put("portrate_id", portrate_id);
        userDao.updatePortrateOrder(param);
    }

    public void updateSubPortrateOrder(int portrate_id) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("portrate_id", portrate_id);
        userDao.updateSubPortrateOrder(param);
    }

    public void updatePassword(int user_id, String username, String password) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("user_id", user_id);
        param.put("username", username);
        param.put("password", password);
        userDao.updatePassword(param);
    }

    public void updateSubPassword(String username, String password) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("username", username);
        param.put("password", password);
        userDao.updateSubPassword(param);
    }

    public void updateSubUserPasswd(String formerUserName, String username, String password) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("formerUsername", formerUserName);
        param.put("username", username);
        param.put("password", password);
        userDao.updateSubUserPasswd(param);
    }

    public void updateUsername(int user_id, String username) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("user_id", user_id);
        param.put("username", username);
        userDao.updateUsername(param);
    }

    public void updateSystemPassword(int user_id, String username, String system_password) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("user_id", user_id);
        param.put("username", username);
        param.put("system_password", system_password);
        userDao.updateSystemPassword(param);
    }

    public void updateSubSystemPassword(String system_password) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("system_password", system_password);
        userDao.updateSubSystemPassword(param);
    }

    public void updatePortOrder(int user_id, String username, int port_id) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("user_id", user_id);
        param.put("username", username);
        param.put("port_id", port_id);
        userDao.updatePortOrder(param);
    }

    public void updateSubPortOrder(int port_id) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("port_id", port_id);
        userDao.updateSubPortOrder(param);
    }

    public void addSubAccount(String username, String password, String systemPassword, int isAdmin, int portrateId,
                              int portId, int deviceId, int sectorId) {
        Map<String, Object> param = new HashMap<>(8);
        param.put("username", username);
        param.put("password", password);
        param.put("system_password", systemPassword);
        param.put("is_admin", isAdmin);
        param.put("portrate_id", portrateId);
        param.put("port_id", portId);
        param.put("device_id", deviceId);
        param.put("sector_id", sectorId);
        userDao.addSubAccount(param);
    }

    public int getSubAccountsNum() {
        return userDao.getSubAccountsNum();
    }

    public List<Map<String, Object>> getSubAccountsInfo() {
        return userDao.getSubAccountsInfo();
    }

    public void deleteUser(String username) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("username", username);
        userDao.deleteUser(param);
    }

    //CARD DAO
    public boolean isCardExist(String cardNum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("card_number", cardNum);
        return cardDao.isCardExist(param);
    }

    public void updateCardInfo(String cardNum, int cardType, int balance, int validDay, String lastTime, String startTime, int rechargeTime, int payRate, int powerRate) {
        Map<String, Object> param = new HashMap<>(9);
        param.put("card_number", cardNum);
        param.put("card_type", cardType);
        param.put("balance", balance);
        param.put("valid_day", validDay);
        param.put("last_time", lastTime);
        param.put("start_time", startTime);
        param.put("recharge_time", rechargeTime);
        param.put("pay_rate", payRate);
        param.put("power_rate", powerRate);
        cardDao.updateCardInfo(param);
    }

    public void updateCardInfoQ10(String cardNum, int balance) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("card_number", cardNum);
        param.put("balance", balance);
        cardDao.updateCardInfoQ10(param);
    }

    public void updateCardInfoExTime(String cardNum, int cardType, int balance, int validDay, int rechargeTime, int payRate, int powerRate) {
        Map<String, Object> param = new HashMap<>(7);
        param.put("card_number", cardNum);
        param.put("card_type", cardType);
        param.put("balance", balance);
        param.put("valid_day", validDay);
        param.put("recharge_time", rechargeTime);
        param.put("pay_rate", payRate);
        param.put("power_rate", powerRate);
        ServiceImpl.cardDao.updateCardInfoExTime(param);
    }

    public void addCard(String cardNum, int cardType, int balance, int validDay, String lastTime, String startTime, int rechargeTime, int payRate, int powerRate) {
        Map<String, Object> param = new HashMap<>(9);
        param.put("card_number", cardNum);
        param.put("card_type", cardType);
        param.put("balance", balance);
        param.put("valid_day", validDay);
        param.put("last_time", lastTime);
        param.put("start_time", startTime);
        param.put("recharge_time", rechargeTime);
        param.put("pay_rate", payRate);
        param.put("power_rate", powerRate);
        cardDao.addCard(param);
    }

    public void addNewCard(String cardNum, String username, String phone) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("card_number", cardNum);
        param.put("username", username);
        param.put("phone", phone);
        cardDao.addNewCard(param);
    }

    public int getCardTypeByNum(String cardNum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("card_number", cardNum);
        return cardDao.getCardTypeByNum(param);
    }

    public String getCardPhoneByNum(String cardNum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("card_number", cardNum);
        return cardDao.getCardPhoneByNum(param);
    }

    public String getLastTimeByNum(String cardNum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("card_number", cardNum);
        return cardDao.getLastTimeByNum(param);
    }

    public String getStartTimeByNum(String cardNum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("card_number", cardNum);
        return cardDao.getStartTimeByNum(param);
    }

    public void deleteCard(String cardNum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("card_number", cardNum);
        cardDao.deleteCard(param);
    }

    public String getUsernameByCardNum(String cardNum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("card_number", cardNum);
        return cardDao.getUsernameByCardNum(param);
    }


    //RECHARGE DAO
    public void addRechargeHis(String cardNum, String username, String phone, int cardType, int balance, int topUp,
                               int validDay, int rechargeTime, int payRate, int powerRate, String time, String operator) {
        Map<String, Object> param = new HashMap<>(11);
        param.put("card_number", cardNum);
        param.put("username", username);
        param.put("phone", phone);
        param.put("card_type", cardType);
        param.put("balance", balance);
        param.put("top_up", topUp);
        param.put("valid_day", validDay);
        param.put("recharge_time", rechargeTime);
        param.put("pay_rate", payRate);
        param.put("power_rate", powerRate);
        param.put("now_time", time);
        param.put("operator", operator);
        rechargeDao.addRechargeHis(param);
    }

    public int getRechargeCount() {
        return rechargeDao.getRechargeCount();
    }

    public int getRechargeCountRangeWithPhone(String startTime, String endTime, String phone,String operator) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("start_time", startTime);
        param.put("end_time", endTime);
        param.put("phone", "%" + phone + "%");
        param.put("operator", operator);
        return rechargeDao.getRechargeCountRangeWithPhone(param);
    }

    public int getRechargeCountRangeWithoutPhone(String startTime, String endTime, String operator) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("start_time", startTime);
        param.put("end_time", endTime);
        param.put("operator", operator);
        return rechargeDao.getRechargeCountRangeWithoutPhone(param);
    }

    public List<Map<String, Object>> getRechargeHisAll() {
        return rechargeDao.getRechargeHisAll();
    }

    public List<Map<String, Object>> getRechargeHisRangeWithPhone(String startTime, String endTime, String phone,String operator) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("start_time", startTime);
        param.put("end_time", endTime);
        param.put("phone", "%" + phone + "%");
        param.put("operator", operator);
        return rechargeDao.getRechargeHisRangeWithPhone(param);
    }

    public List<Map<String, Object>> getRechargeHisRangeWithoutPhone(String startTime, String endTime,String operator) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("start_time", startTime);
        param.put("end_time", endTime);
        param.put("operator", operator);
        return rechargeDao.getRechargeHisRangeWithoutPhone(param);
    }
}
