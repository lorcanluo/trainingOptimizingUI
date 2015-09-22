package lorcan.github.memoryleak;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by luocan on 15/4/14.
 */
public class BlackActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.gc();
    }
}
