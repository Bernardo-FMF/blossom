package org.blossom.auth.mapper.interfac;

public interface IDtoMapper<E, D> {
    D toDto(E entity);
}
