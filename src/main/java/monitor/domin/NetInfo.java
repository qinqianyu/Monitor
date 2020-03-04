package monitor.domin;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

/**
 * @description: 用来保存网络io信息
 * @author: jxk
 * @create: 2020-03-03 16:37
 **/
@Data
@Builder
public class NetInfo {
    //累计接收(单位：字节)
    private Long Receive;
    //累计发送(单位：字节)
    private Long  Transmit;
    //采集时间(时间戳)
    private Long gatherTime;
}
