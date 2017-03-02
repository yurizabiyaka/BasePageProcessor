package com.example.dariya.regexparser;

/**
 * Created by Пользователь on 28.02.2017.
 */

public enum UrlstringBuilderFactory {
    INSTANCE;

    public BaseUrlstringBuilder getUrlstringBuilder(final String aTag){
        if("SearchResults".equals(aTag))
            return new ExpenseSearchUrlBuilder();
        if("Fz44_Test_1".equals(aTag))
            return new Fz44PrintFormUrlBuilder();
        else
            return null;
    }
}
