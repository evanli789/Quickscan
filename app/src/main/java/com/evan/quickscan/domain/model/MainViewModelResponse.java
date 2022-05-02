package com.evan.quickscan.domain.model;

import com.amazonaws.services.textract.model.Block;
import com.evan.quickscan.util.MainActivityStatus;

import java.util.List;

public class MainViewModelResponse {
    private final MainActivityStatus status;
    private       String             message;
    private       List<Block>        blockList;

    public MainViewModelResponse(
            MainActivityStatus status,
            List<Block> blockList,
            String message
    ) {
        this.status = status;
        this.blockList = blockList;
        this.message = message;
    }

    public MainViewModelResponse(MainActivityStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public MainViewModelResponse(MainActivityStatus status) {
        this.status = status;
    }

    public static MainViewModelResponse successTextractCall(
            MainActivityStatus status, List<Block> blockList, String message
    ) {
        return new MainViewModelResponse(status, blockList, message);
    }

    public static MainViewModelResponse error(MainActivityStatus status, String message) {
        return new MainViewModelResponse(status, message);
    }

    public static MainViewModelResponse setProgressBarStatus(MainActivityStatus status) {
        return new MainViewModelResponse(status);
    }

    public static MainViewModelResponse setEditText(MainActivityStatus status, String text) {
        return new MainViewModelResponse(status, text);
    }

    public MainActivityStatus getMainActivityStatus() {
        return status;
    }

    public List<Block> getBlockList() {
        return blockList;
    }

    public String getMessage() {
        return message;
    }
}
