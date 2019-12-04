package bean.manager;

import swing.ProgressBarWithTipFrame;

import java.util.HashMap;

/**
 * @author kingfans
 */
public class ProgressFrameWithTipManager {

    private static final HashMap<String, ProgressBarWithTipFrame> frameMap = new HashMap<>();

    public static void addFrame(String frameName, ProgressBarWithTipFrame f) {
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

    public static ProgressBarWithTipFrame getFrameByName(String frameName) {
        synchronized (frameMap) {
            return frameMap.get(frameName);
        }
    }

}
