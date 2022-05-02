package com.evan.quickscan.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.evan.quickscan.util.Constants;
import com.evan.quickscan.R;
import com.evan.quickscan.util.KeyboardUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class DialogAddKey extends DialogFragment {

    private Context context;

    @BindView(R.id.et_access_key)
    TextInputEditText etAccessKey;

    @BindView(R.id.til_access_key)
    TextInputLayout tilAccessKey;

    @BindView(R.id.til_secret_key)
    TextInputLayout tilSecretKey;

    @BindView(R.id.et_secret_key)
    TextInputEditText etSecretKey;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    private AddKeyViewModel viewModel;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_key, container, false);
        ButterKnife.bind(this, view);

        tilAccessKey.setEndIconOnClickListener(icon -> {
            viewModel.onScanAccessKeyQRClick();
            initScanner();
        });

        tilSecretKey.setEndIconOnClickListener(icon -> {
            viewModel.onScanSecretKeyQRClick();
            initScanner();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel sharedViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(SharedViewModel.class);

        viewModel = new ViewModelProvider(this).get(AddKeyViewModel.class);
        viewModel.getLiveData().observe(this, addKeyResponse -> {
            switch (addKeyResponse.getAddKeyStatus()) {
                case SUCCESS_SAVE:
                    sharedViewModel.keysAdded();
                    KeyboardUtils.hideKeyboard(etAccessKey);
                    this.dismiss();
                    break;
                case ON_FRAGMENT_INIT:
                    etAccessKey.setText(addKeyResponse.getAccessKey());
                    etSecretKey.setText(addKeyResponse.getSecretKey());
                    break;
                case ON_ACCESS_KEY_SCAN:
                    etAccessKey.setText(addKeyResponse.getMessage());
                    break;
                case ON_SECRET_KEY_SCAN:
                    etSecretKey.setText(addKeyResponse.getMessage());
                    break;
                case INVALID_INPUT:
                    Toast.makeText(context, addKeyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case ON_CLEAR:
                    sharedViewModel.keysCleared();
                    etSecretKey.getText().clear();
                    etAccessKey.getText().clear();
                    break;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            try {
                String scannedQrString = result.getContents();
                viewModel.onScanResult(scannedQrString);
            } catch (Exception e) {
                Timber.e("Failed to retrieve qr, " + e);
            }
        }
    }

    @OnClick(R.id.btn_help)
    void onHelp() {
        DialogFragment instructionsFragment = new InstructionsFragment();
        instructionsFragment.show(getParentFragmentManager(), Constants.FRAGMENT_INSTRUCTIONS);
    }

    private void initScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan image qr");
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        String accessKey = etAccessKey.getText().toString().trim();
        String secretKey = etSecretKey.getText().toString().trim();
        viewModel.onSaveBtnClick(accessKey, secretKey);
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        this.dismiss();
    }

    @OnClick(R.id.btn_clear)
    void onClear(){
        viewModel.onClearBtnClick();
    }
}
