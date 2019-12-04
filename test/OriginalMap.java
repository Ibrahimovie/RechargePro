import service.impl.ServiceImpl;
import utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * @Author: kingfans
 * @Date: 2018/10/22
 */
public class OriginalMap {

    public static void main(String[] args) {
//        String str = "{now_time=2018-10-22 10:10:22, valid_day=198, card_number=5a5de836, top_up=100, card_type=3, community=%E6%9D%AD%E5%B7%9E%E5%BE%97%E5%BA%B7, operator=admin, recharge_time=240, balance=7800, phone=-, action=addRechargeHis, pay_rate=20, power_rate=100, username=-}";
//        Map<String, String> map = Utils.StringToOriginMap(str);
//        System.out.println(map.size());
//        for (String key : map.keySet()) {
//            String value = map.get(key);
//            System.out.println(key + " : " + value);
//        }
        List<Map<String, Object>> pushMsgList = ServiceImpl.getInstance().getPushMsgList();
//        System.out.println(pushMsgList);
//        for (int i = 0; i < pushMsgList.size(); i++) {
//            Map<String, Object> map = pushMsgList.get(i);
////            System.out.println(map);
//            String content = (String) map.get("content");
//            System.out.println("content : " + content);
//            System.out.println(content.contains("action"));
//            Map<String, String> m = Utils.StringToOriginMap2(content);
//            System.out.println("map : " + m);
//            System.out.println("size : " + m.size());
//            for (String key : m.keySet()) {
//                String value = m.get(key);
//                System.out.println(key + " : " + value);
//            }
//            System.out.println(m.get("action"));
//        }
        Map<String, Object> map = pushMsgList.get(2);
        String content = (String) map.get("content");
        System.out.println(content);
        Map<String, String> map1 = Utils.StringToMapDou(content);
        System.out.println(map1.get("action"));
    }
}
