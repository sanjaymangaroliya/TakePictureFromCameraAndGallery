package com.takepicturefromcameraandgallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.takepicturefromcameraandgallery.R;

import java.util.ArrayList;
import java.util.HashMap;

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<HashMap<String, String>> imageList;
    private DisplayImageOptions options;

    public GridViewAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        mContext = context;
        imageList = list;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_user)
                .showImageForEmptyUri(R.drawable.default_user)
                .showImageOnFail(R.drawable.default_user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = new View(mContext);
            view = inflater.inflate(R.layout.row_gridview, null);
            ImageView img_row = view.findViewById(R.id.img_row);
            String picturePath = imageList.get(position).get("image_path");
            ImageLoader.getInstance().displayImage("file://" + picturePath, img_row, options);
        } else {
            view = convertView;
        }
        return view;
    }
}