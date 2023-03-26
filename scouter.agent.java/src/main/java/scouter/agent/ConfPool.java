package scouter.agent;

public class ConfPool {
    public static boolean counter_pool_enabled = true;
    public static int counter_pool_interval = 5000;
    public static boolean tomcat_ds_enabled = false;
    public static boolean weblogic_ds_enabled = false;
    public static boolean hikari_pool_enabled = true;
    public static boolean dbcp_pool_enabled = true;
    public static boolean tomcat_pool_enabled = true;
    public static boolean weblogic_pool_enabled = true;
    public static boolean jeus_pool_enabled = true;
    public static boolean jboss_pool_enabled = true;
    public static boolean jedis_pool_enabled = true;
    public static boolean hybris_pool_enabled = true;
    public static boolean c3p0_pool_enabled = true;
    public static boolean druid_pool_enabled = false;
    public static boolean pool_detail_enabled = true;
    public static boolean debug_pool_detail_enabled = false;

    public static void apply(Configure conf) {
        counter_pool_enabled = conf.hook_default_pool_enabled && conf.getBoolean("counter_pool_enabled", true);
        counter_pool_interval = conf.getInt("counter_pool_interval", conf.getInt("ds_update_interval", 5000));
        tomcat_ds_enabled = conf.getBoolean("tomcat_ds_enabled", false);
        weblogic_ds_enabled = conf.getBoolean("weblogic_ds_enabled", false);
        hikari_pool_enabled = conf.getBoolean("hikari_pool_enabled", true);
        tomcat_pool_enabled = conf.getBoolean("tomcat_pool_enabled", true);
        dbcp_pool_enabled = conf.getBoolean("dbcp_pool_enabled", true);
        weblogic_pool_enabled = conf.getBoolean("weblogic_pool_enabled", true);
        jeus_pool_enabled = conf.getBoolean("jeus_pool_enabled", true);
        jboss_pool_enabled = conf.getBoolean("jboss_pool_enabled", true);
        jedis_pool_enabled = conf.getBoolean("jedis_pool_enabled", true);
        hybris_pool_enabled = conf.getBoolean("hybris_pool_enabled", true);
        c3p0_pool_enabled = conf.getBoolean("c3p0_pool_enabled", true);
        druid_pool_enabled = conf.getBoolean("druid_pool_enabled", false);
        pool_detail_enabled = conf.getBoolean("pool_detail_enabled", false);
        debug_pool_detail_enabled = conf.getBoolean("debug_pool_detail_enabled", false);
        if (dbcp_pool_enabled || hikari_pool_enabled) {
            weblogic_ds_enabled = false;
            tomcat_ds_enabled = false;
        }

        if (weblogic_pool_enabled) {
            weblogic_ds_enabled = false;
        }


    }
}
