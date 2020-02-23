package com.ba.barcodereader.helper;

import com.ba.barcodereader.enums.Config;

import java.io.IOException;

public class ScannerHelper {

    public static void scanFile() {
        try {
            Process process = new ProcessBuilder(Config.SCANNER_EXE_PATH).start();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();//TODO:Add Logger
        }
    }
}
