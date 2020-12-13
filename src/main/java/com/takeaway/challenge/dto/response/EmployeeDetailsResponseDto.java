package com.takeaway.challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.takeaway.challenge.constant.GlobalConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EmployeeDetailsResponseDto {

    private String employeeId;
    private String name;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_FORMAT)
    private LocalDate dataOfBirth;

    private DepartmentDto department;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_TIME_FORMAT)
    private ZonedDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_TIME_FORMAT)
    private ZonedDateTime lastUpdatedAt;
}
