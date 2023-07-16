package org.blossom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SearchParametersDto {
    @NotBlank
    String contains;
    @Min(0)
    Integer page;
    @Min(1)
    Integer pageLimit;

    public boolean hasPagination() {
        return Objects.nonNull(page) && Objects.nonNull(pageLimit);
    }
}
