package monitor.services;

import monitor.domin.MachineStaticInfo;
import monitor.domin.MemInfo;
import monitor.domin.ResourceInfo;
import monitor.repositories.Machine;
import monitor.repositories.untils.RedisPoolUtil4J;
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

    public MemInfo getmem(String host) {
        Jedis jedis = RedisPoolUtil4J.getConnection();
        String mem = jedis.get("mem-" + host);
        String[] split = mem.split("-");
        jedis.close();
        return MemInfo.builder().MemUesd(Float.parseFloat(split[1])).MemTotal(Float.parseFloat(split[0])).build();
    }
    public ResourceInfo getresource(String host) {
        Jedis jedis = RedisPoolUtil4J.getConnection();
        String resource = jedis.get("resource-" + host);
        jedis.close();
        return ResourceInfo.builder().ResourceUsage(Float.parseFloat(resource)).build();
    }

    public List<String> getcpuaverage(String host) {
        Jedis jedis = RedisPoolUtil4J.getConnection();
        List<String> lrange = jedis.lrange("cpu-" + host, -60, -1);
        jedis.close();
        return lrange;
    }

    public List<List<Integer>> getcpucputhreads(String host) {
        Integer threadCount = Machine.getMachine(host).getMachineStaticInfo().getThreadCount();
        Jedis jedis = RedisPoolUtil4J.getConnection();
        ArrayList<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            List<String> lrange = jedis.lrange("cpu-" + host + "-" + i, -60, -1);
            result.add(parseList(lrange));
        }
        jedis.close();
        System.out.println(result);
        return result;
    }

    private List<Integer> parseList(List<String> lrange) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        lrange.forEach((x) -> arrayList.add((int) Math.round(Double.parseDouble(x))));
        return arrayList;
    }

}
