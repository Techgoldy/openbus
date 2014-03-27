package com.produban.openbus.analysis;

import backtype.storm.tuple.Values;
import org.json.JSONArray;
import org.json.JSONObject;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;

import static com.produban.openbus.util.Common.join;

/**
 * Class Description
 */
public class TweetJsonDecoder extends BaseFunction {

    @Override
    public void execute(TridentTuple objects, TridentCollector tridentCollector) {

        byte[] jsonBytes = objects.getBinary(0);
        JSONObject jsonObj = new JSONObject(new String(jsonBytes));

        String tweetId = jsonObj.getString("id_str");
        String rawDate = jsonObj.getString("created_at");
        String text = jsonObj.getString("text");
        int retweetCount = jsonObj.getInt("retweet_count");


        JSONArray coordinates = jsonObj.getJSONObject("coordinates").getJSONArray("coordinates");
        double longitude = coordinates.getDouble(0);
        double latitude = coordinates.getDouble(1);

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
            JSONObject mention = entityUrls.getJSONObject(i);
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
        tridentValues.add(text);
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
