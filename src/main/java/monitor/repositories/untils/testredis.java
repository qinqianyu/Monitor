package monitor.repositories.untils;

import redis.clients.jedis.Jedis;

public class testredis {
    public static void main(String[] args) {
        Jedis jedis = RedisPoolUtil4J.getConnection();
        String key = jedis.set("key", "123");
        jedis.close();
    }
}
