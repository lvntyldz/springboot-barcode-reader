package com.ba.barcodereader.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseModel {
    private Date timestamp;
    private String message;
    private String details;
}
