package bean.manager;

import java.util.*;

import bean.*;

/**
 * @author kingfans
 */
public class CardManager {

    private static final HashMap<String, Card> cardMap = new HashMap<>();

    public static void addCard(String cardNum, Card card) {
        synchronized (cardMap) {
            if (cardMap.get(cardNum) != null) {
                cardMap.remove(cardNum);
                cardMap.put(cardNum, card);
            } else {
                cardMap.put(cardNum, card);
            }
        }
    }

    public static void deleteCard(String cardNum) {
        synchronized (cardMap) {
            if (cardMap.get(cardNum) != null) {
                cardMap.remove(cardNum);
            }
        }
    }

    public static Card getCardByNum(String cardNum) {
        synchronized (cardMap) {
            return cardMap.get(cardNum);
        }
    }

}
