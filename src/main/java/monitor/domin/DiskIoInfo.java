package monitor.domin;


import lombok.Data;
import lombok.Builder;
/**
 * 用来保存机器IO占用信息
 */

@Data
@Builder
public class DiskIoInfo {
    //io占用率(百分之)
    private Float IoUsage ;

    //采集时间(时间戳)
    private Long gatherTime;
}