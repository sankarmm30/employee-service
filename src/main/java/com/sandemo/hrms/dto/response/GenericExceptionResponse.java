package com.sandemo.hrms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sandemo.hrms.constant.GlobalConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
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