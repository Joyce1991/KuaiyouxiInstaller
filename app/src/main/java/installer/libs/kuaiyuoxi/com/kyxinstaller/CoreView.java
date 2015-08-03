package installer.libs.kuaiyuoxi.com.kyxinstaller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by jalen-pc on 2015/8/3.
 */
public class CoreView extends CompoundButton {
    private static final String TAG = "CoreView";

    public CoreView(Context context) {
        this(context,null);
    }

    public CoreView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setChecked(isChecked());
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
