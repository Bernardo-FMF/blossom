package org.blossom.notification.factory.interfac;

public interface IEntityFactory<T, D> {
    T buildEntity(D data);
}