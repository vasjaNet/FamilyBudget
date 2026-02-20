package org.s3m.commonlib.util;

import lombok.experimental.UtilityClass;

/**
 * Common constants for the application
 */
@UtilityClass
public class AppConstants {

    public static final String API_VERSION = "v1";
    public static final String API_PREFIX = "/api/" + API_VERSION;

    // Service names
    public static final String USER_SERVICE = "userService";
    public static final String CHAT_SERVICE = "chatService";

    // Common headers
    public static final String X_TRACE_ID = "X-Trace-ID";
    public static final String X_REQUEST_ID = "X-Request-ID";

    // Common response messages
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

}

