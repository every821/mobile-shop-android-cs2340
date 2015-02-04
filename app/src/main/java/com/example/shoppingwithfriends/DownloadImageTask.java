package com.example.shoppingwithfriends;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.HashMap;

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    String key;
    public static HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();


    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    public DownloadImageTask(String key) {
        this.key = key;
    }
    public DownloadImageTask(String key, ImageView bmImage) {
        this.key = key;
        this.bmImage = bmImage;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bmp = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bmp = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (key!=null) {
            images.put(key, result);
        }
        if (bmImage != null) {
            bmImage.setImageBitmap(result);
            bmImage.requestLayout();
        }
    }
}
