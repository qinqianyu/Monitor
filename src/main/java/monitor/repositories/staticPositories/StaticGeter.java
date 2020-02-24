package monitor.repositories.staticPositories;

import monitor.domin.MachineStaticInfo;
import monitor.repositories.untils.JSchExecutor;

import java.util.List;

/**
 * 用来获取机器的静态信息
 */
public class StaticGeter {
    private JSchExecutor executor;

    public StaticGeter(JSchExecutor executor) {
        this.executor = executor;
    }

    public MachineStaticInfo getStaticInfo() {
        MachineStaticInfo staticInfo = MachineStaticInfo.builder().build();
        staticInfo.setHost(executor.getHost());
        List<String> strings = null;
        try {
            strings = executor.execCmd("hostname");
        } catch (Exception e) {
            e.printStackTrace();

        }
        staticInfo.setHostname(parseHostNeme(strings));
        try {
            int count = executor.execCmd("cat /proc/stat | grep cpu").size() -1;
            staticInfo.setThreadCount(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            strings = executor.execCmd("df -k |awk 'NR>1{print $2,$3}' | awk -v t=0 '{t+=$1}END{print t}'");
            staticInfo.setDiskcapacity(parsesetDisk(strings));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            strings = executor.execCmd("df -k |awk 'NR>1{print $2,$3}' | awk -v t=0 '{t+=$2}END{print t}'");
            staticInfo.setDiskused(parsesetDisk(strings));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staticInfo;
    }

    /**
     * 用来解析获取的HostName的结果
     * @param strings 获取的HostName的多行文本信息
     * @return 返回字符格式的HostName
     */
    private String parseHostNeme(List<String> strings) {
        if (strings == null || strings.size() < 1) {
            return "未获取到hostname";
        }
        return strings.get(0);
    }
    /**
     * 用来解析获取的硬盘信息的结果
     * @param strings 获取的磁盘信息的多行文本信息
     * @return 返回磁盘容量信息(单位是GB)
     */
    private Float parsesetDisk(List<String> strings) {
        if (strings == null || strings.size() < 1) {
            return 0f;
        }
        return Float.parseFloat(strings.get(0)) / 1048576;
    }
}
