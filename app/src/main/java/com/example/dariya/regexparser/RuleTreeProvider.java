package com.example.dariya.regexparser;

import com.tree.TreeNode;

/**
 * Created by Dariya on 24.02.2017.
 */

public interface RuleTreeProvider {
//    public TreeNode<BaseParsingRule> LoadRuleTree(final String aTag);
    BaseTreeResultTransformer getTransformer(final String aTag);
}
