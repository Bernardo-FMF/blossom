package org.blossom.auth.delta;

import lombok.AllArgsConstructor;
import org.blossom.auth.delta.markable.EntityMarkable;

import java.util.function.BiConsumer;

@AllArgsConstructor
public class DeltaEngine<T extends EntityMarkable<?>, E> {
    private BiConsumer<T, E> consumer;
    public void applyDelta(T markable, E entity) {
        consumer.accept(markable, entity);
    }
}
