package com.valenguard.client.network.shared;

import com.valenguard.client.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings({"ConstantConditions", "unused"})
@AllArgsConstructor
@Getter
public class ClientHandler {
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    @FunctionalInterface
    private interface Reader {
        Object accept() throws IOException;
    }

    public boolean readBoolean() {
        return (Boolean) readIn(new Reader() {
            @Override
            public Boolean accept() throws IOException {
                return inputStream.readBoolean();
            }
        });
    }

    public String readString() {
        return (String) readIn(new Reader() {
            @Override
            public String accept() throws IOException {
                return inputStream.readUTF();
            }
        });
    }

    public byte readByte() {
        return (Byte) readIn(new Reader() {
            @Override
            public Byte accept() throws IOException {
                return inputStream.readByte();
            }
        });
    }

    public char readChar() {
        return (Character) readIn(new Reader() {
            @Override
            public Character accept() throws IOException {
                return inputStream.readChar();
            }
        });
    }

    public double readDouble() {
        return (Double) readIn(new Reader() {
            @Override
            public Double accept() throws IOException {
                return inputStream.readDouble();
            }
        });
    }

    public float readFloat() {
        return (Float) readIn(new Reader() {
            @Override
            public Float accept() throws IOException {
                return inputStream.readFloat();
            }
        });
    }

    public int readInt() {
        return (Integer) readIn(new Reader() {
            @Override
            public Integer accept() throws IOException {
                return inputStream.readInt();
            }
        });
    }

    public long readLong() {
        return (Long) readIn(new Reader() {
            @Override
            public Long accept() throws IOException {
                return inputStream.readLong();
            }
        });
    }

    public short readShort() {
        return (Short) readIn(new Reader() {
            @Override
            public Short accept() throws IOException {
                return inputStream.readShort();
            }
        });
    }

    private Object readIn(Reader reader) {
        try {
            return reader.accept();
        } catch (SocketException e) {
            Log.println(getClass(), "Tried to read data, but socket closed!", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This is used to send the entity packet data.
     *
     * @param opcode        The code that defines what this packet contents will contain.
     * @param writeCallback The data we will be sending to the client.
     */
    public void write(byte opcode, Write writeCallback) {
        try {
            outputStream.writeByte(opcode);
            writeCallback.accept(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects this client from the server.
     */
    public void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            socket = null;
            outputStream = null;
            inputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
