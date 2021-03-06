package perf;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;

/**
 * TODO
 */
public class IgniteTest {
    public static void main(String[] args) {
        Ignite ignite = Ignition.start();

        CacheConfiguration<String, byte[]> cfg = new CacheConfiguration<>("test");

        cfg.setCopyOnRead(false);

        IgniteCache<String, byte[]> cache = ignite.createCache(cfg);

        cache.put("key", new byte[1024 * 1024]);

        long total = 0;
        long cnt = 0;

        for (int i = 0; i < 1_000_000; i++) {
            long s = System.nanoTime();

            long l = cache.get("key").length;

            if (l != 1024 * 1024)
                throw new IllegalStateException();

            long t = System.nanoTime() - s;

            total += t;
            cnt++;
        }

        System.out.println("Average: " + (total / cnt));

        ignite.close();
    }
}
