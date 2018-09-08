package dao;

import java.util.*;

/**
 * @author kingfans
 */
public interface UserDao {
    String getAdminPassword();

    ArrayList<String> getSubUsername();

    boolean isUserExist(Map<String, Object> params);

    Map<String, Object> getUserInfo(Map<String, Object> params);

    void updateSectorOrder(Map<String, Object> params);

    void updateSubSectorOrder(Map<String, Object> params);

    void updatePortrateOrder(Map<String, Object> params);

    void updateSubPortrateOrder(Map<String, Object> params);

    void updatePortOrder(Map<String, Object> params);

    void updateSubPortOrder(Map<String, Object> params);

    void updatePassword(Map<String, Object> params);

    void updateSubPassword(Map<String, Object> params);

    void updateSubUserPasswd(Map<String, Object> params);

    void updateUsername(Map<String, Object> params);

    void updateSystemPassword(Map<String, Object> params);

    void updateSubSystemPassword(Map<String, Object> params);

    void addSubAccount(Map<String, Object> params);

    int getSubAccountsNum();

    List<Map<String, Object>> getSubAccountsInfo();

    void deleteUser(Map<String, Object> params);

    void updateCommunity(Map<String, Object> params);

    void updateAllCommunity(Map<String, Object> params);

    String getCommunity(Map<String, Object> params);

}
