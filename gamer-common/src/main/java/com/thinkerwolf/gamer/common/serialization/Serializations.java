package com.thinkerwolf.gamer.common.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;

/**
 * 序列化集合工具
 *
 * @author wukai
 * @date 2020/5/13 17:31
 */
public final class Serializations {

    public static byte[] getBytes(Serializer serializer, Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput oo = null;
        try {
            oo = serializer.serialize(baos);
            oo.writeObject(obj);
            oo.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            closeQuietly(baos);
            closeQuietly(oo);
        }
    }

    public static <T> T getObject(Serializer serializer, byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInput oi = null;
        try {
            oi = serializer.deserialize(bais);
            return oi.readObject(clazz);
        } catch (IOException e) {
            throw e;
        } finally {
            closeQuietly(bais);
            closeQuietly(oi);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException ignored) {
        }
    }

    public static void closeQuietly(ObjectInput oi) {
        try {
            if (oi != null)
                oi.close();
        } catch (IOException ignored) {
        }
    }

    public static void closeQuietly(ObjectOutput oo) {
        try {
            if (oo != null)
                oo.close();
        } catch (IOException ignored) {
        }
    }
}
