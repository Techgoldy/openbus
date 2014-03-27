package com.produban.openbus.util;

import java.util.List;

/**
 * Helper methods
 */
public class Common {

    public static String join(String[] sentence, String separator)
    {
        String result = "";
        for (int i = 0; i < sentence.length; i++)
        {
            String word = sentence[i];
            result += word + separator;
        }
        return result.trim();
    }

    public static String join(List<String> sentence, String separator)
    {
        String result = "";
        for (String word : sentence)
        {
            result += word + separator;
        }
        return result.trim();
    }
}
