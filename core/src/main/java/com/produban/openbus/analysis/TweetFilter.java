package com.produban.openbus.analysis;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

import static com.produban.openbus.util.Common.join;

/**
 * Trident filter for tweets
 */
public class TweetFilter extends BaseFilter {

    String regex;

    public TweetFilter(String[] keywords) {
        this.regex = "\\W("+join(keywords, "|") + ")\\W";
    }

    @Override
    //filter every tweet not containing at least one keyword
    public boolean isKeep(TridentTuple objects) {
        String text = objects.getString(0);
        return text.matches(this.regex) ? true : false;
    }
}
