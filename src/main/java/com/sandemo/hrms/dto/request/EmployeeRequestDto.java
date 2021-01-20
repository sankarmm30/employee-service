package com.sandemo.hrms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sandemo.hrms.constant.GlobalConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDto {

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Name should not have more than 50 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email should not have more than 50 characters")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_FORMAT)
    @PastOrPresent(message = "Date of birth cannot be a future date")
    private LocalDate dateOfBirth;

    @NotNull(message = "Department id should be provided")
    private Long departmentId;
}
