package com.example.dariya.regexparser;

import com.tree.TreeNode;

import java.util.Map;

/**
 * Created by Dariya on 24.02.2017.
 */

public abstract class BaseTreeResultTransformer {
    private RuleTreeResultsBuilder  FResult;
    BaseTreeResultTransformer(String aData, TreeNode<BaseParsingRule> aRuleTree){
        FResult = new RuleTreeResultsBuilder(aRuleTree, aData);
    }
    RuleTreeResultsBuilder getResult(){
        return FResult;
    }
    public abstract Map<String, Object> Transform();
}
