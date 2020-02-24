package com.ba.barcodereader.enums;

public enum Dimensions {

    //barcode frame dimensions
    BARCODE_FRAME_X(0),
    BARCODE_FRAME_Y(400),
    BARCODE_FRAME_W(300),
    BARCODE_FRAME_H(1200),

    //tesseract frame dimensions
    TESSERACT_FRAME_X(2000),
    TESSERACT_FRAME_Y(0),
    TESSERACT_FRAME_W(1200),
    TESSERACT_FRAME_H(300),

    //header frame dimensions
    HEADER_FRAME_X(0),
    HEADER_FRAME_Y(0),
    HEADER_FRAME_W(1600),
    HEADER_FRAME_H(300),

    //image dimensions
    RGB_WHITE_THRESHOLD(230),//230-255 arası beyaz kabul edilir
    SUB_IMAGE_DIMENSION(180),//180px genişliğinde beyaz kare aranır resimde

    //for tesseract
    WHITE_COLOR(16777215),
    BLACK_COLOR(0),
    RGB_THRESHOLD(120);

    private int val;

    Dimensions(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

}
