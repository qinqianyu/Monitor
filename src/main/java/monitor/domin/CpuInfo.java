package monitor.domin;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

/**
 * 用来保存cpu信息
 */
@Data
@Builder
public class CpuInfo {
    //累计空闲时间(单位：jiffies,1jiffies=0.01秒)
    private Long idleCpuTime;
    //累计总时间(单位：jiffies,1jiffies=0.01秒)
    private Long totalCpuTime;
    //采集时间(时间戳)
    private Long gatherTime;
    //线程数量(逻辑核心)
    private Integer threadCount;
    //核心的信息列表
    private ArrayList<CpuThreadInfo> cpuThreadInfos;
}
