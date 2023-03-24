package scouter.agent.counter.task.pools;

import scouter.util.CastUtil;

import java.lang.reflect.Method;

public class PoolObjectReflect {

    public static int getInt(Object o, String method) throws Exception {
        Method m = o.getClass().getMethod(method);
        m.setAccessible(true);
        Object out = m.invoke(o);
        return CastUtil.cint(out);
    }

    public static long getLong(Object o, String method) throws Exception {
        Method m = o.getClass().getMethod(method);
        m.setAccessible(true);
        Object out = m.invoke(o);
        return CastUtil.clong(out);
    }

    public static String getStringNoException(Object o, String method) {
        try {
            Method m = o.getClass().getMethod(method);
            m.setAccessible(true);
            Object out = m.invoke(o);
            return CastUtil.cString(out);
        } catch (Exception var4) {
            return null;
        }
    }
}
