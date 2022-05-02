package com.evan.quickscan.domain.model;

import com.evan.quickscan.util.AddKeyStatus;

public class AddKeyResponse {
    private final AddKeyStatus addKeyStatus;
    private       String       accessKey, secretKey;
    private       String       message;


    public AddKeyResponse(AddKeyStatus addKeyStatus, String message) {
        this.addKeyStatus = addKeyStatus;
        this.message = message;
    }

    public AddKeyResponse(AddKeyStatus addKeyStatus) {
        this.addKeyStatus = addKeyStatus;
    }

    public AddKeyResponse(AddKeyStatus addKeyStatus, String accessKey, String secretKey) {
        this.addKeyStatus = addKeyStatus;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public AddKeyStatus getAddKeyStatus() {
        return addKeyStatus;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getMessage() {
        return message;
    }
}
