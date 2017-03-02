package com.example.dariya.regexparser;

import com.tree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Пользователь on 02.03.2017.
 */

public class Fz44PrintFormResultTransformer extends BaseTreeResultTransformer {
    public Fz44PrintFormResultTransformer(TreeNode<BaseParsingRule> aRuleTree) {
        super(aRuleTree);
    }

    @Override
    public Map<String, Object> Transform() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("titleSubtitle",new String(getResult().N().S(0,0).S(1,0).R()+" "+getResult().N().S(0,0).S(1,1).R()));
        Integer pgCount = new Integer(getResult().N().S(0,0).Count(2));
        result.put("paramgroupsCount",pgCount);
//        for(int i=0; i<pgCount; i++){
//            Map<String,String> map3 = new HashMap<>();
//            result.put()
//        }

//        result.put("pagestotal",getResult().N().S(0,0).S(1,0).S(12,0).R());
//        result.put("count",Integer.valueOf(getResult().N().S(0,0).Count(2)));
//        List<Map<String,String>> list = new ArrayList<>();
//        result.put("expenseList", list);
//        for(int i=0; i<getResult().N().S(0,0).Count(2); i++ ) {
//            Map<String,String> map3 = new HashMap<>();
//            map3.put("expenseType", getResult().N().S(0, 0).S(2, i).S(3,0).R());
//            map3.put("expenseStage", getResult().N().S(0, 0).S(2, i).S(3,1).R());
//            map3.put("expensePrice", getResult().N().S(0, 0).S(2, i).S(3,2).R());
//            map3.put("num", getResult().N().S(0, 0).S(2, i).S(4,0).R().trim());
//            map3.put("organization", getResult().N().S(0, 0).S(2, i).S(4,1).R().trim());
//            map3.put("subject", getResult().N().S(0, 0).S(2, i).S(4,2).R().trim());
//            map3.put("urlPrintForm", getResult().N().S(0, 0).S(2, i).S(5,0).R().trim());
//            map3.put("urlCommonInfo", getResult().N().S(0, 0).S(2, i).S(6,0).R().trim());
//            map3.put("urlDocuments", getResult().N().S(0, 0).S(2, i).S(7,0).R().trim());
//            list.add(map3);
//        }
        return result;
    }
}
