package dao.impl;

import dao.*;
import org.springframework.orm.ibatis.*;

import java.util.*;

/**
 * @author kingfans
 */
public class UserDaoImpl implements UserDao {
    private SqlMapClientTemplate sqlMapClientTemplate;

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    @Override
    public boolean isUserExist(Map<String, Object> params) {
        return ((int) sqlMapClientTemplate.queryForObject("userDao.isUserExist", params)) > 0;
    }

    @Override
    public Map<String, Object> getUserInfo(Map<String, Object> params) {
        return (Map<String, Object>) sqlMapClientTemplate.queryForObject("userDao.getUserInfo", params);
    }

    @Override
    public void updateSectorOrder(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSectorOrder", params);
    }

    @Override
    public void updatePortrateOrder(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updatePortrateOrder", params);
    }

    @Override
    public void updatePortOrder(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updatePortOrder", params);
    }

    @Override
    public void updatePassword(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updatePassword", params);
    }

    @Override
    public void updateUsername(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateUsername", params);
    }

    @Override
    public void updateSystemPassword(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSystemPassword", params);
    }
}
