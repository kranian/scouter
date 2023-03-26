package scouter.agent.counter.task.pools;

import scouter.agent.ConfPool;
import scouter.agent.Logger;
import scouter.agent.counter.CounterBasket;
import scouter.agent.counter.anotation.Counter;
import scouter.agent.trace.JDBCPool;
import scouter.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.Enumeration;

public class HikariConnectionPool {

    public static String className = "HikariConnectionPool";

    public String id() {
        return className;
    }
    @Counter
    public void process(CounterBasket pw) {
        if(!ConfPool.hikari_pool_enabled){
            return;
        }
        try{
            Enumeration<WeakReference<Object>> poolEnum = JDBCPool.getObjectEnum(className);
            while(poolEnum.hasMoreElements()) {
                Object pool = poolEnum.nextElement().get();
                if(pool != null){
                    String poolName = pool.toString();

                    int act = PoolObjectReflect.getInt(pool, "getActiveConnections");
                    int idle = PoolObjectReflect.getInt(pool, "getIdleConnections");
                    int total = act + idle;

                    if(ConfPool.debug_pool_detail_enabled){
                        Logger.println(className, " pool="+poolName+" act=" + act + " idle=" + idle+" total="+total);
                    }
                }
            }

        }catch (Throwable e){
            Logger.println("A114","BasicDataSourcePool Error: "+ StringUtil.nullToEmpty(e.getMessage())+"hotfix: dbcp_pool_enabled=false");
        }


    }
}
