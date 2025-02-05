package com.sandemo.hrms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDto {

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Name should not have more than 50 characters")
    private String name;
}
