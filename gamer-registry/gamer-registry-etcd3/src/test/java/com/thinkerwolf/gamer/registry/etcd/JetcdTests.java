package com.thinkerwolf.gamer.registry.etcd;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.registry.ChildEvent;
import com.thinkerwolf.gamer.registry.DataEvent;
import com.thinkerwolf.gamer.registry.INotifyListener;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchResponse;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JetcdTests {

    public static void main(String[] args) {
        testJetcd();
        testRegistry();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testJetcd() {
        URL url = URL.parse("jetcd://127.0.0.1:2379");
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(url.toHostPort());
        String backup = url.getString(URL.BACKUP);
        if (StringUtils.isNotBlank(backup)) {
            for (String bk : Constants.SEMICOLON_SPLIT_PATTERN.split(backup)) {
                if (StringUtils.isNotBlank(bk)) {
                    URL u = URL.parse(bk);
                    builder.append(";").append("http://").append(u.toHostPort());
                }
            }
        }
        Client client = Client.builder().endpoints(Constants.SEMICOLON_SPLIT_PATTERN.split(builder.toString())).build();
        KV kv = client.getKVClient();

        ByteSequence bs = JetcdUtil.toByteSeq("p1");
        WatchOption wop = WatchOption.newBuilder().withPrefix(bs).build();
        client.getWatchClient().watch(bs, wop, new Watch.Listener() {
            @Override
            public void onNext(WatchResponse response) {
                System.err.println("onNext : " + response);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.err.println("onCompleted");
            }
        });
    }

    public static void testRegistry() {
        ScheduledExecutorService exe = Executors.newScheduledThreadPool(1);

        URL url = URL.parse("jetcd://127.0.0.1:2379/p2");
        JetcdRegistry registry = new JetcdRegistry(url);

        Map<String, Object> parameters = new HashMap<>();
        URL u1 = URL.parse("http://127.0.0.1/p2/game");
        u1.setParameters(parameters);

        parameters.putIfAbsent(URL.NODE_NAME, "game_1001");
        registry.register(u1);
        registry.subscribe(u1, new INotifyListener() {
            @Override
            public void notifyDataChange(DataEvent event) throws Exception {
                System.out.println(event);
            }

            @Override
            public void notifyChildChange(ChildEvent event) throws Exception {

            }
        });

        URL u2 = URL.parse("http://127.0.0.1/p2/game");
        exe.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("lookup:" + registry.lookup(u2));
            }
        }, 3000, 3000, TimeUnit.MILLISECONDS);
    }


}
