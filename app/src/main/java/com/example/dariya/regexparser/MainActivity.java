package com.example.dariya.regexparser;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements Handler.Callback , UiHanldeProvider, OkHttpClientProvider {
    // UI:
    private EditText maMainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maMainText = (EditText)findViewById(R.id.editText);
        maUiHandler = new Handler(this);
    }

    @Override
    public boolean handleMessage(Message aMsg){
        SetTaskFinished(aMsg.arg1);
        switch (aMsg.what) {
            case BaseRunnableResourseLoader.LoadResultConstants.LOAD_SUCCESS :
                RuleTreeResultsBuilder sesSel = new RuleTreeResultsBuilder(LoadRuleTree("Find1"),(String)aMsg.obj);
//                if(sesSel.N().S(0,0).S(2,0).S(4,0).Count(5)==0)
//                    maMainText.append("Empty res"+"\n");
//                String ss1=sesSel.N().S(0,0).S(2,0).S(3,0).R();
//                maMainText.append(ss1+"\n");
//                int rcount = sesSel.N().S(0,0).S(2,0).C(3);
//                maMainText.append(""+rcount+"\n");
                StringBuilder s2 = new StringBuilder();
                for(int i=0; i<sesSel.N().S(0,0).Count(2); i++ ) {
                    maMainText.append("ЗАКУПКА " + i + ":\n\n");
                    List<String> lstr3 = sesSel.N().S(0, 0).S(2, i).All(3);
                    s2.setLength(0);
                    for (String s : lstr3) {
                        s2.append(s);
                        s2.append("\n\n");
                    }
                    maMainText.append(s2.toString() + "\n");
                }
                break;
            case BaseRunnableResourseLoader.LoadResultConstants.LOAD_FAIL:
                maMainText.append("Fail Message: " + (String)aMsg.obj+"\n");
                break;
            case BaseRunnableResourseLoader.LoadResultConstants.LOAD_TIMEOUT:
                maMainText.append("Timeout loading task " + aMsg.arg1 +"\n");
                break;
        }
        if(LoadTasksFinished())
            maMainText.append("Done");
        return true;
    }


    public TreeNode<BaseParsingRule> LoadRuleTree(String aName){
        return getSet1();
    }

    public TreeNode<BaseParsingRule>  getSet1() {
        TreeNode<BaseParsingRule> root0 = new TreeNode<BaseParsingRule>(new RegexFindRule(0,"<html>.*</html>"));
        TreeNode<BaseParsingRule> root1 = root0.addChild(new RegexMatchRule(1,"<p\\sclass=\"allRecords\">.*?<strong>(.*?)</strong>.*?</p>"));
//        TreeNode<IndexedRegex> root = new TreeNode<IndexedRegex>(new IndexedRegex(0,"<(?:\"[^\"]*\"['\"]*|'[^']*'['\"]*|[^'\">])+>"));
        TreeNode<BaseParsingRule> next2 = root0.addChild(new RegexFindRule(2,"<div class=\"registerBox registerBoxBank margBtm20\">.*?(?=<div class=\"registerBox registerBoxBank margBtm20\">|<div class=\"margBtm50\">)"));
        TreeNode<BaseParsingRule> next3 = next2.addChild(new RegexMatchRule(3,"<a[^>]*?(?=href)href=\"(.*?)\""));
        return root0;
    }

    // Backgroung task message handler:
    private Handler maUiHandler;

    @Override
    public Handler getUiHandler() {
        return maUiHandler;
    }

    // OkHttp:
    OkHttpClient maClient = new OkHttpClient();

    @Override
    public OkHttpClient getOkHttpClient() {
        return maClient;
    }

    // Task control:

    private class TaskFinishedFlag {
        public long TaskId;
        public Boolean Finished;
        TaskFinishedFlag(long aTaskId, Boolean aIsFinished){
            TaskId = aTaskId;
            Finished = aIsFinished;
        }
    }

    private ArrayList<TaskFinishedFlag> FLoadTasks;

    Boolean LoadTasksFinished(){
        Boolean yes = true;
        for(TaskFinishedFlag flag : FLoadTasks) {
            yes = yes && flag.Finished;
            if (!yes)
                break;
        }
        return yes;
    }

    void SetTaskFinished(long aTaskId){
        for(TaskFinishedFlag flag : FLoadTasks)
            if(flag.TaskId == aTaskId)
                flag.Finished = true;
    }

    // Load Click:
    public void Load(View v) {
        int maxTasks = 1;
        FLoadTasks = new ArrayList<TaskFinishedFlag>(maxTasks);
        for(int i=0; i<maxTasks; i++){
            WebUrlLoader infogetter = new WebUrlLoader(
                    getUiHandler()
                    , "http://zakupki.gov.ru/epz/order/quicksearch/search_eis.html?searchString=%D1%83%D1%81%D0%BB%D1%83%D0%B3%D0%B8+%D1%81%D0%B2%D1%8F%D0%B7%D0%B8&morphology=on&pageNumber=1&sortDirection=false&recordsPerPage=_10&showLotsInfoHidden=false&fz44=on&fz223=on&af=on&ca=on&priceFrom=&priceTo=&currencyId=1&agencyTitle=&agencyCode=&agencyFz94id=&agencyFz223id=&agencyInn=&regions=&publishDateFrom=&publishDateTo=&sortBy=UPDATE_DATE&updateDateFrom=&updateDateTo="
//                    , "https://httpbin.org/html"
                    , getOkHttpClient()
            );
            Thread tr = new Thread(infogetter);
            infogetter.setId(tr.getId());
            tr.start();
            FLoadTasks.add(new TaskFinishedFlag(infogetter.getId(),false));
        }
    }
}
