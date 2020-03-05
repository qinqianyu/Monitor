package monitor.repositories.executor.dynamicExe;

import com.jcraft.jsch.JSchException;
import monitor.repositories.connecters.JSchExecutor;
import org.junit.Test;

public class myTest {
    @Test
    public void main() throws JSchException, InterruptedException {
        JSchExecutor root = new JSchExecutor("root", "123456", "192.168.106.101");
        root.connect();
        CpuUsage cpuUsage = new CpuUsage(root);
        cpuUsage.start();
        Thread.sleep(50000);
        cpuUsage.setFlag(false);
        Thread.sleep(2000);
        root.disconnect();
    }
}
