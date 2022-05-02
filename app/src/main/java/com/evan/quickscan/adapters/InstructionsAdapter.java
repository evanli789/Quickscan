package com.evan.quickscan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.evan.quickscan.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.ViewHolder> {

    public interface InstructionsAdapterCallback {
        void onImageClick(int position);
    }

    private final InstructionsAdapterCallback callback;
    private final int[]                       list;

    public InstructionsAdapter(int[] list, InstructionsAdapterCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_instruction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Glide.with(holder.imageView.getContext())
                .load(list[i])
                .apply(RequestOptions.fitCenterTransform())
                .into(holder.imageView);

        String instructionNum = "Step " + (i + 1) + ":";
        holder.tvInstructionNum.setText(instructionNum);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.tv_instruction_num)
        TextView tvInstructionNum;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.onImageClick(getAdapterPosition());
        }
    }

}
