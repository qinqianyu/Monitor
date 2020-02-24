package monitor.domin;

import lombok.Builder;
import lombok.Data;

/**
 * 用来保存cpu核心信息
 */
@Data
@Builder
public class CpuThreadInfo {
    //累计空闲时间(单位：jiffies,1jiffies=0.01秒)
    private Long idleCpuTime;
    //累计总时间(单位：jiffies,1jiffies=0.01秒)
    private Long totalCpuTime;
}
