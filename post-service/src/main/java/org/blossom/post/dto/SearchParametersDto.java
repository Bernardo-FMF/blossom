package org.blossom.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SearchParametersDto {
    String query;
    Integer page;
    Integer pageLimit;

    public boolean hasPagination() {
        return Objects.nonNull(page) && Objects.nonNull(pageLimit);
    }
}
