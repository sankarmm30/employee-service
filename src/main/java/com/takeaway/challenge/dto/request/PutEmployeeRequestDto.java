package com.takeaway.challenge.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutEmployeeRequestDto {

    private String name;

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private Long departmentId;

    @AssertTrue
    public boolean isValid() {

        return StringUtils.hasText(name) || StringUtils.hasText(email)
                || dateOfBirth != null || departmentId != null;
    }
}
