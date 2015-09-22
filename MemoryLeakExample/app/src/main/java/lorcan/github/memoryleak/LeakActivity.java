package lorcan.github.memoryleak;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by luocan on 15/4/14.
 */
public class LeakActivity extends Activity implements View.OnClickListener {
    private LinearLayout linearLayout;
    LeakHelper leakHelper = LeakHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);

        linearLayout = (LinearLayout) findViewById(R.id.leak_layout);
        findViewById(R.id.leak_btn).setOnClickListener(this);
        findViewById(R.id.leak_black_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leak_btn:
                addTestImageView();
                break;
            case R.id.leak_black_btn:
                Intent intent = new Intent(this,BlackActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void addTestImageView() {
        Bitmap bitmap = getBitmapFromAsset();
        leakHelper.addDrawable(bitmap);

        ImageView image = new ImageView(this);
        image.setImageBitmap(bitmap);

        linearLayout.addView(image);
    }


    private Bitmap getBitmapFromAsset() {
        //获取assets下的资源
        AssetManager am = getAssets();
        Bitmap image = null;
        try {
            //图片放在img文件夹下
            InputStream is = am.open("test.jpg");
            image = BitmapFactory.decodeStream(is);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
