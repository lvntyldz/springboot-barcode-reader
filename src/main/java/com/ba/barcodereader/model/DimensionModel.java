package com.ba.barcodereader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DimensionModel {

    private int xPoint;
    private int yPoint;
    private int width;
    private int height;

}
