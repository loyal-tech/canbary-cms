package com.adopt.apigw.modules.Alert.singeltonThreadPool;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SMSCommunicationSingeltonThreadPool {

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void addThreadToPool(Runnable threadObj) {
        executorService.execute(threadObj);
    }

    public void shutDownThreadPool() {
        executorService.shutdown();
    }

    public String getActiveThreads() {
        return "Thread pool size :- " + ((ThreadPoolExecutor) executorService).getActiveCount();
    }
}
