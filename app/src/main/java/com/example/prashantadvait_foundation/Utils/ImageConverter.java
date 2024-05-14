package com.example.prashantadvait_foundation.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageConverter extends AsyncTask<List<String>, Void, List<Bitmap>> {

    private ImageConversionListener listener;

    public ImageConverter(ImageConversionListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Bitmap> doInBackground(List<String>... urlsList) {
        List<String> urls = urlsList[0];
        List<Bitmap> bitmaps = new ArrayList<>();

        for (String url : urls) {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmaps.add(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmaps;
    }

    @Override
    protected void onPostExecute(List<Bitmap> bitmaps) {
        if (listener != null) {
            listener.onConversionComplete(bitmaps);
        }
    }

    public interface ImageConversionListener {
        void onConversionComplete(List<Bitmap> bitmaps);
    }
}

