package com.ba.barcodereader.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private Date timestamp;
    private String message;
    private String details;
}
