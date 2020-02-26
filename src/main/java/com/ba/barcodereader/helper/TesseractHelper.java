package com.ba.barcodereader.helper;

import com.ba.barcodereader.exception.SystemException;
import com.ba.barcodereader.props.Config;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TesseractHelper {

    private TesseractHelper() {
    }

    public static List<String> getFinalDataByLength(String text) {

        List<String> data = new ArrayList<>();

        String[] datasByNewLine = text.split("\n");

        for (String row : datasByNewLine) {
            String[] allData = row.split(" ");
            for (String s : allData) {
                s = s.replaceAll("\\D+", "");//remmove nonDigitData
                if (s.length() == 11 || s.length() == 12 || s.length() == 13) {
                    data.add(s);
                }
            }
        }

        return data;
    }

    public static String getTextFromImage(BufferedImage subimage) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(Config.DATA_FOLDER);
        try {
            return tesseract.doOCR(subimage);
        } catch (TesseractException e) {
            //log.error("OCR not read image as text! e : {} ", e);
            throw new SystemException("Could not read text!");
        }
    }

}
