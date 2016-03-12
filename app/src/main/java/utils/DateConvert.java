package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ws02 on 2016/1/8.
 */
public class DateConvert {
    public static DateConvert getInstance() {
        if (instance==null){
            instance=new DateConvert();
        }
        return instance;
    }

    private static DateConvert instance;
    private DateConvert(){}
    public String transferLongToDate(Long millSec){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Date date= new Date(millSec);

        return sdf.format(date);

    }
}
