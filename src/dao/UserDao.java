package dao;

import java.util.*;

/**
 * @author kingfans
 */
public interface UserDao {
    boolean isUserExist(Map<String, Object> params);

    Map<String, Object> getUserInfo(Map<String, Object> params);

    void updateSectorOrder(Map<String, Object> params);

    void updatePortrateOrder(Map<String, Object> params);

    void updatePortOrder(Map<String, Object> params);

    void updatePassword(Map<String, Object> params);

    void updateUsername(Map<String, Object> params);

    void updateSystemPassword(Map<String, Object> params);
}
