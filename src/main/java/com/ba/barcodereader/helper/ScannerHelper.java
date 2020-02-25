package com.ba.barcodereader.helper;

import com.ba.barcodereader.props.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScannerHelper {

    public static void scanFile() throws InterruptedException {
        try {
            Process process = new ProcessBuilder(Config.SCANNER_EXE_PATH).start();
            process.waitFor(5, TimeUnit.SECONDS);
            new ProcessBuilder("taskkill /IM \"kodak.scan.exe\" /F").start();
        } catch (FileNotFoundException e) {//TODO:Development[Throw custom exception]
            log.error("File not found! Full file path : {}", Config.SCANNER_EXE_PATH);
        } catch (IOException e) {
            log.error("Something went wrong while processing exe file! File path : {}", Config.SCANNER_EXE_PATH);
        }
    }
}