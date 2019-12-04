package bean.manager;

import swing.ProgressBarFrame;

import java.util.HashMap;

/**
 * @author kingfans
 */
public class ProgressFrameManager {

    private static final HashMap<String, ProgressBarFrame> frameMap = new HashMap<>();

    public static void addFrame(String frameName, ProgressBarFrame f) {
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

    public static ProgressBarFrame getFrameByName(String frameName) {
        synchronized (frameMap) {
            return frameMap.get(frameName);
        }
    }

}
