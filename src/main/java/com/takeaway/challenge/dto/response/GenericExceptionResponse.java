package com.takeaway.challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.takeaway.challenge.constant.GlobalConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericExceptionResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_TIME_FORMAT)
    private ZonedDateTime timestamp;

    private Integer status;
    private String message;
    private List<String> errors;
    private String path;
}