package com.thinkerwolf.gamer.core.serialization;


import com.thinkerwolf.gamer.common.SPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 序列化
 *
 * @author wukai
 */
@SPI("jdk")
public interface Serializer {

    ObjectOutput serialize(OutputStream os) throws IOException;

    ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException;

}
