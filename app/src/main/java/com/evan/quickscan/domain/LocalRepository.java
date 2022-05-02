package com.evan.quickscan.domain;

import com.evan.quickscan.domain.model.Transaction;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class LocalRepository {
    private final AppDatabase appDatabase;

    @Inject
    public LocalRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public Single<List<Transaction>> getTransactions(){
        return appDatabase.transactionDao().getTransactions();
    }

    public Single<Long> insertTransaction(Transaction tx){
        return appDatabase.transactionDao().insertTx(tx);
    }
}
