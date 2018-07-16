package bean.manager;

import java.util.*;

import swing.*;

/**
 * @author kingfans
 */
public class FrameManager {

    private static final HashMap<String, RechargeFrame> frameMap = new HashMap<>();

    public static void addFrame(String frameName, RechargeFrame f) {
        synchronized (frameMap) {
            if (frameMap.get(frameName) != null) {
                frameMap.remove(frameName);
                frameMap.put(frameName, f);
            } else {
                frameMap.put(frameName, f);
            }
        }
    }

    public static void removeFrame(String frameName) {
        synchronized (frameMap) {
            if (frameMap.get(frameName) != null) {
                frameMap.remove(frameName);
            }
        }
    }

    public static RechargeFrame getFrameByName(String frameName) {
        synchronized (frameMap) {
            return frameMap.get(frameName);
        }
    }

}
