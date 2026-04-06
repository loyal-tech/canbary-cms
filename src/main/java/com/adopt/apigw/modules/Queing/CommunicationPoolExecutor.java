package com.adopt.apigw.modules.Queing;

import org.springframework.stereotype.Component;

import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class CommunicationPoolExecutor extends ThreadPoolExecutor {

    public static BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();

    public CommunicationPoolExecutor() {
        super(CommunicationConstant.CORE_POOL_SIZE, CommunicationConstant.MAX_POOL_SIZE, CommunicationConstant.THREAD_ALIVE_TIME, TimeUnit.MILLISECONDS, blockingQueue);
    }
}