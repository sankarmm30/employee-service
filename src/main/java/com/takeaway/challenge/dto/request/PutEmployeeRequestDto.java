package com.takeaway.challenge.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.takeaway.challenge.constant.GlobalConstant;
import com.takeaway.challenge.util.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutEmployeeRequestDto {

    @Size(max = 50, message = "Name should not have more than 50 characters")
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email should not have more than 50 characters")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GlobalConstant.DATE_FORMAT)
    @PastOrPresent(message = "Date of birth cannot be a future date")
    private LocalDate dateOfBirth;

    private Long departmentId;

    @AssertTrue(message = "Minimum one attribute should be provided for the update")
    private boolean isValid() {

        return StringUtils.hasText(name) || StringUtils.hasText(email)
                || dateOfBirth != null || departmentId != null;
    }

    @AssertTrue(message = "Invalid name provided")
    private boolean isValidNameWhenNotNull() {

        if(Util.isNotNull(name)) {

            return StringUtils.hasText(name);
        }

        return true;
    }

    @AssertTrue(message = "Invalid email provided")
    private boolean isValidEmailWhenNotNull() {

        if(Util.isNotNull(email)) {

            return StringUtils.hasText(email);
        }

        return true;
    }
}
