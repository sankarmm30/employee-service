package com.takeaway.challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EmployeeDetailsResponseDto {

    private String employeeId;
    private String name;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD")
    private LocalDate dataOfBirth;

    private DepartmentDto department;
}
