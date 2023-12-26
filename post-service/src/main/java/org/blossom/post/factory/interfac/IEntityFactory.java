package org.blossom.post.factory.interfac;

public interface IEntityFactory<T, D> {
    T buildEntity(D data);
}
