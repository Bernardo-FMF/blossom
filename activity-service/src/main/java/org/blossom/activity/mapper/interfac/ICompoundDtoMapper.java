package org.blossom.activity.mapper.interfac;

public interface ICompoundDtoMapper<E, T, D> {
    D toDto(E entity, T entity2);
}