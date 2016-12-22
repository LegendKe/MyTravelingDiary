package com.ruihai.xingka.ui.caption.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ruihai.xingka.R;


public class ImagePageFragment extends Fragment {

    private static final String IMAGE_PICKER_IMAGE_KEY = "image_picker_image_key";
    private static final String IMAGE_PICKER_POSITION_KEY = "image_picker_position_key";

    private ImageView ivImage;

    private int position;
    private String imageUrl;

    public static ImagePageFragment newInstance(int position, String imageUrl) {
        Bundle bundle = new Bundle();
        bundle.putInt(IMAGE_PICKER_POSITION_KEY, position);
        bundle.putString(IMAGE_PICKER_IMAGE_KEY, imageUrl);

        ImagePageFragment simplePageFragment = new ImagePageFragment();
        simplePageFragment.setArguments(bundle);

        return simplePageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(IMAGE_PICKER_POSITION_KEY, -1);
        imageUrl = getArguments().getString(IMAGE_PICKER_IMAGE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivImage = (ImageView) view.findViewById(R.id.iv_image);
        Glide.with(this)
                .load(imageUrl)
                .thumbnail(0.5f)
                .into(ivImage);
    }
}
