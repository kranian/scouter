package scouter.agent.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import scouter.agent.ClassDesc;
import scouter.agent.Configure;
import scouter.agent.asm.util.AsmUtil;
import scouter.agent.counter.task.pools.BasicDataSourcePool;
import scouter.agent.trace.JDBCPool;

import java.util.HashMap;
import java.util.Map;

public class ConnectionPoolASM implements IASM, Opcodes {
    public Map<String, String> target = new HashMap();

    public ConnectionPoolASM() {
        this.target.clear();
        if (Configure.getInstance().hook_default_pool_enabled) {
            this.setDefaultPoolClass();
        }

    }

    private void setDefaultPoolClass() {
        Configure configure = Configure.getInstance();
//        if (configure.hook_hikari_pool_enabled) {
//            this.target.put("com/zaxxer/hikari/pool/HikariPool", "HikariPool");
//        }

        if (configure.hook_dbcp_pool_enabled) {
            this.target.put("org/apache/commons/dbcp/BasicDataSource", BasicDataSourcePool.className);
            this.target.put("org/apache/commons/dbcp2/BasicDataSource", BasicDataSourcePool.className);
            this.target.put("org/apache/tomcat/dbcp/dbcp/BasicDataSource", BasicDataSourcePool.className);
            this.target.put("org/apache/tomcat/dbcp/dbcp2/BasicDataSource", BasicDataSourcePool.className);
        }

//        if (configure.hook_tomcat_pool_enabled) {
//            this.target.put("org/apache/tomcat/jdbc/pool/ConnectionPool", TomcatConnnPool.className);
//        }

//        if (configure.hook_weblogic_pool_enabled) {
//            this.target.put("weblogic/jdbc/common/internal/ConnectionPool", WebLogicConnPool.classNameId);
//        }

//        if (configure.hook_jeus_pool_enabled) {
//            this.target.put("jeus/jdbc/connectionpool/ConnectionPoolImpl", JeusConnPool.className);
//        }
//
//        if (configure.hook_jboss_pool_enabled) {
//            this.target.put("org/jboss/jca/core/connectionmanager/pool/PoolStatisticsImpl", JBossConnPool.className);
//        }

//        if (configure.hook_jedis_pool_enabled) {
//            this.target.put("redis/clients/jedis/JedisPool", RedisJedisPool.className);
//        }
//
//        if (configure.hook_c3p0_pool_enabled) {
//            this.target.put("com/mchange/v2/c3p0/ComboPooledDataSource", C3P0DataSourcePool.className);
//        }




    }

    @Override
    public ClassVisitor transform(ClassVisitor cv, String className, ClassDesc classDesc) {
        if(this.target.containsKey(className)){
            return new ConnectionPoolCV(cv,this.target.get(className));
        }
        return cv;
    }
}

class ConnectionPoolCV extends ClassVisitor implements Opcodes{
    public String classId;

    public ConnectionPoolCV(ClassVisitor cv, String classId){
        super(ASM9, cv);
        this.classId = classId;
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null ) {
            return mv;
        }
        if("<init>".equals(name)){
            return mv;
        }

        return new ConnectionPoolMV(access,desc,mv,this.classId);
    }
}
class ConnectionPoolMV extends LocalVariablesSorter implements Opcodes{
    private static final String TRACEAPICALL = JDBCPool.class.getName().replace('.', '/');
    private static final String METHOD = "register";
    private static final String SIGNATURE = "(Ljava/lang/String;Ljava/lang/Object;)V";
    public String classId;
    public ConnectionPoolMV(int access, String desc, MethodVisitor mv, String classId){
        super(ASM9, access, desc, mv);
        this.classId=classId;
    }
    @Override
    public void visitInsn(int opcode) {

        if ((opcode >= IRETURN && opcode <= RETURN)) {
            AsmUtil.PUSH(this.mv,this.classId);
            this.mv.visitVarInsn(Opcodes.ALOAD,0);
            this.mv.visitMethodInsn(Opcodes.INVOKESTATIC,TRACEAPICALL,METHOD,SIGNATURE,false);
        }
        this.mv.visitInsn(opcode);

    }
}