package com.zomato.photofilters.imageprocessors.subfilters;

import android.content.Context;
import android.graphics.Bitmap;
import com.zomato.photofilters.imageprocessors.BitmapUtils;
import com.zomato.photofilters.imageprocessors.ImageProcessor;
import com.zomato.photofilters.imageprocessors.SubFilter;


/**
 * @author varun
 * Subfilter used to overlay bitmap with the color defined
 */
public class DogFilter implements SubFilter {
    private static String tag = "";
    public static final String IMAGE_NAME = "dogFilter.jpg";
    private Context context;

    public DogFilter(Context context) {
        this.context = context;
    }

    @Override
    public Bitmap process(Bitmap inputImage) {
        return BitmapUtils.getBitmapFromAssets(context, IMAGE_NAME, 300, 300);
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(Object tag) {
        DogFilter.tag = (String) tag;
    }
}
