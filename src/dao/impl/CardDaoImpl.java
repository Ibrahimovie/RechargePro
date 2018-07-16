package dao.impl;

import dao.CardDao;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import java.util.Map;

/**
 * @author kingfans
 */
public class CardDaoImpl implements CardDao {
    private SqlMapClientTemplate sqlMapClientTemplate;

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    @Override
    public boolean isCardExist(Map<String, Object> params) {
        return ((int) sqlMapClientTemplate.queryForObject("cardDao.isCardExist", params)) > 0;
    }

    @Override
    public void updateCardInfo(Map<String, Object> params) {
        sqlMapClientTemplate.update("cardDao.updateCardInfo", params);
    }

    @Override
    public void updateCardInfoQ10(Map<String, Object> params) {
        sqlMapClientTemplate.update("cardDao.updateCardInfoQ10", params);
    }

    @Override
    public void updateCardInfoExTime(Map<String, Object> params) {
        sqlMapClientTemplate.update("cardDao.updateCardInfoExTime", params);
    }

    @Override
    public void addCard(Map<String, Object> params) {
        sqlMapClientTemplate.update("cardDao.addCard", params);
    }

    @Override
    public void addNewCard(Map<String, Object> params) {
        sqlMapClientTemplate.update("cardDao.addNewCard", params);
    }

    @Override
    public int getCardTypeByNum(Map<String, Object> params) {
        return (int) sqlMapClientTemplate.queryForObject("cardDao.getCardTypeByNum", params);
    }

    @Override
    public String getUsernameByCardNum(Map<String, Object> params) {
        return (String) sqlMapClientTemplate.queryForObject("cardDao.getUsernameByCardNum", params);
    }

    @Override
    public String getCardPhoneByNum(Map<String, Object> params) {
        return (String) sqlMapClientTemplate.queryForObject("cardDao.getCardPhoneByNum", params);
    }

    @Override
    public String getLastTimeByNum(Map<String, Object> params) {
        return (String) sqlMapClientTemplate.queryForObject("cardDao.getLastTimeByNum", params);
    }

    @Override
    public String getStartTimeByNum(Map<String, Object> params) {
        return (String) sqlMapClientTemplate.queryForObject("cardDao.getStartTimeByNum", params);
    }

    @Override
    public void deleteCard(Map<String, Object> params) {
        sqlMapClientTemplate.delete("cardDao.deleteCard", params);
    }
}
