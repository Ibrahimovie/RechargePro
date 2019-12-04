package dao.impl;

import dao.PushMsgDao;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Author: kingfans
 * @Date: 2018/10/22
 */
public class PushMsgDaoImpl implements PushMsgDao {

    private SqlMapClientTemplate sqlMapClientTemplate;

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    @Override
    public void addPushMsg(Map<String, Object> params) {
        sqlMapClientTemplate.update("pushMsgDao.addPushMsg", params);
    }

    @Override
    public List<Map<String, Object>> getPushMsgList() {
        return (List<Map<String, Object>>) sqlMapClientTemplate.queryForList("pushMsgDao.getPushMsgList");
    }

    @Override
    public void deletePushMsg(Map<String, Object> params) {
        sqlMapClientTemplate.delete("pushMsgDao.deletePushMsg", params);
    }
}
