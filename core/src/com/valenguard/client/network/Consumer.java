package com.valenguard.client.network;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T data);
}
