package dao.impl;

import dao.UckImberOrtDao;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import java.util.Map;

/**
 * @Author: kingfans
 * @Date: 2018/12/4
 */
public class UckImberOrtDaoImpl implements UckImberOrtDao {

    private SqlMapClientTemplate sqlMapClientTemplate;

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    @Override
    public Map<String, Object> getMap() {
        return (Map<String, Object>) sqlMapClientTemplate.queryForObject("uckImberOrtDao.getMap");
    }
}
