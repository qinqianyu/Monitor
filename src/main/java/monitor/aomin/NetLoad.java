package monitor.aomin;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: jxk
 * @create: 2020-03-04 10:20
 **/
@Data
@Builder
public class NetLoad {
    private String receive;
    private String transmit;
    private String gatherTime;
}
