package monitor.services;

import com.redislabs.modules.rejson.JReJSON;
import monitor.aomin.CpuLoad;
import monitor.aomin.DiskIoLoad;
import monitor.aomin.MemLoad;
import monitor.domin.DiskIoInfo;
import monitor.domin.MachineStaticInfo;
import monitor.domin.MemInfo;
import monitor.repositories.Machine;
import monitor.repositories.connecters.RedisPoolUtil4J;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MachinService {
    public Set<MachineStaticInfo> getMachines() {
        return Machine.getMachineStatics();
    }

    public String addMonitor(String host, String user, String passwd) {
        Boolean result = Machine.addMachine(user, passwd, host);
        if (result) {
            return "添加成功";
        } else {
            return "已经存在";
        }
    }

    public void updataStaticInfo(String host) {
        Machine.getMachine(host).updataStaticInfo();
    }
    public void startDynamicMonitor(String host) {
        Machine.getMachine(host).startDynamicMonitor();
    }
    public void stopDynamicMonitor(String host) {
        Machine.getMachine(host).stopDynamicMonitor();
    }

    public Boolean getDynamicFlag(String host) {
        return Machine.getMachine(host).getDynamicFlag();
    }
    public MachineStaticInfo getStaStaticInfo(String host) {
        return Machine.getMachine(host).getMachineStaticInfo();
    }

    public MemLoad getmem(String host) {
        JReJSON jsonClient = RedisPoolUtil4J.getJsonClient();
        return jsonClient.get("mem-" + host, MemLoad.class);
    }
    public DiskIoLoad getdiskio(String host) {
        JReJSON jsonClient = RedisPoolUtil4J.getJsonClient();
        return jsonClient.get("diskio-" + host, DiskIoLoad.class);
    }

    public CpuLoad getcpu(String host) {
        JReJSON jsonClient = RedisPoolUtil4J.getJsonClient();
        return jsonClient.get("cpu-" + host, CpuLoad.class);

    }

}
