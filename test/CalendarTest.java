import utils.Utils;

import javax.rmi.CORBA.Util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: kingfans
 * @Date: 2019/1/16
 */
public class CalendarTest {

    public static void main(String[] args) {
        String s = "2019-01-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date ago = new Date();
        try {
            ago = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date now = new Date();
        int days = Utils.differentDaysByMillisecond(ago,now);
        System.out.println(days);


    }
}
