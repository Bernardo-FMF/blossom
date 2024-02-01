package org.blossom.notification.factory.interfac;

public interface ICompoundEntityFactory<T, D, E> {
    T buildEntity(D data, E data2);
}