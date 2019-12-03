package com.nablarch.framework.multithread;

import com.nablarch.framework.multithread.component.Bbb;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.auto.AnnotationAutoContainerFactory;
import nablarch.fw.dicontainer.annotation.auto.DefaultComponentPredicate;
import nablarch.fw.dicontainer.annotation.auto.TraversalConfig;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class MultiThreadTester {
    /** ロガー **/
    private static final Logger LOGGER = LoggerManager.get(MultiThreadTester.class);

    public static void main(String[] notUsed) {
        final MultiThreadTester me = new MultiThreadTester();
        me.testMultiThread();
        //me.testSingleThread();
    }

    public void testMultiThread() {
        Container container = createContainer();
        Settings settings = new Settings();
        settings.max = 10_000_000;
        settings.threads = 10;
        settings.supplier = () -> new Runner(container, settings.getLoopCntPerThread());
        settings.execute();
    }

    public void testSingleThread() {
        Container container = createContainer();
        Settings settings = new Settings();
        settings.max = 10_000_000;
        settings.threads = 1;
        settings.supplier = () -> new Runner(container, settings.getLoopCntPerThread());
        settings.execute();
    }

    private static class Settings {
        /** 総実行回数 */
        int max;
        /** 何スレッドで実行するか */
        int threads;
        /** 実行する処理 */
        Supplier<Runnable> supplier;

        /**
         * 1スレッドあたりの処理件数。
         * @return 1スレッドあたりの処理件数
         */
        int getLoopCntPerThread() {
            return max / threads;
        }

        void execute() {
            ExecutorService pool = Executors.newFixedThreadPool(threads);

            long start = System.nanoTime();

            for (int i = 0; i < threads; i++) {
                pool.submit(supplier.get());
            }

            pool.shutdown();
            try {
                boolean terminated = pool.awaitTermination(30, TimeUnit.SECONDS);
                if (!terminated) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
            }
            long end = System.nanoTime();
            LOGGER.logInfo("time(ms) = " + ((end - start) / 1000 / 1000));
            LOGGER.logInfo("count = " + Runner.cnt.get());
        }

    }



    private static Container createContainer() {
        Iterable<TraversalConfig> traversalConfigs = Collections
                .singleton(new MultiThreadTraverseConfig());
        AnnotationContainerBuilder containerBuilder = AnnotationContainerBuilder
                .createDefault();
        AnnotationAutoContainerFactory factory = new AnnotationAutoContainerFactory(
                containerBuilder, traversalConfigs, new DefaultComponentPredicate());
        return factory.create();
    }


    private static class Runner implements Runnable {

        private final Container container;

        static final AtomicInteger cnt = new AtomicInteger();

        private final int loop;

        Runner(Container container, int loop) {
            this.container = container;
            this.loop = loop;
        }
        @Override
        public void run() {
            for (int i = 0; i < loop; i++) {
                Bbb component = container.getComponent(Bbb.class);
                cnt.getAndAdd(component.value());
                LOGGER.logDebug(component.bbb());
            }
        }
    }

    private static class NopRunner implements Runnable {

        @Override
        public void run() {

        }
    }
}
