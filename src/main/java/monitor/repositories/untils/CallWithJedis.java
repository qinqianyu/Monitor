package monitor.repositories.untils;

import redis.clients.jedis.Jedis;

@FunctionalInterface
public interface CallWithJedis {
    void call(Jedis jedis);
}