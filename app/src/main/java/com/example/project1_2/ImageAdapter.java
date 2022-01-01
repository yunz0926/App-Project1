package com.example.project1_2;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private int displayWidth;
    private int size;
    private int pad;

    private Context mContext;

    public static Object[] mThumblds = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7,
            R.drawable.image8,
            R.drawable.image9,
            R.drawable.image10,
            R.drawable.image11,
            R.drawable.image12,
            R.drawable.image13,
            R.drawable.image14,
            R.drawable.image15,
            R.drawable.image16,
    };
    public ImageAdapter(Context c, int displayWidth) {
        mContext = c;
        this.displayWidth = displayWidth;
        size = displayWidth/3;
        pad = 10;
    }


    public int getCount() {
        return mThumblds.length;
    }

    public Object getItem(int pos){
        return mThumblds[pos];
    }

    public long getItemId(int position) {
        return 0;
    }

    public void addItem(Object resource){
        List<Object> mThumbldsList = new ArrayList<Object>(Arrays.asList(mThumblds.clone()));
        mThumbldsList.add(resource);
        mThumblds = mThumbldsList.toArray(new Object[0]);
    }

    public void deleteItem(int pos) {
        List<Object> mThumbldsList = new ArrayList<Object>(Arrays.asList(mThumblds.clone()));
        mThumbldsList.remove(pos);
        mThumblds = mThumbldsList.toArray(new Object[0]);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView==null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(pad, 0, pad, 0);
        }
        else{
            imageView = (ImageView) convertView;
        }
        if(mThumblds[position] instanceof Integer){
            imageView.setImageResource((Integer)mThumblds[position]);
        }
        else if(mThumblds[position] instanceof Uri){
            imageView.setImageURI((Uri)mThumblds[position]);
        }
        return imageView;
    }
}
