package com.ba.barcodereader.props;

//TODO:Development[Read from properties file]
public class Config {

    public static final String SCANNER_DEST_FOLDER = "C:\\twain";
    public static final String SCANNED_FILE_PATH = "C:\\twain\\img000001.jpg";//windows
    //public static final String SCANNED_FILE_PATH = "dist/k1/img000001.jpg";//mac
    public static final String SCANNER_EXE_PATH = "kodak\\kodak.scan.exe";
    public static final String TEMP_DIR = "target\\";
    //public static final String DATA_FOLDER = "/usr/share/tesseract-ocr/4.00/tessdata";//ubuntu
    //public static final String DATA_FOLDER = "/usr/local/Cellar/tesseract/4.1.1/share/tessdata";//mac
    public static final String DATA_FOLDER = "C:\\Program Files\\Tesseract-OCR\\tessdata";//windows
    public static final String CROP_IMG_NAME = "croppedImage";
    public static final String WHITE_FRAME_IMG_NAME = "whiteFrameImage";

}
