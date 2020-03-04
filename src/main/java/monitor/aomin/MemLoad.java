package monitor.aomin;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: jxk
 * @create: 2020-03-04 12:07
 **/
@Data
@Builder
public class MemLoad {
    //总空间(单位是GB)
    private String MemTotal;

    //已经使用空间(单位是GB)
    private String MemUesd;

    //采集时间(时间戳)
    private String gatherTime;
}
