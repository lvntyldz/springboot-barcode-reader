package com.ba.barcodereader.helper;

import com.ba.barcodereader.exception.SystemException;
import com.ba.barcodereader.props.Config;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class RegexHelper {

    public static String findCartNumberWithRegex(String stringToSearch) {

        log.info("All regex data to search : {}", stringToSearch);
        final String regexPattern = "((\\d){1}(R)(\\d){11})";

        Pattern pattern = Pattern.compile(regexPattern);

        Matcher matcher = pattern.matcher(stringToSearch);

        return (matcher.find()) ? (matcher.toMatchResult().group(1)) : (null);
    }

}
