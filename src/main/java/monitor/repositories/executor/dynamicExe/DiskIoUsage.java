package monitor.repositories.executor.dynamicExe;

import com.redislabs.modules.rejson.JReJSON;
import io.rebloom.client.Client;
import monitor.aomin.DiskIoLoad;
import monitor.domin.DiskIoInfo;
import monitor.repositories.connecters.JSchExecutor;
import monitor.repositories.connecters.RedisPoolUtil4J;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用来监控硬盘IO占用的线程
 */
public class DiskIoUsage  extends Thread {
    private JSchExecutor executor;

    public DiskIoUsage (JSchExecutor executor) {
        this.executor = executor;
    }

    private static Boolean FLAG = true;

    @Override
    public void run() {
        DiskIoLoad diskIoLoad = DiskIoLoad.builder().build();
        JReJSON jsonClient = RedisPoolUtil4J.getJsonClient();
        String diskiokey = "diskio-" + executor.getHost();
        while (FLAG) {
            List<String> strings = null;
            try {
                strings = executor.execCmd("echo 'date '$[$(date +%s%N)/1000000];iostat -d -x | grep sda");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (strings != null) {
                DiskIoInfo diskIoInfo = parseDiskio(strings);
                if (diskIoInfo != null) {
                    diskIoLoad.setIoUsage(String.format("%.0f", diskIoInfo.getIoUsage()));
                    diskIoLoad.setGatherTime(new SimpleDateFormat("mm:ss").format(new Date(diskIoInfo.getGatherTime())));
                    jsonClient.set(diskiokey,diskIoLoad);
                }
            } else {
                System.out.println("io使用率：获取不到");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFlag(Boolean flag) {
        FLAG = flag;
    }

    private DiskIoInfo parseDiskio(List<String> strings) {
        DiskIoInfo diskIoInfo =  monitor.domin.DiskIoInfo.builder().build();
        for (String line : strings) {
            if (line.startsWith("date")) {
                line = line.trim();
                diskIoInfo.setGatherTime(Long.parseLong(line.split(" ")[1]));
                continue;
            }
            if (line.startsWith("sda")) {
                line = line.trim();
                String[] temp = line.split("\\s+");
                float util = Float.parseFloat(temp[temp.length - 1]);
                diskIoInfo.setIoUsage (util);
                break;
            }
        }
        return diskIoInfo;
    }
}