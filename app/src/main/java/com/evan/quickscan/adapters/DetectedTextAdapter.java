package com.evan.quickscan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.services.textract.model.Block;
import com.evan.quickscan.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetectedTextAdapter extends RecyclerView.Adapter<DetectedTextAdapter.ViewHolder> {

    public interface DetectedTextAdapterCallback{
        void onTextItemClicked(int position);
    }
    private final List<String> blockList;
    private final DetectedTextAdapterCallback callback;

    public DetectedTextAdapter(List<String> blockList, DetectedTextAdapterCallback callback) {
        this.blockList = blockList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_detected_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvText.setText(blockList.get(position));
    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_text)
        TextView tvText;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.onTextItemClicked(getAdapterPosition());
        }
    }
}
