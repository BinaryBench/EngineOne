package me.engineone.core.holder.liveholders;

import me.engineone.core.holder.Holder;
import me.engineone.core.holder.LiveHolder;

import java.util.Iterator;

/**
 * Created by BinaryBench on 4/22/2017.
 */
public class UnionHolder<T> implements LiveHolder<T> {

    private Holder<T> primary;
    private Holder<T> secondary;

    public UnionHolder(Holder<T> primary, Holder<T> secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public Holder<T> getPrimary() {
        return primary;
    }

    @Override
    public Holder<T> getSecondary() {
        return secondary;
    }

    @Override
    public boolean test(T element) {
        return getPrimary().test(element) || getSecondary().test(element);
    }

    @Override
    public int size() {
        int size = getSecondary().size();
        for (T t : getPrimary())
            if (!getSecondary().test(t))
                size++;
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private final Iterator<T> first = getSecondary().iterator();
            private final Iterator<T> second = getPrimary().iterator();
            private T next;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public T next() {
                if (this.next == null)
                    return null;
                T next = this.next;
                this.next = first.hasNext() ? first.next() : second.hasNext() ? second.next() : null;
                return next;
            }
        };
    }
};