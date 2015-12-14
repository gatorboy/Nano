/*
 * Copyright 2015 salesforce.com.
 * All Rights Reserved.
 * Company Confidential.
 */
package com.smenedi.nano;

import org.json.JSONArray;
import org.json.JSONObject;


public class JSONUtil {

    /**
     * The separators is the "." symbol. We need to escape the "." since String.split treats that as special character.
     */
    private static final String PATH_SEPERATOR = "\\.";


    /**
     * Get the JSONArray from the json specified by the path
     *
     * @param from
     *         A {@link JSONObject} to start from
     * @param path
     *         A path to traverse
     *
     * @return A {@link JSONArray} at the end of the path.
     */
    public static JSONArray optArrayFromPath(JSONObject from, final String path) {
        JSONArray ret = null;
        if (path != null) {
            String[] pathComponents = path.split(PATH_SEPERATOR);
            from = traversePath(from, pathComponents);
            if (from != null) {
                final String field = pathComponents[pathComponents.length - 1];
                if (canGet(from, field)) {
                    ret = from.optJSONArray(field);
                }
            }
        }
        return (ret == null) ? new JSONArray() : ret;
    }



    private static JSONObject traversePath(final JSONObject from, final String[] pathComponents) {
        JSONObject cur = from;
        final int end = pathComponents.length - 1;
        for (int i = 0; i < end; ++i) {
            final String field = pathComponents[i];
            if (!canGet(cur, field)) {
                cur = null;
                break;
            }
            cur = cur.optJSONObject(field);
        }
        return cur;
    }

    /**
     * Checks if field exists in a {@link JSONObject} and if it is not
     * {@code null}.
     *
     * @param from
     *         A {@link JSONObject} instance.
     * @param field
     *         A field name within the from {@link JSONObject}
     *         object.
     *
     * @return true if the field exits and is not null
     */
    private static boolean canGet(final JSONObject from, final String field) {
        return from.has(field) && !from.isNull(field);
    }

}
