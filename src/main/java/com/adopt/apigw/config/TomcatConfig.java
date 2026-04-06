package com.adopt.apigw.config;

import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class TomcatConfig {

    @Value("${threads.max}")
    private int maxThreads;

    @Value("${min-spare}")
    private int minSpareThreads;

    @Value("${accept-count}")
    private int acceptCount;

    @Value("${resizeQueue}")
    private int resizeQueue;

    @Value("${name-prefix}")
    private String namePrefix;

    @Value("${tomcat.maxConnection}")
    private String maxConnection;
    @Value("${tomcat.connectionTimeout}")
    private String connectionTimeout;
    @Value("${tomcat.processorCache}")
    private String processorCache;

//    @Bean
//    public TomcatServletWebServerFactory tomcatFactory() {
//        return new TomcatServletWebServerFactory() {
//            @Override
//            protected void customizeConnector(org.apache.catalina.connector.Connector connector) {
//                super.customizeConnector(connector);
//                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//                protocol.setExecutor(new ThreadPoolExecutor(minSpareThreads, maxThreads, CommunicationConstant.THREAD_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()));
//                protocol.setMaxThreads(maxThreads);
//                protocol.setMinSpareThreads(minSpareThreads);
//                protocol.setAcceptCount(acceptCount);
//                protocol.setConnectionTimeout(10000);
//            }
//        };
//    }

//    @Bean
//    public TomcatServletWebServerFactory tomcatFactory() {
//        return new TomcatServletWebServerFactory() {
//            @Override
//            protected void customizeConnector(Connector connector) {
//                super.customizeConnector(connector);
//
//                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//
//                // Thread Pool Configuration
//                ThreadPoolExecutor executor = new ThreadPoolExecutor(
//                        minSpareThreads,
//                        maxThreads,
//                        CommunicationConstant.THREAD_ALIVE_TIME,
//                        TimeUnit.MILLISECONDS,
//                        new LinkedBlockingQueue<>(acceptCount),
//                        new ThreadPoolExecutor.CallerRunsPolicy()  // Prevents tasks from being lost
//                );
//
//                // Enable core thread timeout
//                executor.allowCoreThreadTimeOut(true);
//
//                protocol.setExecutor(executor);
//
//                // Basic Thread Configuration
//                protocol.setMaxThreads(maxThreads);
//                protocol.setMinSpareThreads(minSpareThreads);
//                protocol.setAcceptCount(acceptCount);
//
//                // Connection Management
//                protocol.setMaxConnections(Integer.parseInt(maxConnection));
//                protocol.setConnectionTimeout(Integer.parseInt(connectionTimeout));
//                protocol.setProcessorCache(Integer.parseInt(processorCache));
//
//                // Enable Keep-Alive
//                protocol.setKeepAliveTimeout(20000);
//                protocol.setMaxKeepAliveRequests(100);
//
//                // Socket Configuration
//                protocol.setTcpNoDelay(true);
//
//                // Enable Non-Blocking IO
//                protocol.setUseSendfile(true);
//            }
//        };
//    }

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory() {
            @Override
            protected void customizeConnector(Connector connector) {
                super.customizeConnector(connector);

                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();

                // Optimized queue size based on expected load
                int queueCapacity = acceptCount * 2;
                ThreadPoolExecutor executor = new ThreadPoolExecutor(
                        minSpareThreads,
                        maxThreads,
                        CommunicationConstant.THREAD_ALIVE_TIME,
                        TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<>(queueCapacity),
                        new ThreadFactory() {
                            private final AtomicInteger counter = new AtomicInteger();
                            @Override
                            public Thread newThread(Runnable r) {
                                Thread thread = new Thread(r);
                                thread.setName("http-nio-exec-" + counter.incrementAndGet());
                                thread.setDaemon(true);
                                thread.setPriority(Thread.NORM_PRIORITY);
                                return thread;
                            }
                        },
                        new ThreadPoolExecutor.CallerRunsPolicy()
                );

                executor.allowCoreThreadTimeOut(true);
                executor.prestartAllCoreThreads();

                protocol.setExecutor(executor);

                // Thread settings
                protocol.setMaxThreads(maxThreads);
                protocol.setMinSpareThreads(minSpareThreads);
                protocol.setAcceptCount(acceptCount);

                // Connection settings
                protocol.setMaxConnections(Integer.parseInt(maxConnection));
                protocol.setConnectionTimeout(Integer.parseInt(connectionTimeout));
                protocol.setProcessorCache(Integer.parseInt(processorCache));
                protocol.setAcceptorThreadCount(Runtime.getRuntime().availableProcessors());
                protocol.setPollerThreadCount(Runtime.getRuntime().availableProcessors() * 2);

                // Keep-alive optimizations
                protocol.setKeepAliveTimeout(30000);
                protocol.setMaxKeepAliveRequests(1000);
                protocol.setConnectionLinger(0); // Changed to 0 for faster connection release

                // Performance optimizations
                protocol.setTcpNoDelay(true);
                protocol.setUseSendfile(true);

                // Enable compression
                connector.setProperty("compression", "on");
                connector.setProperty("compressionMinSize", "1024");
                connector.setProperty("compressibleMimeType",
                        "text/html,text/xml,text/plain,application/json");
            }
        };

        // Configure HTTP/2 once
        factory.addConnectorCustomizers(connector -> {
            connector.addUpgradeProtocol(new Http2Protocol());
        });

        return factory;
    }

}
