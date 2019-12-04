package bean.manager;

import bean.LogoutCard;
import bean.User;

import java.util.HashMap;

/**
 * @Author: kingfans
 * @Date: 2018/10/27
 */
public class LogoutCardManager {

    private static final HashMap<String, LogoutCard> LogoutCardMap = new HashMap<>(1);

    public static void addCard(LogoutCard card) {
        synchronized (LogoutCardMap) {
            if (!LogoutCardMap.isEmpty()) {
                LogoutCardMap.clear();
            }
            LogoutCardMap.put(card.getCardNum(), card);
        }
    }

    public static void removeCard() {
        synchronized (LogoutCardMap) {
            LogoutCardMap.clear();
        }
    }

    public static LogoutCard getCard(String cardNum) {
        synchronized (LogoutCardMap) {
            return LogoutCardMap.get(cardNum);
        }
    }
}
