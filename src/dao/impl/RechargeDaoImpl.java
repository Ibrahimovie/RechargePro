package dao.impl;

import dao.*;
import org.springframework.orm.ibatis.*;

import java.util.*;

/**
 * @author kingfans
 */
public class RechargeDaoImpl implements RechargeDao {
    private SqlMapClientTemplate sqlMapClientTemplate;

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    @Override
    public void addRechargeHis(Map<String, Object> params) {
        sqlMapClientTemplate.update("rechargeDao.addRechargeHis", params);
    }

    @Override
    public int getRechargeCount() {
        return (int) sqlMapClientTemplate.queryForObject("rechargeDao.getRechargeCount");
    }

    @Override
    public int getRechargeCountRangeWithPhone(Map<String, Object> params) {
        return (int) sqlMapClientTemplate.queryForObject("rechargeDao.getRechargeCountRangeWithPhone", params);
    }

    @Override
    public int getRechargeCountRangeWithoutPhone(Map<String, Object> params) {
        return (int) sqlMapClientTemplate.queryForObject("rechargeDao.getRechargeCountRangeWithoutPhone", params);
    }

    @Override
    public List<Map<String, Object>> getRechargeHisAll() {
        return (List<Map<String, Object>>) sqlMapClientTemplate.queryForList("rechargeDao.getRechargeHisAll");
    }

    @Override
    public List<Map<String, Object>> getRechargeHisRangeWithPhone(Map<String, Object> params) {
        return (List<Map<String, Object>>) sqlMapClientTemplate.queryForList("rechargeDao.getRechargeHisRangeWithPhone", params);
    }

    @Override
    public List<Map<String, Object>> getRechargeHisRangeWithoutPhone(Map<String, Object> params) {
        return (List<Map<String, Object>>) sqlMapClientTemplate.queryForList("rechargeDao.getRechargeHisRangeWithoutPhone", params);
    }
}
