package monitor.repositories.DynamicPositories;


import monitor.domin.MemInfo;
import monitor.repositories.untils.JSchExecutor;
import monitor.repositories.untils.RedisPoolUtil4J;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用来监控内存的线程
 */
public class MemUsage extends Thread {
    private JSchExecutor executor;

    public MemUsage(JSchExecutor executor) {
        this.executor = executor;
    }

    private static Boolean FLAG = true;

    @Override
    public void run() {
        MemInfo memInfo;
        Jedis jedis = RedisPoolUtil4J.getConnection();
        String memkey = "mem-" + executor.getHost();
        while (FLAG) {
            List<String> strings = null;
            try {
                strings = executor.execCmd("echo 'date '$[$(date +%s%N)/1000000];cat  /proc/meminfo | head -n 2");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (strings != null) {
                memInfo = parseMem(strings);
            } else {
                System.out.println("mem使用率：获取不到");
                return;
            }
            if (memInfo != null) {
                jedis.set(memkey, memInfo.getMemTotal() + "-" + memInfo.getMemUsed());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        jedis.close();
    }

    public void setFlag(Boolean flag) {
        FLAG = flag;
    }

    private MemInfo parseMem(List<String> strings) {
        MemInfo memInfo = MemInfo.builder().build();
        for (String line : strings) {
            if (line.startsWith("date")) {
                line = line.trim();
                memInfo.setGatherTime(Long.parseLong(line.split(" ")[1]));
                continue;
            }
            if (line.startsWith("MemTotal")) {
                line = line.trim();
                String[] temp = line.split("\\s+");
                float memTotal = (float) Long.parseLong(temp[1]) / 1048576;
                memTotal = new BigDecimal(memTotal).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                memInfo.setMemTotal(memTotal);
                continue;
            }
            if (line.startsWith("MemFree")) {
                line = line.trim();
                String[] temp = line.split("\\s+");
                float memFree = (float) Long.parseLong(temp[1]) / 1048576;
                memFree = new BigDecimal(memFree).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                memInfo.setMemFree(memFree);
                break;
            }
        }
        return memInfo;
    }
}
