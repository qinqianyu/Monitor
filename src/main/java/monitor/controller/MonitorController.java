package monitor.controller;

import monitor.domin.MachineStaticInfo;
import monitor.domin.MemInfo;
import monitor.domin.ResourceInfo;
import monitor.services.MachinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private MachinService machinService;

    /**
     * @return 所有被监控机器的静态信息集合
     */
    @GetMapping("/machines")
    public Set<MachineStaticInfo> getMachines() {
        return machinService.getMachines();
    }

    /**
     * 用来添加一台需要被监控的机器
     *
     * @param user   用户名
     * @param passwd 密码
     * @param host   ip
     * @return 添加成功与否的结果
     */
    @GetMapping("/addMonitor")
    public String addMonitor(@RequestParam(value = "ip", required = true) String host, String user, String passwd) {
        return machinService.addMonitor(host, user, passwd);
    }

    /**
     * @param host 机器的ip
     * @return 动态监控的开关状态(动态监控每秒进行一次 ， 包含更新频率较高的信息 ， 如cpu ， 内存 ， 磁盘io负载)
     */
    @GetMapping("/dynamicflag")
    public Boolean dynamicflag(@RequestParam(value = "ip", required = true) String host) {
        return machinService.getDynamicFlag(host);
    }

    /**
     * 更新静态信息
     * @param host 机器的ip
     */
    @GetMapping("/updataStaticInfo")
    public void updataStaticInfo(@RequestParam(value = "ip", required = true) String host) {
        machinService.updataStaticInfo(host);
    }

    /**
     * 开启动态监控
     * @param host 机器的ip
     */
    @GetMapping("/startDynamicMonitor")
    public void startDynamicMonitor(@RequestParam(value = "ip", required = true) String host) {
        machinService.startDynamicMonitor(host);
    }

    /**
     * 开启动态监控
     * @param host  机器的ip
     */
    @GetMapping("/stopDynamicMonitor")
    public void stopDynamicMonitor(@RequestParam(value = "ip", required = true) String host) {
        machinService.stopDynamicMonitor(host);
    }

    /**
     *
     * @param host  机器的ip
     * @return  机器的静态信息
     */
    @GetMapping("/taStaticInfo")
    public MachineStaticInfo getStaStaticInfo(@RequestParam(value = "ip", required = true) String host) {
        return machinService.getStaStaticInfo(host);
    }

    /**
     *
     * @param host  机器的ip
     * @return  机器内存信息
     */
    @GetMapping("/meminfo")
    public MemInfo getmem(@RequestParam(value = "ip", required = true) String host) {
        return machinService.getmem(host);
    }

    /**
     *
     * @param host  机器的ip
     * @return  机器IO占用信息
     */
    @GetMapping("/resourceinfo")
    public ResourceInfo getresource(@RequestParam(value = "ip", required = true) String host) {
        return machinService.getresource(host);
    }

    /**
     *
     * @param host  机器的ip
     * @return  cpu60秒内的综合负载列表
     */
    @GetMapping("/cpuaverage")
    public List<String> getcpuaverage(@RequestParam(value = "ip", required = true) String host) {
        return machinService.getcpuaverage(host);
    }
    /**
     *
     * @param host  机器的ip
     * @return  cpu60秒内的各个核心负载列表
     */
    @GetMapping("/cputhreads")
    public List<List<Integer>> getcpucputhreads(@RequestParam(value = "ip", required = true) String host) {
        return machinService.getcpucputhreads(host);
    }


}
