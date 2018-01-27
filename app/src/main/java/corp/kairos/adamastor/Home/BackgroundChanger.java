package corp.kairos.adamastor.Home;


import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class BackgroundChanger {

    private static String TAG = BackgroundChanger.class.toString();

    private static BackgroundChanger mInstance = null;
    private ExecutorService executorService;

    private BackgroundChanger() {
        this.executorService = newSingleThreadExecutor();
    }

    public static BackgroundChanger getInstance() {
        if (mInstance == null) {
            mInstance = new BackgroundChanger();
        }

        return mInstance;
    }


    public void changeBackground(Context appContext, int wallpaperResource) {

        this.executorService.submit(() -> {
            try {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(appContext);
                DisplayMetrics metrics = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getMetrics(metrics);
                int height = metrics.heightPixels;
                int width = metrics.widthPixels;
                wallpaperManager.suggestDesiredDimensions(width, height);
                wallpaperManager.setResource(wallpaperResource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Changes the wallpaper of the device asynchronously,
     * resorting to a new thread.
     *
     * @param appContext         The application context
     * @param wallpaperResource The resource to set the wallpaper as.
     */
    public void setWallpaper(Context appContext, int wallpaperResource) {
        this.executorService.submit(() -> {
            Bitmap wallpaperBitmap = BitmapFactory.decodeResource(appContext.getResources(), wallpaperResource);
            try {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(appContext);
                if(wallpaperBitmap != null) {
                    DisplayMetrics metrics = new DisplayMetrics();
                    WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
                    windowManager.getDefaultDisplay().getMetrics(metrics);
                    int height = metrics.heightPixels;
                    int width = metrics.widthPixels;
                    wallpaperManager.setWallpaperOffsetSteps(1, 1);
                    wallpaperManager.suggestDesiredDimensions(width, height);
                    Bitmap bitmap = centerCropWallpaper(appContext, wallpaperBitmap, Math.min(wallpaperManager.getDesiredMinimumWidth(), wallpaperManager.getDesiredMinimumHeight()));
                    wallpaperManager.setBitmap(bitmap);
                } else {
                    Log.e(TAG, "Wallpaper could not be set.");
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error setting wallpaper. " + ex.getMessage(), ex);
            }
        });

    }

    private Bitmap centerCropWallpaper(Context appContext, Bitmap wallpaper, int desiredHeight){
        float scale = (float) desiredHeight / wallpaper.getHeight();
        int scaledWidth = (int) (scale * wallpaper.getWidth());
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int deviceWidth = metrics.widthPixels;
        int imageCenterWidth = scaledWidth /2;
        int widthToCut = imageCenterWidth - deviceWidth / 2;
        int leftWidth = scaledWidth - widthToCut;
        Bitmap scaledWallpaper = Bitmap.createScaledBitmap(wallpaper, scaledWidth, desiredHeight, false);
        return Bitmap.createBitmap(scaledWallpaper, widthToCut, 0, leftWidth, desiredHeight);
    }

//    public void swapWallpaper(Context appContext, int wallpaperResource) {
//        this.executorService.submit(() -> {
//            WallpaperManager wallpaperManager = WallpaperManager.getInstance(appContext);
//            DisplayMetrics metrics = new DisplayMetrics();
//            WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
//            windowManager.getDefaultDisplay().getMetrics(metrics);
//            int height = metrics.heightPixels;
//            int width = metrics.widthPixels;
//            Glide.with(appContext)
//                .load()
//                .override()
//                .fitcenter()
//                .into()
//        });
//    }

}
