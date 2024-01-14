package org.blossom.activity.factory.interfac;

public interface IEntityFactory<T, D> {
    T buildEntity(D data);
}