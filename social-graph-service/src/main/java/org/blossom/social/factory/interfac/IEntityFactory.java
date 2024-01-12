package org.blossom.social.factory.interfac;

public interface IEntityFactory<T, D> {
    T buildEntity(D data);
}