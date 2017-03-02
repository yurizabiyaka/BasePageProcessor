package com.example.dariya.regexparser;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.tree.TreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity { //implements Handler.Callback , UiHanldeProvider, OkHttpClientProvider, RuleTreeProvider {

    // UI:
    private EditText maMainText;

    // OkHttp:
    OkHttpClient maClient = new OkHttpClient();

    // Results:
    ExpenseSearchUrlBuilder FSearchUrlBuilder;
    private Integer FPagesTotal;
    int FPageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maMainText = (EditText)findViewById(R.id.editText);
    }

    private PageLoaderCallbackInterface callbackInterface=new PageLoaderCallbackInterface() {
        @Override
        public void onSuccess(Map<String, Object> resSet2) {
            maMainText.setText("");
            if(null == FPagesTotal) // on the first call, create FPagesTotal
                FPagesTotal = new Integer((String) resSet2.get("pagestotal"));
            maMainText.append("Total Results: "+resSet2.get("total")+"\n");
            maMainText.append("Current page: "+resSet2.get("pagenum")+"\n");
            maMainText.append("Out of pages: "+resSet2.get("pagestotal")+"\n");
            List<Map<String,String>> list = (List<Map<String,String>>)resSet2.get("expenseList");
            int i=0;
            for(Map<String,String> map3 : list){
                maMainText.append("\n\nЗАКУПКА [page "+FPageNum+"] "+i+"\n");
                StringBuilder sb = new StringBuilder();
                printMap(((List<Map<String,String>>) resSet2.get("expenseList")).get(i++),sb);
                maMainText.append(sb.toString());
            }
        }

        @Override
        public void onFail(String err) {
            maMainText.append("Fail Message: " + err+"\n");
        }

        @Override
        public void onTimeOut() {
            maMainText.append("Timeout loading task "+"\n");
        }
    };

    private PageLoaderCallbackInterface fz44CallBack=new PageLoaderCallbackInterface() {
        @Override
        public void onSuccess(Map<String, Object> res) {
            maMainText.setText("");
            maMainText.append("Title: "+res.get("titleSubtitle")+"\n");
            maMainText.append("Param groups: "+res.get("paramgroupsCount")+"\n");
            List<Map<String,Object>> list = (List<Map<String,Object>>)res.get("paramgroups");
            int i=0;
            for(Map<String,Object> map3 : list){
                StringBuilder sb = new StringBuilder();
                String x;
                x="groupName"; sb.append("\n" + x + " : " + map3.get(x));
                x="hasTable"; sb.append("\n" + x + " : " + map3.get(x));
                for(Pair<String,String> keyVal : (List<Pair<String,String>>)map3.get("keyValues"))
                    sb.append("\n" + keyVal.first + " : " + keyVal.second)  ;
                x="table";
                if(null != map3.get(x))
                    sb.append("\n" + x + " : " + map3.get(x));
                maMainText.append(sb.toString());
            }
        }

        @Override
        public void onFail(String err) {
            maMainText.append("Fail Message: " + err+"\n");
        }

        @Override
        public void onTimeOut() {
            maMainText.append("Timeout loading task "+"\n");
        }
    };

    public static void printMap(Map mp, StringBuilder sb) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            sb.append("\n" + pair.getKey() + " : " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

//    // Task control:
//
//    private class TaskFinishedFlag {
//        public long TaskId;
//        public Boolean Finished;
//        TaskFinishedFlag(long aTaskId, Boolean aIsFinished){
//            TaskId = aTaskId;
//            Finished = aIsFinished;
//        }
//    }
//
//    private ArrayList<TaskFinishedFlag> FLoadTasks;
//
//    Boolean LoadTasksFinished(){
//        Boolean yes = true;
//        for(TaskFinishedFlag flag : FLoadTasks) {
//            yes = yes && flag.Finished;
//            if (!yes)
//                break;
//        }
//        return yes;
//    }
//
//    void SetTaskFinished(long aTaskId){
//        for(TaskFinishedFlag flag : FLoadTasks)
//            if(flag.TaskId == aTaskId)
//                flag.Finished = true;
//    }

    // Load Click:
    public void Load(View v) {

//        int maxTasks = 1;
//        FLoadTasks = new ArrayList<TaskFinishedFlag>(maxTasks);
//        for(int i=0; i<maxTasks; i++){

            // Loading search results:
//        String tag = "SearchResults";
        String tag = "Fz44_Test_1";
        if("Fz44_Test_1".equals(tag)) {
            WebUrlLoader infogetter = new WebUrlLoader(
                    this
                    , fz44CallBack
                    , UrlstringBuilderFactory.INSTANCE.getUrlstringBuilder(tag).Build()
                    , maClient
                    , RuleTreeFactory.INSTANCE.getTransformer(tag) // can be NULL
            );
            Thread tr = new Thread(infogetter);
            infogetter.setId(tr.getId());
            tr.start();
        }
        if("SearchResults".equals(tag)){
            // when no builder exists, create and tune suitable one:
            if(null == FSearchUrlBuilder){
                // create via factory:
                FSearchUrlBuilder = (ExpenseSearchUrlBuilder)UrlstringBuilderFactory.INSTANCE.getUrlstringBuilder(tag); // can return NULL if the tag is unknown
                // initialize properly for the first time:
                if(null != FSearchUrlBuilder) {// get a default parameter set:
                    Map<String, String> params = ExpenseSearchUrlBuilder.getEmptyMap();
                    // put into the search string:
                    params.put("searchString", "услуги+связи");
                    FSearchUrlBuilder = FSearchUrlBuilder.setApp(this).InitUrl(params);
                }
            }
            if(null != FSearchUrlBuilder) {
                // pages total control:
                // TODO: 28.02.2017 Check what happens when requested page num exceeds pagestotal totally
                if(null != FPagesTotal && FPageNum < FPagesTotal) FPageNum++; else FPageNum = 1;
                FSearchUrlBuilder.setPageNum(FPageNum);
                WebUrlLoader infogetter = new WebUrlLoader(
                        this
                        , callbackInterface
                        , FSearchUrlBuilder.Build()
                        , maClient
                        , RuleTreeFactory.INSTANCE.getTransformer(tag) // can be NULL
                );
                Thread tr = new Thread(infogetter);
                infogetter.setId(tr.getId());
                tr.start();
//                FLoadTasks.add(new TaskFinishedFlag(infogetter.getId(), false));
            }
//        }
        }
    }
}
