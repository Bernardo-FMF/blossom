package org.blossom.auth.mapper.interfac;

import org.blossom.auth.dto.PaginationInfoDto;

import java.util.Collection;

public interface IPaginatedDtoMapper<E, T, D, P> extends IDtoMapper<E, D> {
    T toPaginatedDto(Collection<E> entities, P paginationInfo);

    default PaginationInfoDto createPaginationInfo(long currentPage, long allPages, long totalElements, boolean eof) {
        return PaginationInfoDto.builder().currentPage(currentPage)
                .totalPages(allPages)
                .totalElements(totalElements)
                .eof(eof)
                .build();
    }
}
