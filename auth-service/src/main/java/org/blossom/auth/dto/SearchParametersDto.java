package org.blossom.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SearchParametersDto {
    String contains;
    Integer page;
    Integer pageLimit;

    public boolean hasPagination() {
        return Objects.nonNull(page) && Objects.nonNull(pageLimit);
    }
}
