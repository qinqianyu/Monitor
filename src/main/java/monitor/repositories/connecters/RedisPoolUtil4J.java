package monitor.repositories.connecters;

import com.redislabs.modules.rejson.JReJSON;
import io.rebloom.client.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 用来连接redis的连接池
 */
public class RedisPoolUtil4J {
    private static final String redisHost = "192.168.20.138";//redis的ip地址
    //private static final String redisHost = "192.168.106.101";//redis的ip地址
    private static final int redisPort = 6379;//端口
    private static final int maxTotal = 10;//连接池总数
    private static final int maxIdle = 5;//最大空闲连接数
    private static final int minIdle = 3;//最少空闲连接数
    private static final int redisTimeout = 2000;//连接redis超时时间,单位毫秒!默认2000,有布隆过滤器建议调大一点.
    private static final long maxWaitMillis = 10000;//申请资源最大等待时间,过期报错
    private static final String redisPassword = null;//密码
    //在borrow(引入)一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的,配置true会降低性能,配置false长期使用写入会不稳定.
    private static final boolean TestOnBorrow = true;
    private static final boolean TestOnReturn = false;//在向对象池中归还对象时是否检测对象有效(true : 是) , 配置true会降低性能；
    private static final boolean TestOnCreate = false;//在创建对象时检测对象是否有效(true : 是) , 配置true会降低性能;

    private static final JedisPool pool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMinIdle(maxIdle);
        config.setMaxIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnBorrow(TestOnBorrow);
        config.setTestOnReturn(TestOnReturn);
        config.setTestOnCreate(TestOnCreate);
        pool = new JedisPool(config, redisHost, redisPort, redisTimeout, redisPassword);
    }

    //获取redis连接
    public static Jedis getConnection() {
        return pool.getResource();
    }

    //获取布隆过滤器连接
    public static Client getBloomClient() {
        return new Client(pool);
    }

    //获取ReJSON连接
    public static JReJSON getJsonClient() {
        return new JReJSON(pool);
    }

    //获取ReCell连接
    public static JReCell getCellClient() {
        return new JReCell(pool);
    }

    //不用close的方法,该方法执行一后自动close
    public static void execute(CallWithJedis caller) {
        Jedis jedis = pool.getResource();
        try {
            caller.call(jedis);
        } catch (JedisConnectionException e) {
            caller.call(jedis);
        } finally {
            jedis.close();
        }
    }

    public static JedisPool getPool(){
        return pool;
    }
}
