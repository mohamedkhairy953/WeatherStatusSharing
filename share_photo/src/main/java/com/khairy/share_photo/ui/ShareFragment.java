package com.khairy.share_photo.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.khairy.core.listeners.OnFragmentInteractionListener;
import com.khairy.share_photo.R;
import com.khairy.share_photo.callback.ShareHandler;
import com.khairy.share_photo.databinding.FragmentShareBinding;


import java.io.File;

import static com.khairy.navigation_module.ParameterKeysKt.WEATHER_PHOTO_EXTRA;


public class ShareFragment extends Fragment implements ShareHandler {
    private static final String IMAGE_TYPE = "image/*";
    private OnFragmentInteractionListener mListener;
    private String weatherPhoto;
    private FragmentShareBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(WEATHER_PHOTO_EXTRA)) {
            weatherPhoto = bundle.getString(WEATHER_PHOTO_EXTRA);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share, container, false);
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (weatherPhoto != null && !weatherPhoto.isEmpty()) {
            Glide.with(requireContext()).
                    load(new File(weatherPhoto)).
                    into(binding.weatherPhotoIv);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setToolbarTitle(getString(R.string.share_photo));

    }

    @Override
    public void onShareClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(IMAGE_TYPE);
        File photoFile = new File(weatherPhoto);
        Uri myPhotoFileUri = FileProvider.getUriForFile(getActivity(),
                getActivity().getApplicationContext().getPackageName()
                        + ".provider", photoFile);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri);
        startActivity(Intent.createChooser(intent, "Share with..."));
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
