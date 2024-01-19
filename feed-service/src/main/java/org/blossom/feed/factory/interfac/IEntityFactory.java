package org.blossom.feed.factory.interfac;

public interface IEntityFactory<T, D> {
    T buildEntity(D data);
}