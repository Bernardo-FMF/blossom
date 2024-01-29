package org.blossom.message.mapper.interfac;

public interface IDtoMapper<E, D> {
    D toDto(E entity);
}