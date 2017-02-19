package com.example.dariya.regexparser;

import android.os.Handler;
import android.os.Message;

import java.util.Map;

/**
 * Created by Dariya on 18.02.2017.
 */

public abstract class BaseRunnableResourseLoader implements Runnable{

    public abstract class LoadResultConstants {
        public static final int LOAD_SUCCESS = 0;
        public static final int LOAD_FAIL = 1;
        public static final int LOAD_TIMEOUT = 2;
    }

    Handler FHandle; // we send messages to it
    long FTaskId;

    public long getId(){
        return FTaskId;
    }
    public void setId(long aTaskId){
        FTaskId = aTaskId;
    }
    public BaseRunnableResourseLoader(Handler aHandle){
        FHandle = aHandle;
    }
    public BaseRunnableResourseLoader(Handler aHandle, long aTaskId){
        FHandle = aHandle;
        setId(aTaskId);
    }
    public void SendSuccesResult(String aResult){
        Message msgForHandler = FHandle.obtainMessage(BaseRunnableResourseLoader.LoadResultConstants.LOAD_SUCCESS, (int)this.getId(), 0, aResult);
        FHandle.sendMessage(msgForHandler);
    }
    public void SendFailMessage(String aFailMessage){
        Message msgForHandler = FHandle.obtainMessage(BaseRunnableResourseLoader.LoadResultConstants.LOAD_FAIL, (int)this.getId(), 0, aFailMessage);
        FHandle.sendMessage(msgForHandler);
    }
    public void SendTimeoutEvent(){
        Message msgForHandler = FHandle.obtainMessage(BaseRunnableResourseLoader.LoadResultConstants.LOAD_TIMEOUT, (int)this.getId(), 0);
        FHandle.sendMessage(msgForHandler);
    }
}
