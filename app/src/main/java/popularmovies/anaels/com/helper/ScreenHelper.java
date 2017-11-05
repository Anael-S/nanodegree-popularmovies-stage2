package popularmovies.anaels.com.helper;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Anael on 10/15/2017.
 */
public class ScreenHelper {

    public static int calculateNoOfColumns(Context context, final int columnWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / columnWidth);
        return noOfColumns;
    }
}
