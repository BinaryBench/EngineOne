package me.engineone.core.holder;

import me.engineone.core.holder.liveholders.DifferenceHolder;
import me.engineone.core.holder.liveholders.UnionHolder;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Holder<T> extends Predicate<T>, Iterable<T> {
    int size();

    default Holder<T> union(Holder<T> holder) {
        return new UnionHolder<>(this, holder);
    }

    default DifferenceHolder<T> difference(Holder<T> holder) {
        return new DifferenceHolder<>(this, holder);
    }

    default PartitionHolder<T> partition(Predicate<T> filter) {
        return new PartitionHolder<T>() {
            @Override
            public Predicate<T> getFilter() {
                return filter;
            }

            @Override
            public Holder<T> getParent() {
                return Holder.this;
            }
        };
    }

    @Override
    default Spliterator<T> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.DISTINCT);
    }

    default Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), Spliterator.DISTINCT), false);
    }

    default Stream<T> parallelStream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), Spliterator.DISTINCT), true);
    }

    @Override
    boolean test(T element);
}