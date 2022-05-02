package com.evan.quickscan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evan.quickscan.R;
import com.evan.quickscan.domain.model.Transaction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private final List<Transaction> list;

    public TransactionsAdapter(List<Transaction> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        String itemNumber = i + 1 + ". ";
        holder.tvTransactionNum.setText(itemNumber);
        holder.tvTransactionName.setText(list.get(i).getMessage());
        holder.tvDateCreated.setText(list.get(i).getFormattedDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_transaction_num)
        TextView tvTransactionNum;

        @BindView(R.id.tv_transaction_name)
        TextView tvTransactionName;

        @BindView(R.id.tv_date_created)
        TextView tvDateCreated;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
