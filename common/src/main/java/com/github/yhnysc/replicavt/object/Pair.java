package com.github.yhnysc.replicavt.object;

import java.util.Comparator;
import java.util.Objects;

public class Pair<T, V> {
    private T _first;
    private V _second;

    public Pair(T first, V second){
        _first = first;
        _second = second;
    }

    public T getFirst(){
        return _first;
    }

    public V getSecond(){
        return _second;
    }

    @Override
    public String toString(){
        return "["+_first+"|"+_second+"]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<T, V> pair = (Pair<T, V>) o;
        return Objects.equals(_first, pair._first) && Objects.equals(_second, pair._second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_first, _second);
    }

}
