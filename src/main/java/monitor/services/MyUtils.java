package monitor.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import monitor.domin.Instance;
import monitor.repositories.Machine;
import monitor.repositories.connecters.RedisPoolUtil4J;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @description:
 * @author: jxk
 * @create: 2020-03-05 08:45
 **/

@Component
public class MyUtils implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        JsonArray json= RedisPoolUtil4J.getJsonClient().get("instance", JsonArray.class);
        Gson gson = new GsonBuilder().create();
        List<Instance> lists = gson.fromJson(json, new TypeToken<List<Instance>>() {}.getType());
        for (Instance tmp:lists) {
            Machine.addMachine(tmp.getUser(),tmp.getPassword(),tmp.getHost());
        }
    }
}
