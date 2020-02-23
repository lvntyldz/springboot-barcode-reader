package com.ba.barcodereader.helper;

import com.ba.barcodereader.props.Config;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ScannerHelper {

    public static void scanFile() {
        try {
            Process process = new ProcessBuilder(Config.SCANNER_EXE_PATH).start();
            //process.destroy();
        } catch (FileNotFoundException e) {//TODO:Development[Throw custom exception]
            e.printStackTrace();//TODO:Add Logger
        } catch (IOException e) {
            e.printStackTrace();//TODO:Add Logger
        }
    }
}