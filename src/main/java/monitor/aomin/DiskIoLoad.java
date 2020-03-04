package monitor.aomin;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: jxk
 * @create: 2020-03-04 11:45
 **/
@Data
@Builder
public class DiskIoLoad {
    //io占用率(百分之)
    private String IoUsage ;

    //采集时间(时间戳)
    private String gatherTime;
}
