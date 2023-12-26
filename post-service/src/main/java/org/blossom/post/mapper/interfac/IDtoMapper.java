package org.blossom.post.mapper.interfac;

public interface IDtoMapper<E, D> {
    D toDto(E entity);
}
