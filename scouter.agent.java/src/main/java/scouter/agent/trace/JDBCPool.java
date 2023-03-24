package scouter.agent.trace;

import scouter.agent.Logger;
import scouter.util.IntKeyLinkedMap;
import scouter.util.StringEnumer;
import scouter.util.StringKeyLinkedMap;

import java.lang.ref.WeakReference;
import java.util.Enumeration;

public class JDBCPool {
    static StringKeyLinkedMap<IntKeyLinkedMap<WeakReference<Object>>> pools = (new StringKeyLinkedMap<IntKeyLinkedMap<WeakReference<Object>>>() {
        protected IntKeyLinkedMap<WeakReference<Object>> create(String key) {
            return (new IntKeyLinkedMap()).setMax(100);
        }
    }).setMax(100);
    public static final Enumeration<WeakReference<Object>> empty = new Enumeration<WeakReference<Object>>() {
        public WeakReference<Object> nextElement() {
            return null;
        }

        public boolean hasMoreElements() {
            return false;
        }
    };

    public static void register(String id, Object conPool) {
        if (conPool != null) {
            int key = System.identityHashCode(conPool);
            IntKeyLinkedMap<WeakReference<Object>> table = pools.intern(id);
            if (!table.containsKey(key)) {
                table.put(key, new WeakReference(conPool));
                Logger.info("REGISTER-POOL id=" + id + " class=" + conPool.getClass().getName());
            }

        }
    }

    public static Enumeration<WeakReference<Object>> getObjectEnum(String name) {
        IntKeyLinkedMap<WeakReference<Object>> pool = pools.get(name);
        return pool != null ? pool.values() : empty;
    }

    public static boolean isEmpty(Object o) {
        return o == empty || o == null;
    }

    public static StringEnumer keys() {
        return pools.keys();
    }

}
