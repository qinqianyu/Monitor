package monitor.repositories.connecters;

import redis.clients.jedis.Jedis;

@FunctionalInterface
public interface CallWithJedis {
    void call(Jedis jedis);
}