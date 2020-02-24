package com.ba.barcodereader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarcodeModel {
    private boolean readSuccessfully;
    private List<String> dataList;
}
