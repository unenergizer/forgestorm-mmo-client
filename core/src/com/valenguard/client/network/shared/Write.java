package com.valenguard.client.network.shared;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface Write {
    void accept(DataOutputStream outStream) throws IOException;
}
