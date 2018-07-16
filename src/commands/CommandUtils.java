package commands;

import utils.*;

/**
 * @author kingfans
 */
public class CommandUtils {
    private static int generateCRC(byte[] buffer, int start, int count) {
        int crc = 65535;
        for (int j = start; j < start + count; j++) {
            crc = ((crc >>> 8 | crc << 8) & 0xFFFF);
            crc ^= (buffer[j] & 0xFF);
            crc ^= (crc & 0xFF) >> 4;
            crc ^= (crc << 12 & 0xFFFF);
            crc ^= ((crc & 0xFF) << 5 & 0xFFFF);
        }
        crc &= 0xFFFF;
        return crc;
    }

    public static byte[] isConnectCommand(int sectorOrder) {
        byte[] b = {(byte) 0xaa, (byte) 0xc3, 4, (byte) 0xa1, (byte) sectorOrder, 0, 0, (byte) 0xbb};
        int crc = generateCRC(b, 3, b.length - 6);
        byte[] crc2 = Utils.intToByteArray2(crc);
        b[5] = (byte) (crc2[1] & 0xFF);
        b[6] = (byte) (crc2[0] & 0xFF);
        return b;
    }

    public static byte[] queryCommand(String password) {
        byte[] b = {(byte) 0xaa, (byte) 0xc3, (byte) 0x0b, (byte) 0xa2,
                (byte) Integer.parseInt(String.valueOf(password.charAt(0))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(1))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(2))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(3))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(4))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(5))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(6))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(7))), 0, 0, (byte) 0xbb};
        int crc = generateCRC(b, 3, b.length - 6);
        byte[] crc2 = Utils.intToByteArray2(crc);
        b[12] = (byte) (crc2[1] & 0xFF);
        b[13] = (byte) (crc2[0] & 0xFF);
        return b;
    }

    public static byte[] registerCommand(String password, int deviceType) {
        byte[] b = {(byte) 0xaa, (byte) 0xc3, (byte) 0x0c, (byte) 0xa3, (byte) deviceType,
                (byte) Integer.parseInt(String.valueOf(password.charAt(0))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(1))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(2))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(3))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(4))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(5))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(6))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(7))), 0, 0, (byte) 0xbb};
        int crc = generateCRC(b, 3, b.length - 6);
        byte[] crc2 = Utils.intToByteArray2(crc);
        b[13] = (byte) (crc2[1] & 0xFF);
        b[14] = (byte) (crc2[0] & 0xFF);
        return b;
    }

    public static byte[] logoffCommand(String password, int deviceType) {
        byte[] b = {(byte) 0xaa, (byte) 0xc3, (byte) 0x0c, (byte) 0xa4, (byte) deviceType,
                (byte) Integer.parseInt(String.valueOf(password.charAt(0))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(1))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(2))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(3))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(4))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(5))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(6))),
                (byte) Integer.parseInt(String.valueOf(password.charAt(7))), 0, 0, (byte) 0xbb};
        int crc = generateCRC(b, 3, b.length - 6);
        byte[] crc2 = Utils.intToByteArray2(crc);
        b[13] = (byte) (crc2[1] & 0xFF);
        b[14] = (byte) (crc2[0] & 0xFF);
        return b;
    }

    public static byte[] rechargeCommand(int deviceType, int money, int lastTime, int startTime, int validDay, int rechargeTime, int payRate, int powerRate, int isReturn, String password) {
        byte[] moneyByte = Utils.intToByteArray2(money);
        byte[] startTimeByte = Utils.intToByteArray2(startTime);
        byte[] b = new byte[33];
        b[0] = (byte) 0xaa;
        b[1] = (byte) 0xc3;
        b[2] = (byte) 0x1d;
        b[3] = (byte) 0xa5;
        b[4] = (byte) deviceType;
        b[5] = 0;
        b[6] = 0;
        b[7] = moneyByte[0];
        b[8] = moneyByte[1];
        if (deviceType == 1) {
            byte[] lastTimeByte = Utils.intToByteArray2(lastTime);
            b[9] = lastTimeByte[0];
            b[10] = lastTimeByte[1];
            b[11] = 0;
            b[12] = 0;
        } else {
            byte[] lastTimeByte = Utils.intToByteArray4(lastTime);
            b[9] = lastTimeByte[0];
            b[10] = lastTimeByte[1];
            b[11] = lastTimeByte[2];
            b[12] = lastTimeByte[3];
        }
        b[13] = startTimeByte[0];
        b[14] = startTimeByte[1];
        b[15] = 0;
        b[16] = (byte) validDay;
        b[17] = (byte) rechargeTime;
        b[18] = (byte) payRate;
        b[19] = 0;
        b[20] = (byte) powerRate;
        b[21] = (byte) isReturn;
        b[22] = (byte) Integer.parseInt(String.valueOf(password.charAt(0)));
        b[23] = (byte) Integer.parseInt(String.valueOf(password.charAt(1)));
        b[24] = (byte) Integer.parseInt(String.valueOf(password.charAt(2)));
        b[25] = (byte) Integer.parseInt(String.valueOf(password.charAt(3)));
        b[26] = (byte) Integer.parseInt(String.valueOf(password.charAt(4)));
        b[27] = (byte) Integer.parseInt(String.valueOf(password.charAt(5)));
        b[28] = (byte) Integer.parseInt(String.valueOf(password.charAt(6)));
        b[29] = (byte) Integer.parseInt(String.valueOf(password.charAt(7)));
        b[30] = 0;
        b[31] = 0;
        b[32] = (byte) 0xbb;
        int crc = generateCRC(b, 3, b.length - 6);
        byte[] crc2 = Utils.intToByteArray2(crc);
        b[30] = (byte) (crc2[1] & 0xFF);
        b[31] = (byte) (crc2[0] & 0xFF);
        return b;
    }
}
