package com.evan.quickscan.domain.model;

import com.evan.quickscan.util.TxStatus;

import java.util.List;

public class TxViewModelResponse {

    private final TxStatus          txStatus;
    private       List<Transaction> listTxs;
    private       String            msg;

    private TxViewModelResponse(TxStatus txStatus, List<Transaction> listTxs) {
        this.txStatus = txStatus;
        this.listTxs = listTxs;
    }

    public static TxViewModelResponse getTxsSuccess(TxStatus txStatus, List<Transaction> listTxs) {
        return new TxViewModelResponse(txStatus, listTxs);
    }

    private TxViewModelResponse(TxStatus txStatus, String msg) {
        this.txStatus = txStatus;
        this.msg = msg;
    }

    public static TxViewModelResponse onError(TxStatus txStatus, String msg) {
        return new TxViewModelResponse(txStatus, msg);
    }

    public TxStatus getTxStatus() {
        return txStatus;
    }

    public List<Transaction> getListTxs() {
        return listTxs;
    }

    public String getMsg() {
        return msg;
    }
}
