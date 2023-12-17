package org.blossom.auth.factory.interfac;

public interface IEntityFactory<T, D> {
    T buildEntity(D data);
}
