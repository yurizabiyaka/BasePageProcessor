package com.example.dariya.regexparser;

/**
 * Created by Пользователь on 28.02.2017.
 */

public enum UrlstringBuilderFactory {
    INSTANCE;

    public BaseUrlstringBuilder getUrlstringBuilder(final String aTag){
        if("SearchResults".equals(aTag))
            return new ExpenseSearchUrlBuilder();
        else
            return null;
    }
}
