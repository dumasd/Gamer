package com.thinkerwolf.gamer.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

/**
 * Hash算法
 *
 * @author wukai
 * @link https://github.com/zhishan332/ConsistantHash
 */
public enum HashAlgorithm {
    /**
     * java hashCode
     */
    NATIVE,
    /**
     * crc32
     */
    CRC32,
    /**
     * 基于md5的hash
     */
    KETAMA,
    /**
     * MurMurHash算法，是非加密HASH算法，性能很高，
     * 比传统的CRC32,MD5,SHA-1,SHA-256（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免）
     * 等HASH算法要快很多，这个算法的碰撞率很低.
     * http://murmurhash.googlepages.com/
     */
    MURMUR,
    ;

    public static HashAlgorithm nameOf(String name) {
        return valueOf(name.toUpperCase());
    }

    private static byte[] md5(String k) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            return digest.digest(k.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public long hash(String k) {
        long hash = 0;
        switch (this) {
            case NATIVE:
                hash = k.hashCode();
                break;
            case CRC32:
                CRC32 crc = new CRC32();
                crc.update(k.getBytes(StandardCharsets.UTF_8));
                hash = crc.getValue() >> 16 & 0x7fff;
                break;
            case KETAMA:
                byte[] bKey = md5(k);
                hash = (long) (bKey[3] & 0xFF) << 24 | (long) (bKey[2] & 0xFF) << 16
                        | (long) (bKey[1] & 0xFF) << 8 | bKey[0] & 0xFF;
                break;
            case MURMUR:
                ByteBuffer buf = ByteBuffer.wrap(k.getBytes());
                int seed = 0x1234ABCD;

                ByteOrder byteOrder = buf.order();
                buf.order(ByteOrder.LITTLE_ENDIAN);

                long m = 0xc6a4a7935bd1e995L;
                int r = 47;

                long rv = seed ^ (buf.remaining() * m);

                long ky;
                while (buf.remaining() >= 8) {
                    ky = buf.getLong();

                    ky *= m;
                    ky ^= ky >>> r;
                    ky *= m;

                    rv ^= ky;
                    rv *= m;
                }

                if (buf.remaining() > 0) {
                    ByteBuffer finish = ByteBuffer.allocate(8).order(
                            ByteOrder.LITTLE_ENDIAN);
                    // for big-endian version, do this first:
                    // finish.position(8-buf.remaining());
                    finish.put(buf).rewind();
                    rv ^= finish.getLong();
                    rv *= m;
                }

                rv ^= rv >>> r;
                rv *= m;
                rv ^= rv >>> r;
                buf.order(byteOrder);
                hash = rv;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return hash;
    }


}
