package com.waracle.androidtest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Riad on 20/05/2015.
 */
public class ImageLoader {

    /*
    Keep an LRUCache for loading bitmaps from byte arrays.

    The reason this works is because even though the ImageLoader is used in different fragments (for loading byte arrays
    and for loading bitmaps), it is always the same instance due to being a part of the ViewModel.
    ViewModel life cycles transcend those of fragments so all fragments will be using the same ImageLoader instance.
     */
    private final LruCache<byte[], Bitmap> bitmapCache;

    public ImageLoader() {
        int cacheSize = 4 * 1024 * 1024;
        bitmapCache = new LruCache<>(cacheSize);
    }

    /**
     * Simple function for loading a bitmap image from the web
     *
     * @param data       image url
     * @param imageView view to set image too.
     */
    public void load(byte[] data, ImageView imageView) {

        // Can you think of a way to improve loading of bitmaps
        // that have already been loaded previously??
        // Yes, use an LRUCache.

        //Even though data cannot be null in this app, check anyways in case this method would be used
        //more than once in the future.
        if(data != null && bitmapCache.get(data) != null){
            setImageView(imageView, bitmapCache.get(data));
        }
        else {
            setImageView(imageView, convertToBitmap(data));
        }
    }

    public byte[] loadImageData(String url) throws IOException {
        byte[] bytes = loadImageDataWithRedirect(url, 0);
        if(bytes != null && bitmapCache.get(bytes) == null){
            bitmapCache.put(bytes, convertToBitmap(bytes));
        }
        return bytes;
    }

    //This helper method exists to avoid getting stuck in a redirect loop, even though that is unlikely it
    // should still be handled.
    private byte[] loadImageDataWithRedirect(String url, int redirectAmount) throws IOException{
        if(redirectAmount >= 3){
            return null;
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.addRequestProperty("Cache-Control", "max-age=" + (60 * 60 * 24 * 7));
        connection.setUseCaches(true);
        int responseCode = connection.getResponseCode();
        String contentType = connection.getHeaderField("Content-Type");
        if(contentType != null && !contentType.startsWith("image/")){
            return null;
        }
        if(responseCode == 200){
            InputStream inputStream = null;
            try {
                try {
                    // Read data from workstation
                    inputStream = connection.getInputStream();
                } catch (IOException e) {
                    // Read the error from the workstation
                    inputStream = connection.getErrorStream();
                }

                // Can you think of a way to make the entire
                // HTTP more efficient using HTTP headers??

                // Use an HTTPResponseCache.

                return StreamUtils.readUnknownFully(inputStream);
            } finally {
                // Close the input stream if it exists.
                StreamUtils.close(inputStream);

                // Disconnect the connection
                connection.disconnect();
            }
        }
        else if(responseCode >= 300 && responseCode <= 308){
            String location = connection.getHeaderField("Location");
            if(location != null){
                return loadImageDataWithRedirect(location, redirectAmount + 1);
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    private static Bitmap convertToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private static void setImageView(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
