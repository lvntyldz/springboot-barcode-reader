package com.ba.barcodereader.dto;

import java.util.List;

public class ResponseDTO {
    private List<String> barcodes;
    private List<String> files;

    public ResponseDTO(List<String> barcodes, List<String> files) {
        this.barcodes = barcodes;
        this.files = files;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
