package corp.kairos.adamastor.Home;


import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@GlideModule
public class BackgroundChanger extends AppGlideModule {

    /**
     * Changes the wallpaper of the device asynchronously,
     * resorting to a new thread.
     *
     * @param appContext        The application context
     * @param wallpaperResource The resource to set the wallpaper as.
     */
    public static void changeWallpaper(Context appContext, int wallpaperResource) {
        AsyncTask.execute(() -> {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(appContext);
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;
            RequestOptions requestOptions = new RequestOptions()
                    .override(width, height)
                    .fitCenter();

            try {
                Drawable resource = Glide.with(appContext)
                    .load(wallpaperResource)
                    .apply(requestOptions)
                    .submit(width, height)
                    .get();
                wallpaperManager.setBitmap(((BitmapDrawable) resource).getBitmap());
            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
            }
        });
    }

}
