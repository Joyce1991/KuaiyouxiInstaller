package installer.libs.kuaiyuoxi.com.kyxinstaller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SecondActivity extends Activity {
    private final static String TAG = "SecondActivity";

    private WindowManager mWindowManager;
    private FloatActionButton btnFloat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mWindowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams floatLayoutParams = new WindowManager.LayoutParams();
        floatLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        floatLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        floatLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        floatLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        floatLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;

        btnFloat = new FloatActionButton(this);
        mWindowManager.addView(btnFloat, floatLayoutParams);

        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "btnFloat OnClick");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(TAG, "dispatchTouchEvent()");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent()");
        return super.onTouchEvent(event);
    }
}
