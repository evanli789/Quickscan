package com.evan.quickscan.ui;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.evan.quickscan.R;
import com.github.chrisbanes.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZoomableImageFragment extends DialogFragment {

    @BindView(R.id.image_view)
    PhotoView imageView;

    private final int drawable;

    public ZoomableImageFragment(int drawable) {
        this.drawable = drawable;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_QuickScan);
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zoomable_image, container, false);
        ButterKnife.bind(this, view);

        Glide.with(this)
                .load(drawable)
                .apply(RequestOptions.centerInsideTransform())
                .into(imageView);

        return view;
    }

    @OnClick(R.id.btn_back)
    public void onBackBtnClick() {
        this.dismiss();
    }
}
