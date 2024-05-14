package com.example.prashantadvait_foundation.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.util.Log;
import com.jakewharton.disklrucache.DiskLruCache;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ImageCache {

    private static final String TAG = "ImageCache";
    private static final int MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8; // 1/8th of available memory
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final String CACHE_DIR = "image_cache";

    private static final String CACHE_KEY_PREFIX = "image_";

    private LruCache<String, Bitmap> memoryCache;
    private DiskLruCache diskCache;
    private Context context;

    public ImageCache(Context context) {
        this.context = context;

        memoryCache = new LruCache<String, Bitmap>(MEMORY_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // Size of the bitmap in kilobytes
            }
        };

        try {
            File cacheDir = getDiskCacheDir(context, CACHE_DIR);
            diskCache = DiskLruCache.open(cacheDir, APP_VERSION, VALUE_COUNT, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error opening disk cache: " + e.getMessage());
        }
    }

    public void cacheImages(List<String> urls) {
        Log.d(TAG, "Caching images...");
        for (String url : urls) {
            Log.d(TAG, "Caching image from URL: " + url);
            new CacheImageTask().execute(url);
        }
    }

    private class CacheImageTask extends AsyncTask<String, Void, Void> {

        private String url;

        @Override
        protected Void doInBackground(String... urls) {
            url = urls[0];
            try {
                // Check if the image is already cached in memory
                Bitmap bitmap = memoryCache.get(url);
                if (bitmap == null) {
                    // Check if the image is already cached on disk
                    bitmap = getBitmapFromDiskCache(url);
                    if (bitmap == null) {
                        // Download the image if not cached
                        bitmap = downloadBitmap(url);
                        if (bitmap != null) {
                            // Cache the downloaded bitmap in memory
                            memoryCache.put(url, bitmap);
                            Log.d(TAG, "Image cached successfully in memory: " + url);
                            // Cache the downloaded bitmap on disk
                            addBitmapToDiskCache(url, bitmap);
                            Log.d(TAG, "Image cached successfully on disk: " + url);
                        } else {
                            // Handle error if unable to download the bitmap
                            Log.e(TAG, "Unable to download bitmap from URL: " + url);
                        }
                    } else {
                        // Cache the bitmap in memory
                        memoryCache.put(url, bitmap);
                        Log.d(TAG, "Image retrieved from disk cache: " + url);
                    }
                } else {
                    Log.d(TAG, "Image retrieved from memory cache: " + url);
                }
            } catch (IOException e) {
                // Handle IOException
                e.printStackTrace();
                Log.e(TAG, "IOException while caching image: " + e.getMessage());
            }
            return null;
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return memoryCache.get(key);
    }

    public Bitmap getBitmapFromDiskCache(String url) throws IOException {
        Log.d(TAG, "Getting bitmap from disk cache...");
        DiskLruCache.Snapshot snapshot = diskCache.get(hashKeyForDisk(url));
        if (snapshot != null) {
            InputStream inputStream = snapshot.getInputStream(0);
            return BitmapFactory.decodeStream(inputStream);
        }
        return null;
    }

    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = getBitmapFromMemoryCache(key);
        if (bitmap != null) {
            Log.d(TAG, "Bitmap retrieved from memory cache for key: " + key);
            return bitmap;
        } else {
            try {
                bitmap = getBitmapFromDiskCache(key);
                if (bitmap != null) {
                    Log.d(TAG, "Bitmap retrieved from disk cache for key: " + key);
                    // Update memory cache
                    memoryCache.put(key, bitmap);
                    Log.d(TAG, "Bitmap added to memory cache for key: " + key);
                } else {
                    Log.d(TAG, "Bitmap not found in cache for key: " + key);
                }
                return bitmap;
            } catch (IOException e) {
                Log.e(TAG, "IOException while retrieving bitmap from disk cache for key: " + key);
                throw new RuntimeException(e);
            }
        }
    }


    private void addBitmapToDiskCache(String url, Bitmap bitmap) throws IOException {
        Log.d(TAG, "Adding bitmap to disk cache...");
        DiskLruCache.Editor editor = diskCache.edit(hashKeyForDisk(url));
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(0);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            editor.commit();
            diskCache.flush();
        }
    }

    public Bitmap downloadBitmap(String url) {
        Log.d(TAG, "Downloading bitmap from URL: " + url);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            URL imageUrl = new URL(url);
            urlConnection = (HttpURLConnection) imageUrl.openConnection();
            urlConnection.setConnectTimeout(5000); // Set connection timeout to 5 seconds
            urlConnection.setReadTimeout(10000); // Set read timeout to 10 seconds
            inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    Log.d(TAG, "Bitmap downloaded successfully from URL: " + url);
                    return bitmap;
                } else {
                    Log.e(TAG, "Failed to decode bitmap from input stream");
                }
            } else {
                Log.e(TAG, "Failed to open input stream");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException while downloading bitmap: " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    private File getDiskCacheDir(Context context, String uniqueName) {
        Log.d(TAG, "Getting disk cache directory...");
        return new File(context.getCacheDir(), uniqueName);
    }

    private String hashKeyForDisk(String key) {
        Log.d(TAG, "Hashing key for disk cache...");
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
            mDigest.update(key.getBytes());
            return bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return String.valueOf(key.hashCode());
        }
    }

    private String bytesToHexString(byte[] bytes) {
        Log.d(TAG, "Converting bytes to hexadecimal string...");
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}









