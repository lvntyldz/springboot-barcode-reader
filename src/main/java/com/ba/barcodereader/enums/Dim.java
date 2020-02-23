package com.ba.barcodereader.enums;

public enum Dim {

    //barcode frame dimensions
    BARCODE_FRAME_X(0),
    BARCODE_FRAME_Y(400),
    BARCODE_FRAME_W(300),
    BARCODE_FRAME_H(1200),


    //image dimensions
    RGB_WHITE_THRESHOLD(230),//230-255 arası beyaz kabul edilir
    SUB_IMAGE_DIMENSION(180);//180px genişliğinde beyaz kare aranır resimde

    private int val;

    Dim(int val) {
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }

}
