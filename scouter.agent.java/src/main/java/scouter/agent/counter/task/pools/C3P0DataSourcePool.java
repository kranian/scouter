package scouter.agent.counter.task.pools;

import scouter.agent.ConfPool;
import scouter.agent.Logger;
import scouter.agent.counter.CounterBasket;
import scouter.agent.counter.anotation.Counter;
import scouter.agent.trace.JDBCPool;
import scouter.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.Enumeration;

public class C3P0DataSourcePool {
    public static String className = "C3P0DataSourcePool";

    public String id() {
        return className;
    }
    @Counter
    public void process(CounterBasket pw) {
        if(!ConfPool.c3p0_pool_enabled){
            return;
        }
        try{
            Enumeration<WeakReference<Object>> poolEnum = JDBCPool.getObjectEnum(className);
            while(poolEnum.hasMoreElements()) {
                Object pool = poolEnum.nextElement().get();
                if(pool != null){
                    String jdbcUrl  = PoolObjectReflect.getStringNoException(pool, "getJdbcUrl");
                    if(jdbcUrl !=null){
                        String url = StringUtil.removeLastString(jdbcUrl,'?');
                    }
                    int act = PoolObjectReflect.getInt(pool, "getNumBusyConnections");
                    int idle = PoolObjectReflect.getInt(pool, "getNumIdleConnections");
                    int total = act + idle;

                    if(ConfPool.debug_pool_detail_enabled){
                        Logger.println("C3P0DataSourcePool"," pool="+jdbcUrl+" act=" + act + " idle=" + idle+" total="+total);
                    }
                }
            }

        }catch (Throwable e){
            Logger.println("A114","C3P0DataSourcePool Error: "+ StringUtil.nullToEmpty(e.getMessage())+", hotfix: c3p0_pool_enabled=false");
        }


    }
}
