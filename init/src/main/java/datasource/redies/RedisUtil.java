//package datasource.redies;
//
//import java.util.concurrent.locks.ReentrantLock;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.math.NumberUtils;
//import io.netty.util.internal.StringUtil;
//import org.apache.log4j.Logger;
//import org.apache.velocity.texen.util.FileUtil;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
//
///**
// * Redis 工具类
// * @author caspar
// *
// */
//public class RedisUtil {
//
//    protected static ReentrantLock lockPool = new ReentrantLock();
//    protected static ReentrantLock lockJedis = new ReentrantLock();
//
//    protected static Logger logger = Logger.getLogger(RedisUtil.class);
//
//    //Redis服务器IP
//    private static String ADDR_ARRAY = FileUtil.getPropertyValue("/properties/redis.properties", "server");
//
//    //Redis的端口号
//    private static int PORT = FileUtil.getPropertyValueInt("/properties/redis.properties", "port");
//
//    //访问密码
////    private static String AUTH = FileUtil.getPropertyValue("/properties/redis.properties", "auth");
//
//    //可用连接实例的最大数目，默认值为8；
//    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
//    private static int MAX_ACTIVE = FileUtil.getPropertyValueInt("/properties/redis.properties", "max_active");;
//
//    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
//    private static int MAX_IDLE = FileUtil.getPropertyValueInt("/properties/redis.properties", "max_idle");;
//
//    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
//    private static int MAX_WAIT = FileUtil.getPropertyValueInt("/properties/redis.properties", "max_wait");;
//
//    //超时时间
//    private static int TIMEOUT = FileUtil.getPropertyValueInt("/properties/redis.properties", "timeout");;
//
//    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
//    private static boolean TEST_ON_BORROW = FileUtil.getPropertyValueBoolean("/properties/redis.properties", "test_on_borrow");;
//
//    private static JedisPool jedisPool = null;
//
//    /**
//     * redis过期时间,以秒为单位
//     */
//    public final static int EXRP_HOUR = 60*60;          //一小时
//    public final static int EXRP_DAY = 60*60*24;        //一天
//    public final static int EXRP_MONTH = 60*60*24*30;   //一个月
//
//    /**
//     * 初始化Redis连接池
//     */
//    private static void initialPool(){
//        try {
//            JedisPoolConfig config = new JedisPoolConfig();
//            config.setMaxTotal(MAX_ACTIVE);
//            config.setMaxIdle(MAX_IDLE);
//            config.setMaxWaitMillis(MAX_WAIT);
//            config.setTestOnBorrow(TEST_ON_BORROW);
//            jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[0], PORT, TIMEOUT);
//        } catch (Exception e) {
//            logger.error("First create JedisPool error : "+e);
//            try{
//                //如果第一个IP异常，则访问第二个IP
//                JedisPoolConfig config = new JedisPoolConfig();
//                config.setMaxTotal(MAX_ACTIVE);
//                config.setMaxIdle(MAX_IDLE);
//                config.setMaxWaitMillis(MAX_WAIT);
//                config.setTestOnBorrow(TEST_ON_BORROW);
//                jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[1], PORT, TIMEOUT);
//            }catch(Exception e2){
//                logger.error("Second create JedisPool error : "+e2);
//            }
//        }
//    }
//
//
//    /**
//     * 在多线程环境同步初始化
//     */
//    private static void poolInit() {
//        //断言 ，当前锁是否已经锁住，如果锁住了，就啥也不干，没锁的话就执行下面步骤
//        assert ! lockPool.isHeldByCurrentThread();
//        lockPool.lock();
//        try {
//            if (jedisPool == null) {
//                initialPool();
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        } finally {
//            lockPool.unlock();
//        }
//    }
//
//
//    public static Jedis getJedis() {
//        //断言 ，当前锁是否已经锁住，如果锁住了，就啥也不干，没锁的话就执行下面步骤
//        assert ! lockJedis.isHeldByCurrentThread();
//        lockJedis.lock();
//
//        if (jedisPool == null) {
//            poolInit();
//        }
//        Jedis jedis = null;
//        try {
//            if (jedisPool != null) {
//                jedis = jedisPool.getResource();
//            }
//        } catch (Exception e) {
//            logger.error("Get jedis error : "+e);
//        }finally{
//            returnResource(jedis);
//            lockJedis.unlock();
//        }
//        return jedis;
//    }
//
//
//    /**
//     * 释放jedis资源
//     * @param jedis
//     */
//    public static void returnResource(final Jedis jedis) {
//        if (jedis != null && jedisPool !=null) {
//            jedisPool.returnResource(jedis);
//        }
//    }
//
//
//    /**
//     * 设置 String
//     * @param key
//     * @param value
//     */
//    public synchronized static void setString(String key ,String value){
//        try {
//            value = StringUtil.isNullOrEmpty(value) ? "" : value;
//            getJedis().set(key,value);
//        } catch (Exception e) {
//            logger.error("Set key error : "+e);
//        }
//    }
//
//    /**
//     * 设置 过期时间
//     * @param key
//     * @param seconds 以秒为单位
//     * @param value
//     */
//    public synchronized static void setString(String key ,int seconds,String value){
//        try {
//            value = StringUtil.isNullOrEmpty(value) ? "" : value;
//            getJedis().setex(key, seconds, value);
//        } catch (Exception e) {
//            logger.error("Set keyex error : "+e);
//        }
//    }
//
//    /**
//     * 获取String值
//     * @param key
//     * @return value
//     */
//    public synchronized static String getString(String key){
//        if(getJedis() == null || !getJedis().exists(key)){
//            return null;
//        }
//        return getJedis().get(key);
//    }
//
//}