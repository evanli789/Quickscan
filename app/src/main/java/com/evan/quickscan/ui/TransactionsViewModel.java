package com.evan.quickscan.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evan.quickscan.util.Constants;
import com.evan.quickscan.domain.LocalRepository;
import com.evan.quickscan.domain.model.Transaction;
import com.evan.quickscan.domain.model.TxViewModelResponse;
import com.evan.quickscan.util.TxStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class TransactionsViewModel extends ViewModel {
    private final List<Transaction>                    listTransactions = new ArrayList<>();
    private final MutableLiveData<TxViewModelResponse> liveData         = new MutableLiveData<>();


    @Inject
    public TransactionsViewModel(LocalRepository localRepository) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_PATTERN, Locale.US);
        localRepository.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(txs -> {
                    for (Transaction tx : txs) {
                        String date = dateFormat.format(tx.getDateCreated());
                        Transaction transaction = new Transaction(tx.getMessage(), tx.getDateCreated());
                        transaction.setFormattedDate(date);
                        listTransactions.add(transaction);
                    }
                    liveData.setValue(TxViewModelResponse.getTxsSuccess(
                            TxStatus.SUCCESS_GET_TXS, listTransactions
                    ));
                }, err -> {
                    Timber.e("err fetching txs: " + err);
                    liveData.setValue(TxViewModelResponse.onError(
                            TxStatus.ERROR, err.getMessage()
                    ));
                });

    }

    public MutableLiveData<TxViewModelResponse> getLiveData() {
        return liveData;
    }

    public List<Transaction> getListTransactions() {
        return listTransactions;
    }
}
