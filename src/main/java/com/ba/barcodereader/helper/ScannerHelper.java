package com.ba.barcodereader.helper;

import com.ba.barcodereader.exception.SystemException;
import com.ba.barcodereader.props.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScannerHelper {

    private ScannerHelper() {
    }

    public static void scanFile() {

        try {

            Process process = new ProcessBuilder(Config.SCANNER_EXE_PATH).start();
            process.waitFor(5, TimeUnit.SECONDS);
            new ProcessBuilder("taskkill /IM \"kodak.scan.exe\" /F").start();

        } catch (FileNotFoundException e) {
            log.error("File not found! Full file path : {} - e : {} ", Config.SCANNER_EXE_PATH, e);
            throw new SystemException("Read file operation failed!");
        } catch (IOException e) {
            log.error("Something went wrong while processing exe file! File path : {} - e: {} ", Config.SCANNER_EXE_PATH, e);
            throw new SystemException("Process file failed!");
        } catch (InterruptedException e) {
            log.error("Something went wrong while processing file and waiting to close exe! e : {}", e);
            throw new SystemException("Process file failed while waiting!");
        }
    }
}
