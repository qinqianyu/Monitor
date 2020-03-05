package monitor.domin;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: jxk
 * @create: 2020-03-05 08:55
 **/
@Data
@Builder
public class Instance {
    private String host;
    private String user;
    private String password;
}
