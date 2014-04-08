package com.produban.openbus.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

import java.util.List;

import static com.produban.openbus.util.Common.join;

/**
 * Trident filter for tweets
 */
public class TweetFilter extends BaseFilter {

    String regex;
    private static final Logger logger = LoggerFactory.getLogger(TweetFilter.class);

    public TweetFilter(List<String> keywords) {
        //lowercase text and words
        logger.info("tweet filter keywords:");
        for (int i = 0; i < keywords.size(); i++) {
            keywords.set(i, keywords.get(i).toLowerCase());
            System.out.println(keywords.get(i));
        }

        this.regex = ".*\\W("+join(keywords, "|") + ")\\W.*";
    }

    @Override
    //filter every tweet not containing at least one keyword
    public boolean isKeep(TridentTuple objects) {
        String text = objects.getString(0).toLowerCase();
        if (text == null){
            logger.info("no text could be retrieved from tuple");
            return false;
        }

        if (text.matches(this.regex)) {
            logger.info("matches!");
            return true;
        }
        else {
            logger.info("filtered!");
            return false;
        }
    }
}
