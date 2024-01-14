package org.blossom.activity.mapper.interfac;

public interface IDtoMapper<E, D> {
    D toDto(E entity);
}
