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
    public String getAdminPassword() {
        return (String) sqlMapClientTemplate.queryForObject("userDao.getAdminPassword");
    }

    @Override
    public ArrayList<String> getSubUsername() {
        return (ArrayList<String>) sqlMapClientTemplate.queryForList("userDao.getSubUsername");
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
    public void updateSubSectorOrder(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSubSectorOrder", params);
    }

    @Override
    public void updatePortrateOrder(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updatePortrateOrder", params);
    }

    @Override
    public void updateSubPortrateOrder(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSubPortrateOrder", params);
    }

    @Override
    public void updatePortOrder(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updatePortOrder", params);
    }

    @Override
    public void updateSubPortOrder(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSubPortOrder", params);
    }

    @Override
    public void updatePassword(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updatePassword", params);
    }

    @Override
    public void updateSubPassword(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSubPassword", params);
    }

    @Override
    public void updateSubUserPasswd(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSubUserPasswd", params);
    }

    @Override
    public void updateUsername(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateUsername", params);
    }

    @Override
    public void updateSystemPassword(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSystemPassword", params);
    }

    @Override
    public void updateSubSystemPassword(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateSubSystemPassword", params);
    }

    @Override
    public void addSubAccount(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.addSubAccount", params);
    }

    @Override
    public int getSubAccountsNum() {
        return (int) sqlMapClientTemplate.queryForObject("userDao.getSubAccountsNum");
    }

    @Override
    public List<Map<String, Object>> getSubAccountsInfo() {
        return (List<Map<String, Object>>) sqlMapClientTemplate.queryForList("userDao.getSubAccountsInfo");
    }

    @Override
    public void deleteUser(Map<String, Object> params) {
        sqlMapClientTemplate.delete("userDao.deleteUser", params);
    }

    @Override
    public void updateCommunity(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateCommunity", params);
    }

    @Override
    public void updateAllCommunity(Map<String, Object> params) {
        sqlMapClientTemplate.update("userDao.updateAllCommunity", params);
    }

    @Override
    public String getCommunity(Map<String, Object> params) {
        return (String) sqlMapClientTemplate.queryForObject("userDao.getCommunity", params);
    }
}
