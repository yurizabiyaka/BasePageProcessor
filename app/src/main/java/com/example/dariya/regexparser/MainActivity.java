package com.example.dariya.regexparser;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;

import com.tree.TreeNode;
import com.tree.TreeNodeIter;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.List;
import java.util.regex.*;

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
                ArrayList<IndexedRegexFindResult> result0 = ApplyRuleTree(getSet1(),(String)aMsg.obj);
                for(IndexedRegexFindResult res : result0)
                    maMainText.append("" + res.RegexIndex + " : " + res.Result+"\n");
//                TreeNode<String> treeRoot = getSet1();
//                String filtredText = DoRegEx((String)aMsg.obj);
//                maMainText.append("Succ Message: " + filtredText+"\n");
//                TreeNode<String> treeRoot = getSet1();
//                List<TreeNode<String>> children = treeRoot.children;
//                for (TreeNode<String> node : children) {
//                    String indent = "\n"+node.getLevel()+" : ";
//                    maMainText.append(indent + node.data);
//                }
//                maMainText.append("\n [3] :" + treeRoot.children.get(3).data);
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
        TreeNode<BaseParsingRule> root = new TreeNode<BaseParsingRule>(new RegexRule(0,"and.*?and"));
//        TreeNode<IndexedRegex> root = new TreeNode<IndexedRegex>(new IndexedRegex(0,"<(?:\"[^\"]*\"['\"]*|'[^']*'['\"]*|[^'\">])+>"));
        TreeNode<BaseParsingRule> next = root.addChild(new RegexRule(1,"the.*?\\."));
        return root;
    }

    // Regex indexed to be placed in a tree:
    public class IndexedRegexFindResult {
        public int RegexIndex;
        public String Result;
        IndexedRegexFindResult(int aRegexId, String aResult){
            RegexIndex = aRegexId;
            Result = aResult;
        }
    }

    ArrayList<IndexedRegexFindResult> ApplyRuleTree(TreeNode<BaseParsingRule> aRuleRoot, String aText){
        ArrayList<IndexedRegexFindResult> result = new ArrayList<IndexedRegexFindResult>();
        // apply self:
        List<List<String>> selfResults = aRuleRoot.data.Apply(aText);
        // add the next result for self:
        for(List<String> oneRes : selfResults){
            result.add(new IndexedRegexFindResult(aRuleRoot.data.getIndex(), oneRes.get(0)));
            // for all children add all results:
            for(TreeNode<BaseParsingRule> child : aRuleRoot.children) {
                // apply the child regex:
                ArrayList<IndexedRegexFindResult> downlinkRes = ApplyRuleTree(child, oneRes.get(0));
                // add results:
                for(IndexedRegexFindResult childRes : downlinkRes)
                    result.add(new IndexedRegexFindResult(childRes.RegexIndex,childRes.Result));
            }
        }
        return result;
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
//                    , "http://zakupki.gov.ru/epz/order/quicksearch/search_eis.html?searchString=%D1%83%D1%81%D0%BB%D1%83%D0%B3%D0%B8+%D1%81%D0%B2%D1%8F%D0%B7%D0%B8&morphology=on&pageNumber=1&sortDirection=false&recordsPerPage=_10&showLotsInfoHidden=false&fz44=on&fz223=on&af=on&ca=on&priceFrom=&priceTo=&currencyId=1&agencyTitle=&agencyCode=&agencyFz94id=&agencyFz223id=&agencyInn=&regions=&publishDateFrom=&publishDateTo=&sortBy=UPDATE_DATE&updateDateFrom=&updateDateTo="
                    , "https://httpbin.org/html"
                    , getOkHttpClient()
            );
            Thread tr = new Thread(infogetter);
            infogetter.setId(tr.getId());
            tr.start();
            FLoadTasks.add(new TaskFinishedFlag(infogetter.getId(),false));
        }
    }
}
