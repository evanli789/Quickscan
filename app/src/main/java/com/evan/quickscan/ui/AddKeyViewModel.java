package com.evan.quickscan.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evan.quickscan.domain.model.AddKeyResponse;
import com.evan.quickscan.domain.SharedPrefManager;
import com.evan.quickscan.util.AddKeyStatus;
import com.evan.quickscan.util.QRType;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddKeyViewModel extends ViewModel {

    private final SharedPrefManager               sharedPrefManager;
    private final MutableLiveData<AddKeyResponse> liveData = new MutableLiveData<>();
    private QRType currentQRType;

    @Inject
    public AddKeyViewModel(SharedPrefManager sharedPrefManager) {
        this.sharedPrefManager = sharedPrefManager;
        fetchKeys();
    }

    private void fetchKeys() {
        String accessKey = sharedPrefManager.getAccessKey();
        String secretKey = sharedPrefManager.getSecretKey();
        if(accessKey == null || secretKey == null){
            return;
        }

        liveData.postValue(
                new AddKeyResponse(AddKeyStatus.ON_FRAGMENT_INIT, accessKey, secretKey)
        );
    }

    public void onSaveBtnClick(String accessKey, String secretKey) {
        if (accessKey.equals("") || secretKey.equals("")) {

            liveData.postValue(
                    new AddKeyResponse(AddKeyStatus.INVALID_INPUT, "Must add both keys")
            );
            return;
        }

        sharedPrefManager.setAccessKey(accessKey);
        sharedPrefManager.setSecretKey(secretKey);

        liveData.postValue(new AddKeyResponse(AddKeyStatus.SUCCESS_SAVE, "Success saving keys"));
    }

    public void onClearBtnClick(){
        sharedPrefManager.clear();
        liveData.setValue(new AddKeyResponse(AddKeyStatus.ON_CLEAR));
    }

    public MutableLiveData<AddKeyResponse> getLiveData() {
        return liveData;
    }

    public void onScanAccessKeyQRClick(){
        currentQRType = QRType.ACCESS_KEY;
    }

    public void onScanSecretKeyQRClick(){
        currentQRType = QRType.SECRET_KEY;
    }

    public void onScanResult(String scannedQrString) {
        if(currentQRType == QRType.ACCESS_KEY){
            liveData.setValue(new AddKeyResponse(AddKeyStatus.ON_ACCESS_KEY_SCAN, scannedQrString));
        }else{
            liveData.setValue(new AddKeyResponse(AddKeyStatus.ON_SECRET_KEY_SCAN, scannedQrString));
        }
    }
}
