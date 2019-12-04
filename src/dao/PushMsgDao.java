package dao;

import java.util.List;
import java.util.Map;

/**
 * @Author: kingfans
 * @Date: 2018/10/22
 */
public interface PushMsgDao {

    void addPushMsg(Map<String, Object> params);

    List<Map<String, Object>> getPushMsgList();

    void deletePushMsg(Map<String, Object> params);

}
