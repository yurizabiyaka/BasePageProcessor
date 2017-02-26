package com.example.dariya.regexparser;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.tree.TreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements Handler.Callback , UiHanldeProvider, OkHttpClientProvider, RuleTreeProvider {
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
                Set2ResultTransformer rtSet2 = new Set2ResultTransformer((String)aMsg.obj, this);
                Map<String, Object> resSet2 = rtSet2.Transform();
                maMainText.append("Total Results: "+resSet2.get("total")+"\n");
                maMainText.append("Current page: "+resSet2.get("pagenum")+"\n");
                maMainText.append("Out of pages: "+resSet2.get("pagestotal")+"\n");
                List<Map<String,String>> list = (List<Map<String,String>>)resSet2.get("expenseList");
                int i=0;
                for(Map<String,String> map3 : list){
                    maMainText.append("\n\nЗАКУПКА "+i+"\n");
                    StringBuilder sb = new StringBuilder();
                    printMap(((List<Map<String,String>>) resSet2.get("expenseList")).get(i++),sb);
                    maMainText.append(sb.toString());
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

    public static void printMap(Map mp, StringBuilder sb) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            sb.append("\n" + pair.getKey() + " : " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }


    public TreeNode<BaseParsingRule>  getSet1() {
        TreeNode<BaseParsingRule> root0 = new TreeNode<BaseParsingRule>(new RegexFindRule(0,"<html>.*</html>"));
        TreeNode<BaseParsingRule> root1 = root0.addChild(new RegexMatchRule(1,"<p\\sclass=\"allRecords\">.*?<strong>(.*?)</strong>.*?</p>"));
//        TreeNode<IndexedRegex> root = new TreeNode<IndexedRegex>(new IndexedRegex(0,"<(?:\"[^\"]*\"['\"]*|'[^']*'['\"]*|[^'\">])+>"));
        TreeNode<BaseParsingRule> next2 = root0.addChild(new RegexFindRule(2,"<div class=\"registerBox registerBoxBank margBtm20\">.*?(?=<div class=\"registerBox registerBoxBank margBtm20\">|<div class=\"margBtm50\">)"));
        TreeNode<BaseParsingRule> next3 = next2.addChild(new RegexMatchRule(3,"<a[^>]*?(?=href)href=\"(.*?)\""));
        return root0;
    }

    public TreeNode<BaseParsingRule>  getSet2() {
        TreeNode<BaseParsingRule> root0 = new TreeNode<BaseParsingRule>(new RegexFindRule(0,"<html>.*</html>"));
        TreeNode<BaseParsingRule> next1 = root0.addChild(new RegexMatchRule(1,"<div class=\"paginator greyBox\">(.*?)(?=<div class=\"registerBox registerBoxBank margBtm20\">)"));
        TreeNode<BaseParsingRule> next11 = next1.addChild(new RegexMatchRule(10,"<p\\sclass=\"allRecords\">.*?<strong>(.*?)</strong>.*?</p>"));
        TreeNode<BaseParsingRule> next12 = next1.addChild(new RegexMatchRule(11,"<li class=\"currentPage\">(.*?)</li>"));
        TreeNode<BaseParsingRule> next13 = next1.addChild(new RegexMatchRule(12,"<li class=\"rightArrow\">[^0-9]*(\\d+)"));

        TreeNode<BaseParsingRule> next2 = root0.addChild(new RegexFindRule(2,"<div class=\"registerBox registerBoxBank margBtm20\">.*?(?=<div class=\"registerBox registerBoxBank margBtm20\">|<div class=\"margBtm50\">)"));
        TreeNode<BaseParsingRule> next21 = next2.addChild(new RegexMatchRule(3,"<td class=\"tenderTd\">[A-Za-z<>/\\s]+<strong>\\s*([\\w[:blank:]:(),]+)\\s*</strong>.*?<span class=\"[\\w\\s]+\">([\\w\\x20:(),]+)/.*?<strong>\\s*([\\d[:blank:]]+)\\s*<span>"));
        TreeNode<BaseParsingRule> next22 = next2.addChild(new RegexMatchRule(4,"<td class=\"descriptTenderTd\">.*(№\\s\\d+).*Заказчик:\\s*<ul>\\s*<li>\\s*<a.*?>([\\w\\s\",.()№:-]+)</a>.*?<dd>(.*?)</dd>.*?</td>"));
        TreeNode<BaseParsingRule> next23 = next2.addChild(new RegexMatchRule(5,"<a[^>]*?(?=href)href=\"(.*?/[Pp]rint-?[Ff]orm/.*?)\""));
        TreeNode<BaseParsingRule> next24 = next2.addChild(new RegexMatchRule(6,"<a[^>]*?(?=href)href=\"([^\"]*?common-info.*?)\""));
        TreeNode<BaseParsingRule> next25 = next2.addChild(new RegexMatchRule(7,"<a[^>]*?(?=href)href=\"([^\"]*?documents.*?)\""));
        return root0;
    }

    // Rule Loader Interface:
    @Override
    public TreeNode<BaseParsingRule> LoadRuleTree(String aName){
        if("Set1".equals(aName))
            return getSet1();
        if("Set2".equals(aName))
            return getSet2();
        return new TreeNode<BaseParsingRule>(new RegexFindRule(0,"<html>.*</html>"));
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
