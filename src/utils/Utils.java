package utils;

import java.util.*;
import java.io.*;

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

    public static String getStringCardType(final int card) {
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

    public static void byteToLog(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            System.out.print(Integer.toHexString(b[i] & 0xFF) + " ");
        }
        System.out.println();
    }

    public static int exchangeByte(int i) {
        byte[] bytes = {(byte) (i >> 8 & 0xFF), (byte) (i & 0xFF)};
        return (bytes[0] & 0xFF) | (bytes[1] & 0xFF) << 8;
    }
}
