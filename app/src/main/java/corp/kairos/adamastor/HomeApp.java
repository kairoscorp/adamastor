package corp.kairos.adamastor;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by kiko on 27-12-2017.
 */

public class HomeApp extends LinearLayout {

    private String packageName;

    public HomeApp(Context context) {
        super(context);
    }

    public HomeApp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HomeApp,
                0, 0);

        try {
            packageName = a.getString(R.styleable.HomeApp_packageName);
        } finally {
            a.recycle();
        }
    }

    public HomeApp(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HomeApp(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public String getPackageName(){
        return packageName;
    }

    public void setPackageName(String pn){
        packageName = pn;
        /*We don't need the following calls because the change to the attribute does not change the view's appearance
        invalidate();
        requestLayout();*/
    }
}
