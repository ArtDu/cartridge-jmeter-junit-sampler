package io.tarantool.jmeter;

import io.tarantool.driver.api.TarantoolClient;
import io.tarantool.driver.api.TarantoolResult;
import io.tarantool.driver.api.tuple.TarantoolTuple;
import io.tarantool.driver.auth.SimpleTarantoolCredentials;
import io.tarantool.driver.auth.TarantoolCredentials;
import io.tarantool.driver.core.ClusterTarantoolTupleClient;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

public class TarantoolTest {

    TarantoolCredentials config;
    TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> client;
    Map<String, String> env;

    String user;
    String password;
    String host;
    String port;


    public void connectToTarantool() {
        env = System.getenv();
        user = env.getOrDefault("TNT_USER", "admin");
        password = env.getOrDefault("PASSWORD", "secret-cluster-cookie");
        host = env.getOrDefault("HOST", "localhost");
        port = env.getOrDefault("PORT", "3301");

        System.out.println("Connect to tarantool");
        config = new SimpleTarantoolCredentials(user, password);
        client = new ClusterTarantoolTupleClient(config, host, Integer.parseInt(port));
    }

    public TarantoolTest() {
        System.out.println("Empty constuctor");

        connectToTarantool();
    }

    @Test
    public void echo() {
        try {
            List<?> res = client.eval("return 1, require'log'.info('Hello from JAVA')").get();
            assertEquals(1, res.get(0));
        } catch (Exception e) {
            // checked exceptions
            e.printStackTrace();
            fail();
        }
    }
}
