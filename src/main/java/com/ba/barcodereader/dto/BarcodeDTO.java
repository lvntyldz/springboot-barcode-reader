package com.ba.barcodereader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarcodeDTO {
    private boolean readSuccessfully;
    private List<String> dataList;
}
