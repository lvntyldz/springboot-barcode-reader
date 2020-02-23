package com.ba.barcodereader.util;

import com.ba.barcodereader.props.Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileUtils {

    private static final String JPG_EX = "jpg";
    private static final String JPG_FULL_EX = ".jpg";

    public static String writeToTargetAsJpg(BufferedImage image, String name) throws IOException {
        String outputFilePath = getFullOutputFilePath(name);
        File output = new File(outputFilePath);
        ImageIO.write(image, JPG_EX, output);
        return outputFilePath;
    }

    private static String getFullOutputFilePath(String name) {
        return Config.TARGET_DIR + name + JPG_FULL_EX;
    }
}