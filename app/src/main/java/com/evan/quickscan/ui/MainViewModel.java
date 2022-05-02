package com.evan.quickscan.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amazonaws.services.textract.model.Block;
import com.evan.quickscan.domain.LocalRepository;
import com.evan.quickscan.domain.RemoteRepository;
import com.evan.quickscan.domain.SharedPrefManager;
import com.evan.quickscan.domain.model.MainViewModelResponse;
import com.evan.quickscan.domain.model.Transaction;
import com.evan.quickscan.util.MainActivityStatus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final RemoteRepository                       remoteRepository;
    private final SharedPrefManager                      sharedPrefManager;
    private final LocalRepository                        localRepository;
    private final MutableLiveData<MainViewModelResponse> liveData            = new MutableLiveData<>();
    private final List<String>                           displayList         = new ArrayList<>();
    private final List<String>                           numericsOnlyList    = new ArrayList<>();
    private final List<String>                           allDetectedTextList = new ArrayList<>();
    private       StringBuilder                          saveValue           = new StringBuilder();
    private       String                                 imagePath;

    @Inject
    public MainViewModel(
            RemoteRepository remoteRepository,
            LocalRepository localRepository,
            SharedPrefManager sharedPrefManager
    ) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;
        this.sharedPrefManager = sharedPrefManager;

        if (isKeysAdded()) {
            liveData.setValue(new MainViewModelResponse(
                    MainActivityStatus.KEYS_ADDED
            ));
        } else {
            liveData.setValue(new MainViewModelResponse(
                    MainActivityStatus.NO_KEYS_ADDED
            ));
        }
    }

    public void callTextract() {
        if (!isKeysAdded()) {
            liveData.setValue(MainViewModelResponse.error(
                    MainActivityStatus.ERROR,
                    "Must add AWS Keys to call service"
            ));
            return;
        }

        if (imagePath == null) {
            liveData.setValue(new MainViewModelResponse(
                    MainActivityStatus.ERROR,
                    "Please select an image"
            ));
            return;
        }

        liveData.setValue(MainViewModelResponse.setProgressBarStatus(
                MainActivityStatus.SHOW_PROGRESS
        ));

        String accessKey = sharedPrefManager.getAccessKey();
        String secretKey = sharedPrefManager.getSecretKey();

        Single.fromCallable(() -> remoteRepository.callTextract(accessKey, secretKey, imagePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detectDocumentTextResult -> {
                    liveData.setValue(MainViewModelResponse.setProgressBarStatus(
                            MainActivityStatus.HIDE_PROGRESS
                    ));

                    List<Block> blocks = detectDocumentTextResult.getBlocks();
                    for (Block b : blocks) {
                        String s = b.getText();
                        if (s == null) {
                            continue;
                        }
                        allDetectedTextList.add(s);

                        if (isNumeric(s)) {
                            numericsOnlyList.add(s);
                        }
                    }
                    displayList.addAll(allDetectedTextList);

                    liveData.setValue(MainViewModelResponse.successTextractCall(
                            MainActivityStatus.CALL_TEXTRACT_SUCCESS,
                            blocks,
                            "Success analyzing image"
                    ));

                }, err -> {
                    Timber.d("Textract call failed: " + err);
                    liveData.setValue(MainViewModelResponse.setProgressBarStatus(
                            MainActivityStatus.HIDE_PROGRESS
                    ));

                    liveData.setValue(MainViewModelResponse.error(
                            MainActivityStatus.CALL_TEXTRACT_FAILED,
                            err.getMessage()
                    ));

                });

    }

    private static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public MutableLiveData<MainViewModelResponse> getLiveData() {
        return liveData;
    }

    public List<String> getDisplayList() {
        return displayList;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    private boolean isKeysAdded() {
        String accessKey = sharedPrefManager.getAccessKey();
        String secretKey = sharedPrefManager.getSecretKey();
        return accessKey != null && secretKey != null;
    }

    //Display only numeric values
    public void onChecked() {
        liveData.setValue(new MainViewModelResponse(MainActivityStatus.ON_CHECK));
        displayList.clear();
        displayList.addAll(numericsOnlyList);
    }

    //All detected text
    public void onUnchecked() {
        liveData.setValue(new MainViewModelResponse(MainActivityStatus.ON_UNCHECK));
        displayList.clear();
        displayList.addAll(allDetectedTextList);
    }

    public void onTextItemClicked(int position) {
        saveValue.append(" ").append(displayList.get(position));
        liveData.setValue(MainViewModelResponse.setEditText(
                MainActivityStatus.SET_EDIT_TEXT, saveValue.toString()
        ));
    }

    public void setSaveValue(CharSequence s) {
        saveValue.setLength(0);
        this.saveValue = saveValue.append(s);
    }

    public void onClear() {
        saveValue.setLength(0);
        liveData.setValue(MainViewModelResponse.setEditText(
                MainActivityStatus.SET_EDIT_TEXT, saveValue.toString()
        ));
    }

    public void onSave() {
        if (saveValue.length() < 1) {
            return;
        }

        localRepository.insertTransaction(new Transaction(saveValue.toString(), System.currentTimeMillis()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    Timber.d("new tx id: " + id);
                    saveValue.setLength(0);
                    liveData.setValue(new MainViewModelResponse(MainActivityStatus.SUCCESS_SAVE_TX));
                    displayList.clear();
                    allDetectedTextList.clear();
                    numericsOnlyList.clear();
                }, err -> {
                    Timber.e("err fetching txs: " + err);
                    liveData.setValue(MainViewModelResponse.error(
                            MainActivityStatus.ERROR, err.getMessage()
                    ));
                });
    }
}
