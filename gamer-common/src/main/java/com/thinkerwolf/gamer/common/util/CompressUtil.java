package com.thinkerwolf.gamer.common.util;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩工具，支持gzip和deflate
 *
 * @author wukai
 * @data 2020-03-21
 */
public final class CompressUtil {

    public static final String GZIP = "gzip";
    public static final String DEFLATE = "deflate";
    private static final Logger LOG = InternalLoggerFactory.getLogger(CompressUtil.class);

    public static String getCompress(Collection<String> c) {
        if (c == null) {
            return null;
        }
        if (c.isEmpty()) {
            return null;
        }
        if (c.contains(GZIP)) {
            return GZIP;
        } else if (c.contains(DEFLATE)) {
            return DEFLATE;
        }
        return null;
    }

    public static byte[] compress(byte[] bytes, String encoding) throws IOException {
        if (GZIP.equals(encoding)) {
            return compressGzip(bytes);
        } else if (DEFLATE.equals(encoding)) {
            return compressDeflate(bytes);
        }
        return bytes;
    }

    public static byte[] decompress(byte[] bytes, String encoding) throws IOException {
        if (GZIP.equals(encoding)) {
            return decompressGzip(bytes);
        } else if (DEFLATE.equals(encoding)) {
            return decompressDeflate(bytes);
        }
        return bytes;
    }

    public static byte[] compressGzip(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(512);
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(bytes);
            gzip.flush();
        } finally {
            IOUtils.closeQuietly(gzip, e -> LOG.error("Close gzip out", e));
        }
        return out.toByteArray();
    }

    public static byte[] compressDeflate(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(512);
        DeflaterOutputStream deflater = null;
        try {
            deflater = new DeflaterOutputStream(out);
            deflater.write(bytes);
        } finally {
            IOUtils.closeQuietly(deflater, e -> LOG.error("Close deflate out", e));
        }
        return out.toByteArray();
    }

    public static byte[] decompressGzip(byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream gzip = null;
        byte[] buffer = new byte[1024];
        try {
            gzip = new GZIPInputStream(in);
            int n;
            while ((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } finally {
            IOUtils.closeQuietly(gzip, e -> LOG.error("Close gzip in", e));
        }
        return out.toByteArray();
    }

    public static byte[] decompressDeflate(byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream(512);
        DeflaterInputStream deflater = null;
        byte[] buffer = new byte[1024];
        try {
            deflater = new DeflaterInputStream(in);
            int n;
            while ((n = deflater.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } finally {
            IOUtils.closeQuietly(deflater, e -> LOG.error("Close deflate in", e));
        }
        return out.toByteArray();
    }
}
