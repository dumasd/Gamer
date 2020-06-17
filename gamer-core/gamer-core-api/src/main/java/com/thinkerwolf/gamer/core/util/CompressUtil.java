package com.thinkerwolf.gamer.core.util;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩工具，支持gzip和deflate
 *
 * @author wukai
 * @data 2020-03-21
 */
public class CompressUtil {

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


    public static byte[] compressGzip(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = null;
        GZIPOutputStream gzip = null;
        try {
            out = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(out);
            gzip.write(bytes);
            gzip.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(gzip);
        }
    }

    public static byte[] compressDeflate(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = null;
        DeflaterOutputStream deflater = null;
        try {
            out = new ByteArrayOutputStream();
            deflater = new DeflaterOutputStream(out);
            deflater.write(bytes);
            return out.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(deflater);
        }
    }


}
