package com.example.dariya.regexparser;

import android.os.Handler;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Dariya on 18.02.2017.
 */

class WebUrlLoader extends BaseRunnableResourseLoader {
    String FUri;
    String FAnswer;
    private OkHttpClient FClient;
    private Handler FHandle;

    WebUrlLoader(Handler aUiHandle, String aUri, OkHttpClient aClient){
        super(aUiHandle);
        FUri = aUri;
        FClient = aClient;
    }

    @Override
    public void run(){
        Request request = new Request.Builder()
                .url(FUri)
                .build();
        try{
            Response response = FClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                FAnswer = "Fail[" + "]: " + response;
                response.close();
                this.SendFailMessage(FAnswer);
            }
            else {
                FAnswer = response.body().string(); // this closes the stream
                this.SendSuccesResult(FAnswer);
            }
        } catch(IOException ex){
            FAnswer = "Fail["+"]: "+ex.toString();
            this.SendFailMessage(FAnswer);
        }
    }
}

