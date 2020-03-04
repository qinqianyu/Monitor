package monitor.domin;

import lombok.Builder;
import lombok.Data;

/**
 * 用来保存机器内存信息
 */
@Data
@Builder
public class MemInfo {
    //总空间(单位是GB)
    private float MemTotal;
    //空闲空间(单位是GB)
    private float MemFree;
    //已经使用空间(单位是GB)
    //private float MemUesd;

    //采集时间(时间戳)
    private Long gatherTime;

    /**
     * @return 使用率(百分之)
     */
    public float getMemUsage() {
        return (1 - MemFree / MemTotal) * 100;
    }

    public float getMemUsed() {
        return MemTotal - MemFree;
    }
}