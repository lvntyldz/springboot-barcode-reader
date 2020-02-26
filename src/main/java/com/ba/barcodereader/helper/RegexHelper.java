package com.ba.barcodereader.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class RegexHelper {

    private RegexHelper() {
    }

    public static String findCartNumberWithRegex(String stringToSearch) {

        log.info("All regex data to search : {}", stringToSearch);
        final String regexPattern = "((\\d){1}(R)(\\d){11})";

        Pattern pattern = Pattern.compile(regexPattern);

        Matcher matcher = pattern.matcher(stringToSearch);

        return (matcher.find()) ? (matcher.toMatchResult().group(1)) : (null);
    }

}
