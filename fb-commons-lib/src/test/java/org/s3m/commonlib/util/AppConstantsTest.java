package org.s3m.commonlib.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppConstantsTest {

    @Test
    void testApiVersion() {
        assertEquals("v1", AppConstants.API_VERSION);
    }

    @Test
    void testApiPrefix() {
        assertEquals("/api/v1", AppConstants.API_PREFIX);
    }

}

