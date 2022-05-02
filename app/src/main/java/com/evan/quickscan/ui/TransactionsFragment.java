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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evan.quickscan.R;
import com.evan.quickscan.adapters.TransactionsAdapter;
import com.evan.quickscan.util.SnackbarManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TransactionsFragment extends DialogFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.parent_layout)
    ViewGroup parentLayout;

    private Context               context;
    private TransactionsViewModel viewModel;
    private TransactionsAdapter   transactionsAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

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
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);
        viewModel.getLiveData().observe(this, txViewModelResponse -> {
            switch (txViewModelResponse.getTxStatus()) {
                case SUCCESS_GET_TXS:
                    transactionsAdapter.notifyDataSetChanged();
                    break;

                case ERROR:
                    SnackbarManager.createExpirable(parentLayout, txViewModelResponse.getMsg());
                    break;
            }
        });

        transactionsAdapter = new TransactionsAdapter(viewModel.getListTransactions());
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(transactionsAdapter);
    }

    @OnClick(R.id.btn_back)
    void onBackBtnClick() {
        this.dismiss();
    }
}
