package com.ba.barcodereader.helper;

import com.ba.barcodereader.exception.SystemException;
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

    public String writeToTempAsJpg(BufferedImage image, String name) {
        String outputFilePath = getFullOutputFilePath(name);
        File output = new File(outputFilePath);
        try {
            ImageIO.write(image, JPG_EX, output);
        } catch (IOException e) {
            throw new SystemException("File write opreation failed!");
        }
        return outputFilePath;
    }

    private String getFullOutputFilePath(String name) {
        return Config.TEMP_DIR + name + JPG_FULL_EX;
    }

    public String getCroppedImgPath() {
        return getFullOutputFilePath(Config.CROP_IMG_NAME);
    }

    public String getWhiteFrameImgPath() {
        return getFullOutputFilePath(Config.WHITE_FRAME_IMG_NAME);
    }
}
