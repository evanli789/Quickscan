package com.evan.quickscan.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evan.quickscan.R;
import com.evan.quickscan.adapters.InstructionsAdapter;
import com.evan.quickscan.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstructionsFragment extends DialogFragment implements InstructionsAdapter.InstructionsAdapterCallback {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private final int[] arrInstructions = {
            R.drawable.aws_0,
            R.drawable.aws_1,
            R.drawable.aws_2,
            R.drawable.aws_3,
            R.drawable.aws_4,
            R.drawable.aws_5,
            R.drawable.aws_6,
            R.drawable.aws_7,
            R.drawable.aws_8
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_QuickScan);
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.FragmentDialogAnim;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructions, container, false);
        ButterKnife.bind(this, view);

        InstructionsAdapter adapter = new InstructionsAdapter(arrInstructions, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @OnClick(R.id.btn_back)
    void onBackBtnClick() {
        this.dismiss();
    }

    @Override
    public void onImageClick(int position) {
        DialogFragment fragment = new ZoomableImageFragment(arrInstructions[position]);
        fragment.show(getParentFragmentManager(), Constants.FRAGMENT_ZOOMABLE_IMAGE);
    }
}
