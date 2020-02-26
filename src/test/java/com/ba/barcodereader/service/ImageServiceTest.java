package com.ba.barcodereader.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {

    @InjectMocks
    ImageService service;

    @Test
    public void shouldReturnBarcodeListWhenGivenImageHasDataMatrix() {
        List<String> dataList = service.readBarcodeWithZXingFromScannedImage();
        assertNotNull(dataList);
        assertEquals(dataList.size(), 1);
    }

}
