package com.produban.openbus.analysis;

import backtype.storm.tuple.Values;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.produban.openbus.util.Common.join;

/**
 * Class Description
 */
public class TweetJsonDecoder extends BaseFunction {

    private static final Logger logger = LoggerFactory.getLogger(TweetJsonDecoder.class);

    @Override
    public void execute(TridentTuple objects, TridentCollector tridentCollector) {

        byte[] jsonBytes = objects.getBinary(0);
        String jsonString = new String(jsonBytes);

        if (jsonBytes.length == 0) {
            logger.warn("EMPTY json bytes received!");
            return;
        }

        logger.info("DECODING JSON:");
        logger.info(jsonString);
        logger.info("FIRST CHAR:" + jsonString.charAt(0));

        JSONObject jsonObj = new JSONObject(jsonString.trim());


        String tweetId = jsonObj.getString("id_str");
        String rawDate = jsonObj.getString("created_at");

        String twitterDateFormat = "EEE MMM dd HH:mm:ss Z yyyy";
        Date tweetDate = null;
        try {
            tweetDate = new SimpleDateFormat(twitterDateFormat).parse(rawDate);
        }
        catch (ParseException e) {
            logger.error("Error parsing tweet date: "+rawDate);
            logger.error(e.toString());
        }

        String text = jsonObj.getString("text");
        String lang = jsonObj.isNull("lang") ? null : jsonObj.getString("lang");
        int retweetCount = jsonObj.getInt("retweet_count");


        double longitude = 0;
        double latitude = 0;
        if (!jsonObj.isNull("coordinates")) {
            JSONArray coordinates =  jsonObj.getJSONObject("coordinates").getJSONArray("coordinates");
            longitude = coordinates.getDouble(0);
            latitude = coordinates.getDouble(1);
        }

        JSONObject user = jsonObj.getJSONObject("user");
        int userFollowerCount = user.getInt("followers_count");
        String userLocation = user.getString("location");
        String userName = user.getString("screen_name");
        String userId = user.getString("id_str");
        String userImgUrl = user.getString("profile_image_url");

        JSONObject entities = jsonObj.getJSONObject("entities");
        JSONArray entityUrls = entities.getJSONArray("urls");
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < entityUrls.length(); i++) {
            JSONObject entityUrl = entityUrls.getJSONObject(i);
            urls.add(entityUrl.getString("url"));
        }

        JSONArray entityUserMentions = entities.getJSONArray("user_mentions");
        ArrayList<String> mentionedUsers = new ArrayList<>();
        for (int i = 0; i < entityUserMentions.length(); i++) {
            JSONObject mention = entityUserMentions.getJSONObject(i);
            mentionedUsers.add(mention.getString("screen_name"));
        }

        JSONArray entityHashTags = entities.getJSONArray("hashtags");
        ArrayList<String> hashtags = new ArrayList<>();
        for (int i = 0; i < entityHashTags.length(); i++) {
            JSONObject hashtag = entityHashTags.getJSONObject(i);
            hashtags.add(hashtag.getString("text"));
        }

        Values tridentValues = new Values();
        tridentValues.add(tweetId);
        tridentValues.add(rawDate);
        tridentValues.add(tweetDate);
        tridentValues.add(text);
        tridentValues.add(lang);
        tridentValues.add(retweetCount);
        tridentValues.add(longitude);
        tridentValues.add(latitude);
        tridentValues.add(userFollowerCount);
        tridentValues.add(userLocation);
        tridentValues.add(userName);
        tridentValues.add(userId);
        tridentValues.add(userImgUrl);
        tridentValues.add(join(urls, "|"));
        tridentValues.add(join(mentionedUsers, "|"));
        tridentValues.add(join(hashtags, "|"));

        tridentCollector.emit(tridentValues);
    }
}
