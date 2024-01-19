package org.blossom.feed.mapper.interfac;

public interface IDtoMapper<E, D> {
    D toDto(E entity);
}