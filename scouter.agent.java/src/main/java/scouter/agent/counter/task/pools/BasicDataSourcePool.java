package scouter.agent.counter.task.pools;

import scouter.agent.ConfPool;
import scouter.agent.Logger;
import scouter.agent.counter.CounterBasket;
import scouter.agent.trace.JDBCPool;
import scouter.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.Enumeration;

public class BasicDataSourcePool {

    public static String className = "BasicDataSourcePool";

    public String id() {
        return className;
    }
    public void process(CounterBasket pw) {
        if(!ConfPool.dbcp_pool_enabled){
            return;
        }
        try{
            Enumeration<WeakReference<Object>> poolEnum = JDBCPool.getObjectEnum(className);
            while(poolEnum.hasMoreElements()) {
                Object pool = poolEnum.nextElement().get();
                if(pool != null){
                    String jdbcUrl  = PoolObjectReflect.getStringNoException(pool, "getUrl");
                    if(jdbcUrl !=null){
                        String url =StringUtil.removeLastString(jdbcUrl,'?');
                    }
                    int act = PoolObjectReflect.getInt(pool, "getNumActive");
                    int idle = PoolObjectReflect.getInt(pool, "getNumIdle");
                    int total = act + idle;

                }
            }



        }catch (Throwable e){
            Logger.println("A114","BasicDataSourcePool Error: "+ StringUtil.nullToEmpty(e.getMessage())+"hotfix: dbcp_pool_enabled=false");
        }


    }
}
