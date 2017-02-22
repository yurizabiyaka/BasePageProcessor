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
                TreeNode<Object> data = new TreeNode<Object>(new String((String)aMsg.obj));
                TreeNode<Object> result0 = ApplyRuleTree(LoadRuleTree("Find1"),data);
                StringBuilder s1 = new StringBuilder();
                StringBuilder s2 = new StringBuilder();
                StringBuilder s3 = new StringBuilder();
                TreeNode<Object> result1 = SelectResult(0,0,s1,data);
                SelectResult(1,0,s2,result1);
                maMainText.append(s2.toString()+"\n");
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
        TreeNode<BaseParsingRule> root0 = new TreeNode<BaseParsingRule>(new RegexFindRule(0,"<html>.*</html>"));
        TreeNode<BaseParsingRule> root1 = root0.addChild(new RegexFindRule(1,"<p\\sclass=\"allRecords\">.*?<strong>(.*?)</strong>.*?</p>"));
//        TreeNode<IndexedRegex> root = new TreeNode<IndexedRegex>(new IndexedRegex(0,"<(?:\"[^\"]*\"['\"]*|'[^']*'['\"]*|[^'\">])+>"));
        TreeNode<BaseParsingRule> next2 = root0.addChild(new RegexFindRule(2,"<div class=\"registerBox registerBoxBank margBtm20\">.*?(?=<div class=\"registerBox registerBoxBank margBtm20\">|<div class=\"margBtm50\">)"));
        TreeNode<BaseParsingRule> next3 = next2.addChild(new RegexMatchRule(3,"<a[^>]*?(?=href)href=\"(.*?)\""));
        return root0;
    }

    // Regex indexed to be placed in a tree:
    // for a tree of
    //     ____RI0____
    //     |    |    |
    //   RI1   RI2   RI3
    //
    //  result would be:
    //
    //    ___________RI0________________
    //   |                 |            |
    //  String A          String B     String C
    //  |   |   |         |   |   |       etc.
    // RI1 RI2  RI3      RI1 RI2  RI3
    //  |   |   |  |      etc.
    // S1  S2   S31 S32    etc.

    TreeNode<Object> ApplyRuleTree(TreeNode<BaseParsingRule> aRuleRoot, TreeNode<Object> aDataRoot){
        TreeNode<Object> ruleRoot = aDataRoot.addChild(new Integer(aRuleRoot.data.getIndex()));
        // apply self:
        List<String> selfResult = aRuleRoot.data.Apply((String)aDataRoot.data);
        // add the next result for self:
        for(String oneSelfRes : selfResult){
            TreeNode<Object> child = ruleRoot.addChild(oneSelfRes);
            // go deeper:
            // for the child add as a child branches of the rule's children:
            for(TreeNode<BaseParsingRule> childRule : aRuleRoot.children) {
                // apply the child regex:
                TreeNode<Object> childRoot = ApplyRuleTree(childRule, child);
                // add results:
                child.addChild(childRoot);
            }
        }
        return ruleRoot;
    }

    // Selects in the rule branch of rule index aRuleInd (if the branch exists)
    // it's result under index aResultInd (if such a result is present),
    // and place it into aRes (otherwise aRes are given a null value),
    // and return in aResultRoot its result tree, if any exits (otherwise NULL)
    TreeNode<Object> SelectResult(int aRuleInd, int aResultInd, StringBuilder aRes, TreeNode<Object> aResultRoot){
        if(aResultRoot.children == null) {
            aRes = null; // in case a property that may cause an unexpected launch
            return null;
        }
        for(TreeNode<Object> child : aResultRoot.children)
            if(child.data instanceof Integer) // short-circuiting logical AND and unboxing
                if((Integer)(child.data) == aRuleInd)
                if(!child.children.isEmpty() && child.children.size() > aResultInd){
                    aRes.append((String)child.children.get(aResultInd).data); // that is String
                    // that is a tree :
                    return child.children.get(aResultInd).children.isEmpty() ? null : child.children.get(aResultInd).children.get(0);
                }
        aRes = null; // in case a property that may cause an unexpected launch
        return null;
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
