package dao;

import java.util.Map;

/**
 * @author kingfans
 */
public interface CardDao {

    boolean isCardExist(Map<String, Object> params);

    void updateCardInfo(Map<String, Object> params);

    void updateCardInfoQ10(Map<String, Object> params);

    void updateCardInfoExTime(Map<String, Object> params);

    void addCard(Map<String, Object> params);

    void addNewCard(Map<String, Object> params);

    int getCardTypeByNum(Map<String, Object> params);

    String getUsernameByCardNum(Map<String, Object> params);

    String getCardPhoneByNum(Map<String, Object> params);

    String getLastTimeByNum(Map<String, Object> params);

    String getStartTimeByNum(Map<String, Object> params);

    void deleteCard(Map<String, Object> params);
}
