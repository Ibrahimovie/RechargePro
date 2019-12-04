package utils;

import com.alibaba.fastjson.JSON;

import java.util.*;
import java.io.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

/**
 * @author kingfans
 */
public class Utils {
    public static Properties newProperties(String fileName) {
        FileInputStream is = null;
        Properties p = null;
        try {
            is = new FileInputStream(fileName);
            p = new Properties();
            p.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static int getPortrate(int order) {
        int portrate = 0;
        switch (order) {
            case 0:
                portrate = 115200;
                break;
            case 1:
                portrate = 57600;
                break;
            case 2:
                portrate = 38400;
                break;
            case 3:
                portrate = 19200;
                break;
            case 4:
                portrate = 9600;
                break;
            default:
                break;
        }
        return portrate;
    }

    public static int getIntCardType(String card) {
        int cardType = 0;
        switch (card) {
            case "Q10/CDZ":
                break;
            case "Q20(电子钱包A)":
                cardType = 1;
                break;
            case "Q20(电子钱包B)":
                cardType = 2;
                break;
            case "Q20(包月卡)":
                cardType = 3;
                break;
            default:
                break;
        }
        return cardType;
    }

    public static String getStringCardType(int card) {
        String cardType = null;
        switch (card) {
            case 0:
                cardType = "Q10/CDZ";
                break;
            case 1:
                cardType = "Q20(电子钱包A)";
                break;
            case 2:
                cardType = "Q20(电子钱包B)";
                break;
            case 3:
                cardType = "Q20(包月卡)";
                break;
            default:
                break;
        }
        return cardType;
    }


    public static int crc16(final byte[] buffer, int start, int count) {

        int crc = 65535;
        for (int j = start; j < start + count; j++) {
            crc = ((crc >>> 8 | crc << 8) & 0xFFFF);
            crc ^= (buffer[j] & 0xFF);
            crc ^= (crc & 0xFF) >> 4;
            crc ^= (crc << 12 & 0xFFFF);
            crc ^= ((crc & 0xFF) << 5) & 0xFFFF;
        }
        crc &= 0xFFFF;
        return crc;
    }

    public static byte[] intToByteArray4(int i) {
        byte[] result = {(byte) (i >> 24 & 0xFF), (byte) (i >> 16 & 0xFF), (byte) (i >> 8 & 0xFF), (byte) (i & 0xFF)};
        return result;
    }

    public static byte[] intToByteArray2(int i) {
        byte[] result = {(byte) (i >> 8 & 0xFF), (byte) (i & 0xFF)};
        return result;
    }

    public static int exchangeByte(int i) {
        byte[] bytes = {(byte) (i >> 8 & 0xFF), (byte) (i & 0xFF)};
        return (bytes[0] & 0xFF) | (bytes[1] & 0xFF) << 8;
    }

    public static String MapToJsonString(Map<String, Object> map) {
        return JSON.toJSON(map).toString();
    }

    public static String MapToJsonStringDou(Map<String, String> map) {
        return JSON.toJSON(map).toString();
    }

    public static Map<String, Object> StringToMap(String str) {
        return JSON.parseObject(str, Map.class);
    }

    public static Map<String, String> StringToMapDou(String str) {
        return JSON.parseObject(str, Map.class);
    }

    public static Map<String, String> StringToOriginMap(String str) {
        Map<String, String> map = new HashMap<>();
        if (str.startsWith("{")) {
            str = str.substring(1, str.length());
        }
        if (str.endsWith("}")) {
            str = str.substring(0, str.length() - 1);
        }
        String[] array = str.split(",");
        for (String item : array) {
            String[] temp = item.split("=");
            map.put(temp[0], temp[1]);
        }
        return map;

    }

    public static Map<String, String> StringToOriginMap2(String str) {
        str = str.substring(1, str.length() - 1);
        Map map = new HashMap();
        StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(str, ","); entrys.hasMoreTokens();
             map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null)) {
            items = new StringTokenizer(entrys.nextToken(), "=");
        }
        return map;
    }

    public static byte[] File2byte(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        return dirFile.delete();
    }

    public static byte[] FileToByte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static File ByteToFile(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }

    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }

    public static List findFileName(String filepath) {
        File file = new File(filepath);
        List list = new ArrayList();
        File[] files = file.listFiles();
        System.out.println("files size : " + files.length);
        for (int i = 0; i < files.length; i++) {
            list.add(files[i].getName());
        }
        return list;
    }

    //两个日期相差的天数
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    //几天后的日期
    public static Date getDateAfter(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)+day);
        return now.getTime();
    }


}
