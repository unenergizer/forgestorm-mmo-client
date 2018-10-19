package com.valenguard.client.util;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T data);
}
