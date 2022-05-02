package com.evan.quickscan.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "date_created")
    private long dateCreated;

    public Transaction(String message, long dateCreated) {
        this.message = message;
        this.dateCreated = dateCreated;
    }

    @Ignore
    private String formattedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", dateCreated=" + dateCreated +
                ", formattedDate='" + formattedDate + '\'' +
                '}';
    }
}

