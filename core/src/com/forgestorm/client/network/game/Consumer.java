package com.forgestorm.client.network.game;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T data);
}
