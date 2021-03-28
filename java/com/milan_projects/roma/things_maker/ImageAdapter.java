package com.milan_projects.roma.things_maker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by Roma on 24.12.2017.
 */

public class ImageAdapter extends BaseAdapter {
    private int mGalleryItemBackground;
    private Context mContext;
    private final Integer[] mImage = Image_mas.mas;

    public ImageAdapter(Context сontext) {
        mContext = сontext;
    }

    @Override
    public int getCount() {
        return mImage.length;
    }

    @Override
    public Object getItem(int position) {
        return mImage[position];
    }

    @Override
    public long getItemId(int position) {
        return mImage[position];
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = new ImageView(mContext);
        view.setImageResource(mImage[position]);
        view.setPadding(20, 20, 20, 20);
        view.setLayoutParams(new Gallery.LayoutParams(250, 250));
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setBackgroundResource(mGalleryItemBackground);

        return view;
    }}
