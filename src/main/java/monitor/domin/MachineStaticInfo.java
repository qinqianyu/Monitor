package monitor.domin;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 用来保存机器静态信息(更新不频繁信息)
 */
@Data
@Builder
public class MachineStaticInfo {
    //信息更新时间
    private Date updateTime;
    //主机名
    private String hostname;
    //主机ip
    private String host;
    //处理器线程数量
    private Integer threadCount;
    //硬盘容量(单位是GB)
    private Float diskcapacity;
    //硬盘已经使用容量(单位是GB)
    private Float diskused;
}
