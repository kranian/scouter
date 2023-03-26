package scouter.agent.counter.task.pools;

import scouter.agent.ConfPool;
import scouter.agent.Logger;
import scouter.agent.counter.CounterBasket;
import scouter.agent.counter.anotation.Counter;
import scouter.agent.trace.JDBCPool;
import scouter.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.Enumeration;

public class TomcatConnectionPool {

    public static String className = "TomcatConnectionPool";

    public String id() {
        return className;
    }
    @Counter
    public void process(CounterBasket pw) {
        if(!ConfPool.tomcat_pool_enabled){
            return;
        }
        try{
            Enumeration<WeakReference<Object>> poolEnum = JDBCPool.getObjectEnum(className);
            while(poolEnum.hasMoreElements()) {
                Object pool = poolEnum.nextElement().get();
                Logger.println("TomcatConnectionPool"," pool: "+pool);
                if(pool != null){
                    String jdbcUrl  = PoolObjectReflect.getStringNoException(pool, "getName");
                    if(jdbcUrl !=null){
                        String url =StringUtil.removeLastString(jdbcUrl,'?');
                    }
                    int act = PoolObjectReflect.getInt(pool, "getActive");
                    int idle = PoolObjectReflect.getInt(pool, "getIdle");
                    int total = act + idle;

                    if(ConfPool.debug_pool_detail_enabled){
                        Logger.println("TomcatConnectionPool"," pool="+jdbcUrl+" act=" + act + " idle=" + idle+" total="+total);
                    }
                }
            }

        }catch (Throwable e){
            Logger.println("A114","TomcatConnectionPool Error: "+ StringUtil.nullToEmpty(e.getMessage())+",hotfix: tomcat_pool_enabled=false");
        }


    }
}
