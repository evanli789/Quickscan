package com.evan.quickscan.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.BoundingBox;
import com.brother.sdk.common.IConnector;
import com.brother.sdk.common.device.MediaSize;
import com.brother.sdk.scan.ScanJob;
import com.brother.sdk.scan.ScanJobController;
import com.brother.sdk.scan.ScanParameters;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.evan.quickscan.util.Constants;
import com.evan.quickscan.R;
import com.evan.quickscan.adapters.DetectedTextAdapter;
import com.evan.quickscan.util.ExifUtil;
import com.evan.quickscan.util.GlideEngine;
import com.evan.quickscan.util.KeyboardUtils;
import com.evan.quickscan.util.ScannerManager;
import com.evan.quickscan.util.SnackbarManager;
import com.evan.quickscan.util.SpacingUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

@RuntimePermissions
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements
        DetectedTextAdapter.DetectedTextAdapterCallback {

    @BindView(R.id.iv_upload_image)
    ImageView ivUploadImage;

    @BindView(R.id.btn_start)
    Button btnStart;

    @BindView(R.id.parent_layout)
    ViewGroup parentLayout;

    @BindView(R.id.btn_add_key)
    Button btnAddKey;

    @BindView(R.id.btn_edit_key)
    ImageView btnEditKey;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.layout_detected_text)
    ViewGroup layoutDetectedText;

    @BindView(R.id.checkbox_numbers_only)
    CheckBox checkBoxNumbersOnly;

    @BindView(R.id.et_saved_text)
    EditText etSavedText;

    private MainViewModel       mainViewModel;
    private Paint               paint;
    private AlertDialog         progressDialog;
    private DetectedTextAdapter detectedTextAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(this, R.color.standardRed));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getLiveData().observe(this, response -> {
            switch (response.getMainActivityStatus()) {
                case SHOW_PROGRESS:
                    showProgressDialog();
                    break;

                case HIDE_PROGRESS:
                    hideProgressDialog();
                    break;

                case CALL_TEXTRACT_SUCCESS:
                    //Prevents image shrinking when reloading
                    ivUploadImage.layout(0, 0, 0, 0);
                    Glide.with(this)
                            .asBitmap()
                            .load(drawBoxesAroundText(
                                    mainViewModel.getImagePath(),
                                    response.getBlockList()
                            ))
                            .apply(RequestOptions.fitCenterTransform())
                            .into(ivUploadImage);

                    detectedTextAdapter.notifyDataSetChanged();

                    layoutDetectedText.setVisibility(View.VISIBLE);
                    layoutDetectedText.getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    //amount of padding to add to bottom of imageview
                                    int height = layoutDetectedText.getHeight();
                                    Timber.d("Height: " + height);
                                    parentLayout.setPadding(
                                            0,
                                            0,
                                            0,
                                            height + SpacingUtils.convertIntToDP(MainActivity.this, 16)
                                    );
                                    parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            });
                    break;

                case SUCCESS_SAVE_TX:
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    layoutDetectedText.setVisibility(View.GONE);
                    ivUploadImage.setImageResource(0);
                    btnStart.setVisibility(View.GONE);
                    etSavedText.getText().clear();
                    checkBoxNumbersOnly.setChecked(false);
                    detectedTextAdapter.notifyDataSetChanged();
                    KeyboardUtils.hideKeyboard(etSavedText);
                    break;

                case SET_EDIT_TEXT:
                    etSavedText.setText(response.getMessage());
                    break;

                case KEYS_ADDED:
                    btnAddKey.setVisibility(View.GONE);
                    btnEditKey.setVisibility(View.VISIBLE);
                    break;

                case NO_KEYS_ADDED:
                    btnAddKey.setVisibility(View.VISIBLE);
                    btnEditKey.setVisibility(View.GONE);
                    break;

                case ON_CHECK:

                case ON_UNCHECK:
                    detectedTextAdapter.notifyDataSetChanged();
                    break;

                case CALL_TEXTRACT_FAILED:

                case ERROR:
                    SnackbarManager.createExpirable(parentLayout, response.getMessage()).show();
                    break;
            }
        });

        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getKeysAddedStateLiveData().observe(this, keysAdded -> {
            if (keysAdded) {
                btnAddKey.setVisibility(View.GONE);
                btnEditKey.setVisibility(View.VISIBLE);
            } else {
                btnAddKey.setVisibility(View.VISIBLE);
                btnEditKey.setVisibility(View.GONE);
            }
        });

        detectedTextAdapter = new DetectedTextAdapter(mainViewModel.getDisplayList(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, RecyclerView.HORIZONTAL, false)
        );
        recyclerView.setAdapter(detectedTextAdapter);

        checkBoxNumbersOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainViewModel.onChecked();
            } else {
                mainViewModel.onUnchecked();
            }
        });

        etSavedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mainViewModel.setSaveValue(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private Bitmap drawBoxesAroundText(String imagePath, List<Block> blockList) {
        Bitmap immutableBitmap = BitmapFactory.decodeFile(imagePath);
        //ExifUtil rotates to correct orientation after .copy changes it.
        Bitmap bitmap = ExifUtil.rotateBitmap(imagePath, immutableBitmap.copy(Bitmap.Config.ARGB_8888, true));
        Canvas canvas = new Canvas(bitmap);
        for (Block block : blockList) {
            if (block.getBlockType().equals("LINE")) {
                BoundingBox box = block.getGeometry().getBoundingBox();
                int newLeft = Math.round(bitmap.getWidth() * box.getLeft());
                int newTop = Math.round(bitmap.getHeight() * box.getTop());
                int newWidth = Math.round(bitmap.getWidth() * box.getWidth());
                int newHeight = Math.round(bitmap.getHeight() * box.getHeight());
                canvas.drawRect(
                        newLeft,
                        newTop,
                        newWidth + newLeft,
                        newHeight + newTop,
                        paint
                );
            }
        }
        return bitmap;
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(
                this, requestCode, grantResults
        );
    }

    private void initAddDialog() {
        DialogFragment dialogAddKey = new DialogAddKey();
        dialogAddKey.show(getSupportFragmentManager(), Constants.DIALOG_ADD_KEY);
    }

    @OnClick(R.id.btn_edit_key)
    void onEditKey() {
        initAddDialog();
    }

    @OnClick(R.id.btn_add_key)
    void onAddKey() {
        initAddDialog();
    }

    @OnClick(R.id.btn_scan_item)
    void onScanItem() {
        new Thread(() -> {
            ScannerManager.connect(ScannerManager.CONNECTION.WIFI, getApplicationContext());
            runOnUiThread(this::showConnectionProgress);

            while (!ScannerManager.isConnected()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Still Connecting...");
            }
            System.out.println("Connected");
            System.out.println("Attempting scan...");

            runOnUiThread(this::hideProgressDialog);
            executeImageScan(ScannerManager.createIConnector(getApplicationContext()));
        }).start();
    }

    public void executeImageScan(IConnector connector) {
        ScanJob scanJob = null;
        try {
            ScanParameters scanParameters = new ScanParameters();
            scanParameters.documentSize = MediaSize.Letter;
            scanParameters.autoDocumentSizeScan = true;
            scanJob = new ScanJob(scanParameters, this, new ScanJobController(this.getFilesDir()) {
                public void onUpdateProcessProgress(int value) {
                }

                public void onNotifyProcessAlive() {
                }

                public void onImageReadToFile(String scannedImagePath, int pageIndex) {
                    mainViewModel.setImagePath(scannedImagePath);

                    runOnUiThread(() -> {
                        btnStart.setVisibility(View.VISIBLE);
                        Glide.with(MainActivity.this)
                                .load(scannedImagePath)
                                .apply(RequestOptions.fitCenterTransform())
                                .into(ivUploadImage);
                    });
                }
            });
            connector.submit(scanJob);
        } catch (Exception e) {
            Timber.e("Error scanning: " + e);
            scanJob.cancel();
        }
    }

    @OnClick(R.id.btn_select_photo)
    void onSelectPhoto() {
        MainActivityPermissionsDispatcher.selectPhotoWithPermissionCheck(this);
    }

    @OnClick(R.id.btn_start)
    void onStartTextract() {
        mainViewModel.callTextract();
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void selectPhoto() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .isDisplayCamera(false)
                .forResult(Constants.REQUEST_CODE_SELECT_PHOTO);
    }

    @OnClick(R.id.btn_clear)
    void onClear() {
        mainViewModel.onClear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE_SELECT_PHOTO) {
            ArrayList<LocalMedia> list = PictureSelector.obtainSelectorList(data);
            String path = list.get(0).getRealPath();

            if (list.get(0).getMimeType().equals("image/webp")) {
                SnackbarManager.createExpirable(
                        parentLayout, "Textract does not work with webp"
                ).show();
            }

            mainViewModel.setImagePath(path);
            btnStart.setVisibility(View.VISIBLE);

            //Prevents image shrinking when reloading
            ivUploadImage.layout(0, 0, 0, 0);
            Glide.with(MainActivity.this)
                    .load(path)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(ivUploadImage);
        }
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onPermissionDenied() {
        Timber.e("Permission denied");
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onPermissionNeverAskAgain() {
        Toast.makeText(
                this,
                "Must enable read storage permissions in settings",
                Toast.LENGTH_LONG
        ).show();
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialogTheme);
        builder.setView(R.layout.progress_dialog_upload);
        progressDialog = builder.create();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void showConnectionProgress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialogTheme);
        builder.setView(R.layout.progress_dialog_connection);
        progressDialog = builder.create();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void onTextItemClicked(int position) {
        mainViewModel.onTextItemClicked(position);
    }

    @OnClick(R.id.btn_save)
    void onSave() {
        //Dismiss
        mainViewModel.onSave();
    }

    @OnClick(R.id.btn_scan_history)
    void onViewScanHistory() {
        DialogFragment scanHistory = new TransactionsFragment();
        scanHistory.show(getSupportFragmentManager(), Constants.FRAGMENT_SCAN_HISTORY);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}