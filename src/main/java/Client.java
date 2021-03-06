import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

public class Client {
    public static void main(String[] args) {
        final String env = System.getProperty("env");

        if (env == null || env.isEmpty())
            throw new IllegalArgumentException();

        ExecutorService exec = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 100; i++) {
            final String name = "ignite-" + i;

            exec.submit(new Runnable() {
                @Override public void run() {
                    try {
                        Random rnd = new Random();

                        while (true) {
                            IgniteConfiguration cfg = Ignition.loadSpringBean("ignite-" + env + ".xml", "ignite.cfg");

                            cfg.setGridName(name);
                            cfg.setClientMode(true);

                            try (Ignite ignite = Ignition.start(cfg)) {
                                IgniteCache<Key, Value> cache = ignite.cache("test-cache");

                                int idx = rnd.nextInt(5_000_000);

                                if (rnd.nextDouble() > 0.5)
                                    cache.put(new Key(idx), new Value(idx));
                                else {
                                    Value val = cache.get(new Key(idx));

                                    if (val != null && val.index() != idx)
                                        throw new AssertionError();
                                }
                            }
                        }
                    }
                    catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
        }
    }
}
