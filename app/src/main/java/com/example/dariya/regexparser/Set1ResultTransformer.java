package com.example.dariya.regexparser;

import com.tree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dariya on 24.02.2017.
 */

public class Set1ResultTransformer extends BaseTreeResultTransformer {
    Set1ResultTransformer(String aData, RuleTreeProvider aProvider){
        super(aData, aProvider.LoadRuleTree("Set1"));
    }

    @Override
    public Map<String, Object> Transform() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("total",getResult().N().S(0,0).S(1,0).R());
        result.put("count",Integer.valueOf(getResult().N().S(0,0).Count(2)));
        List<List<String>> listList = new ArrayList<>();
        result.put("ulrList", listList);
        for(int i=0; i<getResult().N().S(0,0).Count(2); i++ ) {
            List<String> lstr3 = getResult().N().S(0, 0).S(2, i).All(3);
            listList.add(lstr3);
        }
        return result;
    }
}
