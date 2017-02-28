package com.example.dariya.regexparser;

import com.tree.TreeNode;

/**
 * Created by Пользователь on 28.02.2017.
 */

public enum RuleTreeFactory  {
    INSTANCE;

    private RuleTreeFactory(){}

    public BaseTreeResultTransformer getTransformer(final String aTag){
        if("SearchResults".equals(aTag))
            return new SearchResultsTransformer(LoadRuleTree(aTag));
        else
            return new Set1ResultTransformer(LoadRuleTree("Set1"));
    }

    //    @Override
    public TreeNode<BaseParsingRule> LoadRuleTree(final String aName){
        if("Set1".equals(aName))
            return getSet1();
        if("SearchResults".equals(aName))
            return getSearchResultsTree();
        return new TreeNode<BaseParsingRule>(new RegexFindRule(0,"<html>.*</html>"));
    }

    public TreeNode<BaseParsingRule>  getSet1() {
        TreeNode<BaseParsingRule> root0 = new TreeNode<BaseParsingRule>(new RegexFindRule(0,"<html>.*</html>"));
        TreeNode<BaseParsingRule> root1 = root0.addChild(new RegexMatchRule(1,"<p\\sclass=\"allRecords\">.*?<strong>(.*?)</strong>.*?</p>"));
//        TreeNode<IndexedRegex> root = new TreeNode<IndexedRegex>(new IndexedRegex(0,"<(?:\"[^\"]*\"['\"]*|'[^']*'['\"]*|[^'\">])+>"));
        TreeNode<BaseParsingRule> next2 = root0.addChild(new RegexFindRule(2,"<div class=\"registerBox registerBoxBank margBtm20\">.*?(?=<div class=\"registerBox registerBoxBank margBtm20\">|<div class=\"margBtm50\">)"));
        TreeNode<BaseParsingRule> next3 = next2.addChild(new RegexMatchRule(3,"<a[^>]*?(?=href)href=\"(.*?)\""));
        return root0;
    }

    public TreeNode<BaseParsingRule>  getSearchResultsTree() {
        TreeNode<BaseParsingRule> root0 = new TreeNode<BaseParsingRule>(new RegexFindRule(0,"<html>.*</html>"));
        TreeNode<BaseParsingRule> next1 = root0.addChild(new RegexMatchRule(1,"<div class=\"paginator greyBox\">(.*?)(?=<div class=\"registerBox registerBoxBank margBtm20\">)"));
        TreeNode<BaseParsingRule> next11 = next1.addChild(new RegexMatchRule(10,"<p\\sclass=\"allRecords\">.*?<strong>(.*?)</strong>.*?</p>"));
        TreeNode<BaseParsingRule> next12 = next1.addChild(new RegexMatchRule(11,"<li class=\"currentPage\">(.*?)</li>"));
        TreeNode<BaseParsingRule> next13 = next1.addChild(new RegexMatchRule(12,"<li class=\"rightArrow\">[^0-9]*(\\d+)"));

        TreeNode<BaseParsingRule> next2 = root0.addChild(new RegexFindRule(2,"<div class=\"registerBox registerBoxBank margBtm20\">.*?(?=<div class=\"registerBox registerBoxBank margBtm20\">|<div class=\"margBtm50\">)"));
        TreeNode<BaseParsingRule> next21 = next2.addChild(new RegexMatchRule(3,"<td class=\"tenderTd\">[A-Za-z<>/\\s]+<strong>\\s*([\\w[:blank:]:(),]+)\\s*</strong>.*?<span class=\"[\\w\\s]+\">([\\w\\x20:(),]+)/.*?<strong>\\s*([\\d[:blank:]]+)\\s*<span>"));
            TreeNode<BaseParsingRule> next22 = next2.addChild(new RegexMatchRule(4,"<td class=\"descriptTenderTd\">.*(№\\s\\d+).*Заказчик:\\s*<ul>\\s*<li>\\s*<a.*?>([-\\w\\s\",.()№:A-Za-z=<>/]+)</a>.*?<dd>(.*?)</dd>.*?</td>"));
        TreeNode<BaseParsingRule> next23 = next2.addChild(new RegexMatchRule(5,"<a[^>]*?(?=href)href=\"(.*?/[Pp]rint-?[Ff]orm/.*?)\""));
        TreeNode<BaseParsingRule> next24 = next2.addChild(new RegexMatchRule(6,"<a[^>]*?(?=href)href=\"([^\"]*?common-info.*?)\""));
        TreeNode<BaseParsingRule> next25 = next2.addChild(new RegexMatchRule(7,"<a[^>]*?(?=href)href=\"([^\"]*?documents.*?)\""));
        return root0;
    }
}

