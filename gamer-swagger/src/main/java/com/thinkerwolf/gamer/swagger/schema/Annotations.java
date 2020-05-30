package com.thinkerwolf.gamer.swagger.schema;

import org.apache.commons.lang.StringUtils;

public final class Annotations {

    public static boolean isBlank(String[] tags) {
        if (tags == null) {
            return true;
        }
        if (tags.length == 1 && StringUtils.isEmpty(tags[0])) {
            return true;
        }
        return false;
    }

}
