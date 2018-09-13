package swing;

import service.impl.ServiceImpl;

/**
 * @Author: kingfans
 * @Date: 2018/9/6
 */
public class Main {
    public static void main(String[] args) {
        //查询sqlite是否已选择小区
        String community = ServiceImpl.getInstance().getCommunity("admin");
        System.out.println("query community : " + community);
        if (!"".equals(community.trim()) && community != null && !"-".equals(community)) {
            System.out.println("已选择小区 : " + community);
            new LoginFrame();
        } else {
            System.out.println("还未选择小区!");
            new CommunityFrame();
        }
    }
}
