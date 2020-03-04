package monitor.repositories.connecters;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.util.Pool;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class JReCell {
    private enum Command implements ProtocolCommand {
        throttle("cl.throttle");
        private final byte[] raw;

        Command(String alt) {
            raw = encode(alt);
        }

        public byte[] getRaw() {
            return raw;
        }
    }

    private Pool<Jedis> client;

    public JReCell(Pool<Jedis> jedis) {
        this.client = jedis;
    }

    public List<Long> throttle(String key, Integer max_burst, Integer count_per_period, Integer period) {
        List<Long> result;
        try (Jedis conn = client.getResource()) {
            conn.getClient()
                    .sendCommand(Command.throttle, encode(key),
                            encode(max_burst),
                            encode(count_per_period),
                            encode(period));
            result = conn.getClient().getIntegerMultiBulkReply();
        }
        return result;
    }

    public static byte[] encode(final String str) {
        try {
            if (str == null) {
                throw new JedisDataException("value sent to redis cannot be null");
            }
            return str.getBytes(Protocol.CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new JedisException(e);
        }
    }
    public static byte[] encode(Integer num) {
        try {
            if (num == null) {
                throw new JedisDataException("value sent to redis cannot be null");
            }
            return num.toString().getBytes(Protocol.CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new JedisException(e);
        }
    }
}
