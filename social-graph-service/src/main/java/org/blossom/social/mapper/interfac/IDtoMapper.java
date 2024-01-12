package org.blossom.social.mapper.interfac;

public interface IDtoMapper<E, D> {
    D toDto(E entity);
}