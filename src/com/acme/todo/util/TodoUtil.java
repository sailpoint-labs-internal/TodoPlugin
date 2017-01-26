
package com.acme.todo.util;

import java.util.Date;

/**
 * Utility class for the plugin.
 *
 * @author Dustin Dobervich <dustin.dobervich@sailpoint.com>
 */
public class TodoUtil {

    /**
     * The plugin name.
     */
    public static final String PLUGIN_NAME = "TodoPlugin";

    /**
     * Timestamp for this moment in time.
     *
     * @return The timestamp.
     */
    public static long now() {
        return new Date().getTime();
    }

}

