package com.evan.quickscan.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> keysAddedStateLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getKeysAddedStateLiveData() {
        return keysAddedStateLiveData;
    }

    public void keysAdded(){
        keysAddedStateLiveData.setValue(true);
    }

    public void keysCleared(){
        keysAddedStateLiveData.setValue(false);
    }
}
