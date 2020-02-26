package com.ba.barcodereader.dto;

import java.util.List;

public class BarcodeDTO {
    private boolean readSuccessfully;
    private List<String> dataList;

    public BarcodeDTO() {
    }

    public BarcodeDTO(boolean readSuccessfully, List<String> dataList) {
        this.readSuccessfully = readSuccessfully;
        this.dataList = dataList;
    }

    public boolean isReadSuccessfully() {
        return readSuccessfully;
    }

    public void setReadSuccessfully(boolean readSuccessfully) {
        this.readSuccessfully = readSuccessfully;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }
}
