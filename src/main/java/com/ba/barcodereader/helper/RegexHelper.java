package com.ba.barcodereader.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {

    private RegexHelper() {
    }

    public static String findCartNumberWithRegex(String stringToSearch) {

        final String regexPattern = "((\\d){1}(R)(\\d){11})";

        Pattern pattern = Pattern.compile(regexPattern);

        Matcher matcher = pattern.matcher(stringToSearch);

        return (matcher.find()) ? (matcher.toMatchResult().group(1)) : (null);
    }

}
