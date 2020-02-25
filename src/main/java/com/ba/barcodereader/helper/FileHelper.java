package com.ba.barcodereader.helper;

import com.ba.barcodereader.props.Config;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class FileHelper {

    private static final String JPG_EX = "jpg";
    private static final String JPG_FULL_EX = ".jpg";

    public String writeToTempAsJpg(BufferedImage image, String name) throws IOException {
        String outputFilePath = getFullOutputFilePath(name);
        File output = new File(outputFilePath);
        ImageIO.write(image, JPG_EX, output);
        return outputFilePath;
    }

    private String getFullOutputFilePath(String name) {
        return Config.TEMP_DIR + name + JPG_FULL_EX;
    }

    public String getCroppedImgPath() {
        return getFullOutputFilePath(Config.CROP_IMG_NAME);
    }
}