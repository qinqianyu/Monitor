package monitor.aomin;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: jxk
 * @create: 2020-03-04 11:17
 **/
@Data
@Builder
public class CpuLoad {
    private String load;
    private List<String> threads;
    private String gatherTime;
}
