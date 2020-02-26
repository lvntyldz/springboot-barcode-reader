package com.ba.barcodereader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DimensionDTO {

    private int xPoint;
    private int yPoint;
    private int width;
    private int height;

}
