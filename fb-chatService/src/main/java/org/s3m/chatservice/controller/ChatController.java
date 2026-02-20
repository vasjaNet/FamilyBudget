package org.s3m.chatservice.controller;

import org.s3m.commonlib.config.ApiResponse;
import org.s3m.commonlib.util.AppConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller for chat service demonstrating use of commons-lib
 */
@RestController
@RequestMapping(AppConstants.API_PREFIX + "/chats")
public class ChatController {

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success(
            "Chat Service is healthy",
            AppConstants.CHAT_SERVICE + " v1.0.0"
        );
    }

    @GetMapping("/info")
    public ApiResponse<Object> getServiceInfo() {
        return ApiResponse.success(new Object() {
            public String serviceName = AppConstants.CHAT_SERVICE;
            public String apiVersion = AppConstants.API_VERSION;
            public String status = "Running";
        });
    }
}

