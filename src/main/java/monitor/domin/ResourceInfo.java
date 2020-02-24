package monitor.domin;

import lombok.Builder;
import lombok.Data;

/**
 * 用来保存机器IO占用信息
 */
@Data
@Builder
public class ResourceInfo {
    //io占用率(百分之)
    private float ResourceUsage;

    //采集时间(时间戳)
    private Long gatherTime;
}