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

    public static final int PREDECESSORS = 5;
    public static final int CROSSREFS = 20;
    public static final int BOUND = 1_000_000;
    TarantoolCredentials config;
    TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> client;
    Map<String, String> env;

    String user;
    String password;
    String host;
    String port;

    Random randomGenerator;

    public void connectToTarantool() {
        randomGenerator = new Random();

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

    @Test
    public void put() {
        try {
            int index = randomGenerator.nextInt(BOUND);

            List<Object> predecessors = new ArrayList<>();
            for (int i = 0; i < PREDECESSORS; i++) {
                predecessors.add(new HashMap<Object, Object>() {{
                    put("mdmCode", randomGenerator.nextInt(BOUND));
                }});
            }

            List<Object> mdmRelWithSources = new ArrayList<>();
            for (int i = 0; i < CROSSREFS; i++) {
                String absCode = String.valueOf(randomGenerator.nextInt(BOUND));
                String externalId = String.valueOf(randomGenerator.nextInt(BOUND));
                String crossref = absCode + "~~~" + externalId;
                mdmRelWithSources.add(new HashMap<Object, Object>() {{
                    put("absCode", absCode);
                    put("externalId", externalId);
                    put("crossref", crossref);
                }});
            }

            Map<Object, Object> snapshot = new HashMap<Object, Object>() {{
                put("id", index);
                put("mdmCode", index);
                put("predecessors", predecessors);
                put("mdmRelWithSources", mdmRelWithSources);
                put("name", String.valueOf(index));
            }};
            HashMap<Object, Object> args = new HashMap<Object, Object>() {{
                put("etalonClientSnapshot", snapshot);
                put("etalonModificationDateTime", "2019-03-25T05:47:03.000Z");
                put("crossrefsModificationDateTime", "2019-03-25T05:47:03.000Z");
            }};
            List<?> res = client.eval(
                    "return repository.put('Etalon', ...)",
                    Collections.singletonList(args)).get();
            assertEquals(args, ((ArrayList<?>)res.get(0)).get(0));
        } catch (Exception e) {
            // checked exceptions
            e.printStackTrace();
            fail();
        }
    }

    // TODO: CHECK id == integer
    @Test
    public void findByMdmCode() {
        try {
            Integer index = randomGenerator.nextInt(BOUND);
            List<?> res = client.eval(
                    "return repository.find('Etalon', {{'mdmCode_index', '==', ...}})",
                    Collections.singletonList(index)).get();
            assertEquals(3, res.size());
        } catch (Exception e) {
            // checked exceptions
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void findByMdmCodeInPredecessors() {
        try {
            Integer index = randomGenerator.nextInt(BOUND);
            List<?> res = client.eval(
                    "return repository.find('Etalon', {{'mdmCode_predecessors_index', '==', ...}})",
                    Collections.singletonList(index)).get();
            assertEquals(3, res.size());
        } catch (Exception e) {
            // checked exceptions
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void findByCrossref() {
        try {
            String absCode = String.valueOf(randomGenerator.nextInt(BOUND));
            String externalId = String.valueOf(randomGenerator.nextInt(BOUND));
            String crossref = absCode + "~~~" + externalId;
            List<?> res = client.eval(
                    "return repository.find('Etalon', {{'crossref_index', '==', ...}})",
                    Collections.singletonList(crossref)).get();
            assertEquals(3, res.size());
        } catch (Exception e) {
            // checked exceptions
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void findByNameWithoutIndex() {
        try {
            String index = String.valueOf(randomGenerator.nextInt(BOUND));
            List<?> res = client.eval(
                    "return repository.find('Etalon', {{'etalonClientSnapshot.name', '==', ...}})",
                    Collections.singletonList(index)).get();
            assertEquals(3, res.size());
        } catch (Exception e) {
            // checked exceptions
            e.printStackTrace();
            fail();
        }
    }
}
