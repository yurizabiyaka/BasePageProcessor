package com.example.dariya.regexparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dariya on 19.02.2017.
 */

public class RegexRule extends BaseParsingRule {
    private String FRegex;

    public RegexRule(int aRegexId, String aRegex){
        super(aRegexId);
        FRegex = aRegex;
    }

    public List<List<String>> Apply(String aText){
        List<List<String>> result = new ArrayList<List<String>>();
        Pattern p = Pattern.compile(FRegex, Pattern.DOTALL | Pattern.MULTILINE);
        Matcher m = p.matcher(aText);
        while(m.find()){
            // add the next result for self:
            List<String> findResult = new ArrayList<String>();
            result.add(findResult); // include zero group
            // for all groups add results:
            for(int i=0; i<=m.groupCount(); i++) // add the group result:
                findResult.add(m.group(i));
        }
        return result;
    }
}
