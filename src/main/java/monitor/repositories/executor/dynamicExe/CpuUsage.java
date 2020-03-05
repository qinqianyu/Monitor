package monitor.repositories.executor.dynamicExe;

import com.redislabs.modules.rejson.JReJSON;
import lombok.extern.slf4j.Slf4j;
import monitor.aomin.CpuLoad;
import monitor.domin.CpuInfo;
import monitor.domin.CpuThreadInfo;
import monitor.repositories.connecters.JSchExecutor;
import monitor.repositories.connecters.RedisPoolUtil4J;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用来监控cpu的线程
 */
@Slf4j
public class CpuUsage extends Thread {
    private JSchExecutor executor;
    private static Boolean FLAG = true;

    public CpuUsage(JSchExecutor executor) {
        this.executor = executor;
        // FLAG = true;
    }

    @Override
    public void run() {
        CpuInfo beforCpuInfo = null;
        CpuInfo cpuInfo;
        CpuLoad cpuLoad = CpuLoad.builder().build();
        ArrayList<CpuThreadInfo> beforCpuThreadInfos;
        ArrayList<CpuThreadInfo> cpuThreadInfos = null;
        JReJSON jsonClient = RedisPoolUtil4J.getJsonClient();
        String host = executor.getHost();
        String cpukey = "cpu-" + host;
        while (FLAG) {
            List<String> strings = null;
            try {
                strings = executor.execCmd("echo 'date '$[$(date +%s%N)/1000000];cat /proc/stat | grep cpu");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (strings != null) {
                cpuInfo = parseCpuTime(strings);
            } else {
                log.info("cpu使用率：获取不到");
                return;
            }
            if (beforCpuInfo != null) {
                double cpuUsage = 100 * (1 - (float) (cpuInfo.getIdleCpuTime() - beforCpuInfo.getIdleCpuTime()) / (float) (cpuInfo.getTotalCpuTime() - beforCpuInfo.getTotalCpuTime()));
                cpuLoad.setLoad(String.format("%.0f", cpuUsage));
                cpuLoad.setGatherTime(new SimpleDateFormat("mm:ss").format(new Date(cpuInfo.getGatherTime())));
                cpuLoad.setThreads((new ArrayList<String>()));
                cpuThreadInfos = cpuInfo.getCpuThreadInfos();
                beforCpuThreadInfos = beforCpuInfo.getCpuThreadInfos();
                int index = 0;
                for (CpuThreadInfo tmp : cpuThreadInfos) {
                    double threadUsage = 100 * (1 - (float) (tmp.getIdleCpuTime() - beforCpuThreadInfos.get(index).getIdleCpuTime()) / (float) (tmp.getTotalCpuTime() - beforCpuThreadInfos.get(index).getTotalCpuTime()));
                    cpuLoad.getThreads().add(String.format("%.0f", threadUsage));
                    index++;
                }
                jsonClient.set(cpukey, cpuLoad);
                log.info(cpuInfo.toString());
            }
            beforCpuInfo = cpuInfo;
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

    private CpuInfo parseCpuTime(List<String> strings) {
        CpuInfo cpuInfo = CpuInfo.builder().build();
        ArrayList<CpuThreadInfo> cpuThreadInfos = new ArrayList<>();
        for (String line : strings) {
            if (line.startsWith("date")) {
                line = line.trim();
                cpuInfo.setGatherTime(Long.parseLong(line.split(" ")[1]));
                continue;
            }
            if (line.startsWith("cpu ")) {
                line = line.trim();
                String[] temp = line.split("\\s+");
                cpuInfo.setIdleCpuTime(Long.parseLong(temp[4]));
                long totalCpuTime = 0;
                for (String s : temp) {
                    if (!s.startsWith("cpu")) {
                        totalCpuTime += Long.parseLong(s);
                    }
                }
                cpuInfo.setTotalCpuTime(totalCpuTime);
                continue;
            }
            if (line.startsWith("cpu")) {
                CpuThreadInfo threadInfo = CpuThreadInfo.builder().build();
                line = line.trim();
                String[] temp = line.split("\\s+");
                threadInfo.setIdleCpuTime(Long.parseLong(temp[4]));
                long totalCpuTime = 0;
                for (String s : temp) {
                    if (!s.startsWith("cpu")) {
                        totalCpuTime += Long.parseLong(s);
                    }
                }
                threadInfo.setTotalCpuTime(totalCpuTime);
                cpuThreadInfos.add(threadInfo);
            }
        }
        cpuInfo.setCpuThreadInfos(cpuThreadInfos);
        cpuInfo.setThreadCount(cpuThreadInfos.size());
        return cpuInfo;
    }
}
