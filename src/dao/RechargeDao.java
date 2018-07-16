package dao;


import java.util.List;
import java.util.Map;

/**
 * @author kingfans
 */
public interface RechargeDao {

    void addRechargeHis(Map<String, Object> params);

    int getRechargeCount();

    int getRechargeCountRangeWithPhone(Map<String, Object> params);

    int getRechargeCountRangeWithoutPhone(Map<String, Object> params);

    List<Map<String, Object>> getRechargeHisAll();

    List<Map<String, Object>> getRechargeHisRangeWithPhone(Map<String, Object> params);

    List<Map<String, Object>> getRechargeHisRangeWithoutPhone(Map<String, Object> params);
}
