package monitor.repositories.executor.dynamicExe;

import com.redislabs.modules.rejson.JReJSON;
import monitor.aomin.NetLoad;
import monitor.domin.NetInfo;
import monitor.repositories.connecters.JSchExecutor;
import monitor.repositories.connecters.RedisPoolUtil4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: 用来监控网络的线程
 * @author: jxk
 * @create: 2020-03-03 16:12
 **/
public class NetUsage extends Thread {
    private static Logger log = LoggerFactory.getLogger(NetUsage.class);
    private JSchExecutor executor;
    private static Boolean FLAG = true;

    public NetUsage(JSchExecutor executor) {
        this.executor = executor;
        // FLAG = true;
    }

    @Override
    public void run() {
        NetInfo beforNetInfo = null;
        NetInfo netInfo = null;
        NetLoad netLoad = NetLoad.builder().build();
        JReJSON jsonClient = RedisPoolUtil4J.getJsonClient();
        String host = executor.getHost();
        String netkey = "net-" + host;
        while (FLAG) {
            List<String> strings = null;
            try {
                strings = executor.execCmd("echo 'date '$[$(date +%s%N)/1000000];cat /proc/net/dev | sed -n '4,$p'");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (strings != null) {
                netInfo = parseNet(strings);
            } else {
                System.out.println("Net使用率：获取不到");
                return;
            }
            if (beforNetInfo != null) {
                float receiveUsage = (float) (netInfo.getReceive() - beforNetInfo.getReceive()) / (netInfo.getGatherTime() - beforNetInfo.getGatherTime()) * 1000 / 1024 / 1024;
                float transmitUsage = (float) (netInfo.getTransmit() - beforNetInfo.getTransmit()) / (netInfo.getGatherTime() - beforNetInfo.getGatherTime()) * 1000 / 1024 / 1024;
                String receiveUsageFormat = String.format("%.2f", receiveUsage);
                String transmitUsageFormat = String.format("%.2f", transmitUsage);
                netLoad.setReceive(receiveUsageFormat);
                netLoad.setTransmit(transmitUsageFormat);
                netLoad.setTransmit(new SimpleDateFormat("mm:ss").format(new Date(netInfo.getGatherTime())));
                jsonClient.set(netkey, netLoad);
            }
            beforNetInfo = netInfo;
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

    private NetInfo parseNet(List<String> strings) {
        NetInfo netInfo = NetInfo.builder().build();
        long receive = 0L;
        long transmit = 0L;
        for (String line : strings) {
            line = line.trim();
            if (line.startsWith("date")) {
                netInfo.setGatherTime(Long.parseLong(line.split("\\s+")[1]));
                continue;
            }
            String[] words = line.split("\\s+");
            receive += Long.parseLong(words[1]);
            transmit += Long.parseLong(words[9]);
        }
        netInfo.setReceive(receive);
        netInfo.setTransmit(transmit);
        return netInfo;
    }
}

