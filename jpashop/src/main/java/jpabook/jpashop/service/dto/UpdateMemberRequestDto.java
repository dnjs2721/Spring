package jpabook.jpashop.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * update Request DTO
 */
@Data
public class UpdateMemberRequestDto {
    @NotEmpty
    private String name;
    private String city;
    private String street;
    private String zipcode;
}
