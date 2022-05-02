package com.evan.quickscan.domain;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.evan.quickscan.domain.model.Transaction;
import com.evan.quickscan.domain.model.TransactionDao;

@Database(entities = {Transaction.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TransactionDao transactionDao();

}
