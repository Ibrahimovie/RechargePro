import utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: kingfans
 * @Date: 2019/1/17
 */
public class Test1 {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse("2019-01-16");
        Date endDate  = Utils.getDateAfter(startDate,6);
        System.out.println(sdf.format(endDate));



    }
}
