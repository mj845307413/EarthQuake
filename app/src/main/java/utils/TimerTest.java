package utils;

import java.lang.reflect.Field;
import java.util.TimerTask;

/**
 * Created by ws02 on 2016/1/18.
 */
public abstract class TimerTest extends TimerTask {
    public void setPeriod(long period) {
        //缩短周期，执行频率就提高
        setDeclaredField(TimerTask.class, this, "period", period);
    }

    //通过反射修改字段的值
    static boolean setDeclaredField(Class<?> clazz, Object obj,
                                    String name, Object value) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
