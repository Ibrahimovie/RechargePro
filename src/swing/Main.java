package swing;

import service.impl.ServiceImpl;

/**
 * @Author: kingfans
 * @Date: 2018/9/6
 */
public class Main {
    public static void main(String[] args) {
        //查询sqlite是否已选择小区
//        String community = ServiceImpl.getInstance().getCommunity("admin");
//        System.out.println("查询出所在小区 : " + community);
//        if (!"".equals(community.trim()) && community != null && !"-".equals(community)) {
//            System.out.println("去登陆");
        new LoginFrame();
//        } else {
//            System.out.println("先选择小区");
//            new CommunityFrame();
//        }
    }
}
