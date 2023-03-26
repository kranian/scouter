package scouter.agent.counter.task.pools;

import scouter.agent.ConfPool;
import scouter.agent.Logger;
import scouter.agent.counter.CounterBasket;
import scouter.agent.counter.anotation.Counter;
import scouter.agent.trace.JDBCPool;
import scouter.util.StringUtil;

import java.lang.ref.WeakReference;
import java.util.Enumeration;

public class JeusConnectionPool {

    public static String className = "JeusConnectionPool";
    private static int jeus =0;
    public String id() {
        return className;
    }
    @Counter
    public void process(CounterBasket pw) {
        if(!ConfPool.jeus_pool_enabled){
            return;
        }
        try{
            Enumeration<WeakReference<Object>> poolEnum = JDBCPool.getObjectEnum(className);
            while(poolEnum.hasMoreElements()) {
                Object pool = poolEnum.nextElement().get();
                if(pool != null){
                    String getName  = PoolObjectReflect.getStringNoException(pool, "getName");

                    int total=0;
                    int idle =0;

                    try {
                        total = PoolObjectReflect.getInt(pool, "getCurrentPoolSize");
                    } catch (Exception e) {
                        Logger.println("JEUS-POOL",  "JEUS Connection Pool : " + e);
                        continue;
                    }
                    switch(jeus) {
                        case 0:
                            try {
                                idle = PoolObjectReflect.getInt(pool, "getIdlePoolSize");
                                jeus = 6;
                            } catch (Exception var11) {
                                idle = PoolObjectReflect.getInt(pool, "getNumberOfIdleConnections");
                                jeus = 7;
                            }
                            break;
                        case 6:
                            idle = PoolObjectReflect.getInt(pool, "getIdlePoolSize");
                            break;
                        case 7:
                            idle = PoolObjectReflect.getInt(pool, "getNumberOfIdleConnections");
                    }

                    int act = total - idle;
                    if(ConfPool.debug_pool_detail_enabled){
                        Logger.println("JEUS-POOL"," pool="+getName+" act=" + act + " idle=" + idle+" total="+total);
                    }
                }
            }

        }catch (Throwable e){
            Logger.println("A114","JEUS-POOL Error: "+ StringUtil.nullToEmpty(e.getMessage())+",hotfix: jeus_pool_enabled=false");
        }


    }
}
