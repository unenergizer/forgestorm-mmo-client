package com.valenguard.client.network.game;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T data);
}
