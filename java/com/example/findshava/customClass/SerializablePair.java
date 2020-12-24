package com.example.findshava.customClass;

import androidx.core.util.ObjectsCompat;
import androidx.core.util.Pair;

import java.io.Serializable;

public class SerializablePair<T extends Serializable, F extends Serializable> implements Serializable {

    private T first;
    private F second;

    public SerializablePair(T first, F second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public F getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;
        return ObjectsCompat.equals(p.first, this.first) && ObjectsCompat.equals(p.second, this.second);
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Pair
     */
    @Override
    public int hashCode() {
        return (this.first == null ? 0 : this.first.hashCode()) ^ (this.second == null ? 0 : this.second.hashCode());
    }
}
