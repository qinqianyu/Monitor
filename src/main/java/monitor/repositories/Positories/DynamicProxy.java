package monitor.repositories.Positories;

import monitor.repositories.executor.dynamicExe.CpuUsage;
import monitor.repositories.executor.dynamicExe.DiskIoUsage;
import monitor.repositories.executor.dynamicExe.MemUsage;
import monitor.repositories.executor.dynamicExe.NetUsage;
import monitor.repositories.connecters.JSchExecutor;

/**
 * @description: 动态代理
 * @author: jxk
 * @create: 2020-03-04 10:35
 **/
public class DynamicProxy {
    private JSchExecutor executor;
    private CpuUsage CpuUsage;
    private DiskIoUsage diskIoUsage;
    private MemUsage memUsage;
    private NetUsage netUsage;

    public DynamicProxy(JSchExecutor executor) {
        this.executor = executor;
    }

    public void start() {
        CpuUsage = new CpuUsage(executor);
        memUsage = new MemUsage(executor);
        diskIoUsage = new DiskIoUsage(executor);
        netUsage = new NetUsage(executor);
        CpuUsage.start();
        memUsage.start();
        diskIoUsage.start();
        netUsage.start();
    }

    public void stop() {
        CpuUsage.setFlag(false);
        memUsage.setFlag(false);
        diskIoUsage.setFlag(false);
        netUsage.setFlag(false);
    }

}
