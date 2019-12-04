import service.impl.ServiceImpl;
import utils.AESUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * @Author: kingfans
 * @Date: 2018/12/4
 */
public class AESTest {

    public static void main(String[] args) {
//        String ip = "47.96.87.126";
//        String port = "21";
//        String username = "irving";
//        String password = "cavs4ever";
//        String en_ip = AESUtils.AESEncode("hzdkkrecharge23", ip);
//        String en_port = AESUtils.AESEncode("hzdkkrecharge23", port);
//        String en_username = AESUtils.AESEncode("hzdkkrecharge23", username);
//        String en_password = AESUtils.AESEncode("hzdkkrecharge23", password);
//        System.out.println("ip after aes : " + en_ip);
//        System.out.println("port after aes : " + en_port);
//        System.out.println("username after aes : " + en_username);
//        System.out.println("password after aes : " + en_password);

//        String aes_ip = "hUWWXYArmE4zvKUdesVKhQ==";
//        String original_ip = AESUtils.AESDecode("abcd", aes_ip);
//        System.out.println("after decode : " + original_ip);

        Map<String, Object> map = ServiceImpl.getInstance().getMap();
        System.out.println("param1 : " + AESUtils.AESDecode("hzdkkrecharge23", (String) map.get("param1")));
        System.out.println("param2 : " + AESUtils.AESDecode("hzdkkrecharge23", (String) map.get("param2")));
        System.out.println("param3 : " + AESUtils.AESDecode("hzdkkrecharge23", (String) map.get("param3")));
        System.out.println("param4 : " + AESUtils.AESDecode("hzdkkrecharge23", (String) map.get("param4")));
    }
}
