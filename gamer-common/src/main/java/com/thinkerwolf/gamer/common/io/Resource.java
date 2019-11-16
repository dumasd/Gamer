package com.thinkerwolf.gamer.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 * class path
 * file path
 * url path
 * </pre>
 *
 * @author wukai
 */
public interface Resource {

    boolean exists();

    InputStream getInputStream() throws IOException;

    String getPath();
    
    String getRealPath();
}
