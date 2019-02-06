package com.waracle.androidtest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Riad on 20/05/2015.
 */
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();

    public ImageLoader() { /**/ }

    /**
     * Simple function for loading a bitmap image from the web
     *
     * @param data       image url
     * @param imageView view to set image too.
     */
    public void load(byte[] data, ImageView imageView) {

        // Can you think of a way to improve loading of bitmaps
        // that have already been loaded previously??
        setImageView(imageView, convertToBitmap(data));
    }

    public static byte[] loadImageData(String url) throws IOException {
        return loadImageDataWithRedirect(url, 0);
    }

    //This helper method exists to avoid getting stuck in a redirect loop, even though that is unlikely it
    // should still be handled.
    private static byte[] loadImageDataWithRedirect(String url, int redirectAmount) throws IOException{
        if(redirectAmount >= 3){
            return null;
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
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
