package monitor.repositories;

import com.jcraft.jsch.JSchException;
import lombok.Data;
import monitor.domin.MachineStaticInfo;
import monitor.repositories.DynamicPositories.CpuLoad;
import monitor.repositories.staticPositories.StaticGeter;
import monitor.repositories.untils.JSchExecutor;
import monitor.repositories.DynamicPositories.MemUsage;
import monitor.repositories.DynamicPositories.ResourceUsage;

import java.util.*;

/**
 * 用来控制远程机器的机器控制类
 */
@Data
public class Machine {
    //动态监控的开关(动态监控每秒进行一次，包含更新频率较高的信息，如cpu，内存，磁盘io负载)
    private Boolean dynamicFlag = false;
    //ip地址
    private String host;
    //用户名
    private String user;
    //密码
    private String passwd;

    //获取动态信息的远程执行器
    private JSchExecutor executor;
    //获取静态信息的远程执行器
    private JSchExecutor staticExecutor;
    //获取cpu的线程
    private CpuLoad cpuLoad;
    //cpu的线程数量(逻辑核心数量)
    private Integer threadCount;
    //获取内存信息的线程
    private MemUsage memUsage;
    //获取磁盘io的线程
    private ResourceUsage resourceUsage;
    //机器的静态信息
    private MachineStaticInfo machineStaticInfo;
    //当前监控机器的集合
    private static Map<String, Machine> machineList = new HashMap<>();

    /**
     *
     * @param ip 机器ip
     * @return 对应ip的机器控制类
     */
    public static Machine getMachine(String ip) {
        return machineList.getOrDefault(ip, null);
    }

    /**
     *
     * @return 所有被监控机器的ip集合
     */
    public static Set<String> getMachineHosts() {
        return machineList.keySet();
    }

    /**
     *
     * @return 所有被监控机器的静态信息集合
     */
    public static Set<MachineStaticInfo> getMachineStatics() {
        Collection<Machine> values = machineList.values();
        HashSet<MachineStaticInfo> machineStaticInfos = new HashSet<>();
        for (Machine tmp : values) {
            machineStaticInfos.add(tmp.getMachineStaticInfo());
        }
        return machineStaticInfos;
    }

    /**
     * 用来添加一台需要被监控的机器
     * @param user  用户名
     * @param passwd    密码
     * @param host  ip
     * @return 添加成功与否的结果
     */
    public static Boolean addMachine(String user, String passwd, String host) {
        if (machineList.containsKey(host)) {
            return false;
        } else {
            Machine machine = new Machine(user, passwd, host);
            machine.updataStaticInfo();
            machineList.put(host, machine);
            return true;
        }
    }

    /**
     * 更新静态信息
     */
    public void updataStaticInfo() {
        try {
            staticExecutor.connect();
            StaticGeter staticGeter = new StaticGeter(staticExecutor);
            this.machineStaticInfo = staticGeter.getStaticInfo();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        staticExecutor.disconnect();

    }

    /**
     * 开启动态监控
     */
    public void startDynamicMonitor() {
        if (dynamicFlag)
                return;
        try {
            executor.connect();
            cpuLoad = new CpuLoad(executor);
            memUsage = new MemUsage(executor);
            resourceUsage = new ResourceUsage(executor);
            cpuLoad.start();
            memUsage.start();
            resourceUsage.start();
            this.dynamicFlag = true;
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止动态监控
     */
    public void stopDynamicMonitor() {
        cpuLoad.setFlag(false);
        memUsage.setFlag(false);
        resourceUsage.setFlag(false);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.disconnect();
        this.dynamicFlag = false;
    }

    /**
     * 被隐藏的构造函数
     */
    private Machine(String user, String passwd, String host) {
        this.executor = new JSchExecutor(user, passwd, host);
        this.staticExecutor = new JSchExecutor(user, passwd, host);
        this.host = host;
        this.user = user;
        this.passwd = passwd;
    }

    private Machine() {
    }
    private void setDynamicFlag(Boolean flag) {
    }


}
