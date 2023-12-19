package org.blossom.auth.mapper.interfac;

import java.util.Collection;

public interface IPaginatedDtoMapper<E, T, D, P> extends IDtoMapper<E, D> {
    T toPaginatedDto(Collection<E> entities, P paginationInfo);
}
