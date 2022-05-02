package com.evan.quickscan.domain.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface TransactionDao {

    //Room inserts can only return Long
    @Insert
    Single<Long> insertTx(Transaction tx);

    @Query("SELECT * FROM transactions ORDER BY id ASC")
    Single<List<Transaction>> getTransactions();
}
