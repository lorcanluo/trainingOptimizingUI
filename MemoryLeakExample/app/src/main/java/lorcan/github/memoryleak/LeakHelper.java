package lorcan.github.memoryleak;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by luocan on 15/4/14.
 */
public class LeakHelper {
    private static LeakHelper instance;
    private static ArrayList<Bitmap> drawables = new ArrayList<Bitmap>();

    private LeakHelper() {

    }

    public static LeakHelper getInstance() {
        synchronized (LeakHelper.class) {
            if (instance == null) {
                instance = new LeakHelper();
            }

            return instance;
        }
    }

    public void addDrawable(Bitmap drawable) {
        drawables.add(drawable);
    }
}
