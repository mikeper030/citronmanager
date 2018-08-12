package com.ultimatesoftil.citron.ui.activities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.CustomTouch;


/**
 * Created by Mike on 17/07/2018.
 */

public class GalleryViewerFragment extends Fragment {
    public static boolean active=false;
    ImageView picture;
    String file;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_viewer,container,false);
        return  view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        picture=(ImageView)view.findViewById(R.id.display);
        Bundle bundle=getArguments();

        if(bundle!=null&&bundle.getString("link")!=null) {
            file = bundle.getString("link");
            active = true;
            picture.setOnTouchListener(new CustomTouch());
            Glide.with(getActivity()).load(file).placeholder(getResources().getDrawable(R.drawable.add_image)).into(picture);
        }
        else
        if(bundle.getBoolean("bitmap")==true){
            Bitmap bmp = BitmapFactory.decodeByteArray(bundle.getByteArray("link"), 0, bundle.getByteArray("link").length);
            picture.setImageBitmap(bmp);
            picture.setOnTouchListener(new CustomTouch());
        }




    }





}
