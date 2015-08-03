package installer.libs.kuaiyuoxi.com.kyxinstaller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * 悬浮窗按钮
 * 1、点击弹出录制、观看两个按钮
 * 2、可拖动，松开是会靠边
 * 3、点击展开子View会根据当前所处的位置展现不同的布局
 */
public class FloatActionButton extends RelativeLayout implements ViewTreeObserver.OnPreDrawListener {
    private final static String TAG = "FloatActionButton";
    private final static float MOVE_MIN_LIMIT = 8.0f;

    private Context mContext;
    private boolean isDraggable;    // 标识是否可拖拽
    private boolean isMoveAccept;   // 标识开始移动
    private boolean isOnRight;
    private float mScreenTouchX;
    private float mScreenTouchY;
    private float mScreenActionDownX;
    private float mScreenActionDownY;
    private float mLocalTouchX;
    private float mLocalTouchY;
    private long mTouchDownTime;
    private DisplayMetrics mMetrics;
    private WindowManager mWindowManager;
    private VelocityTracker mVelocityTracker;   // 触屏事件跟踪速率计算器
    private FloatActionButtonHandler mHandler;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private  int mStatusBarHeight;  // 系统状态栏的高度

    private CoreView btnCore;
    private ImageButton btnRecord;
    private ImageButton btnWatch;

    public FloatActionButton(Context context) {
        super(context);
        this.mContext = context;
        init(null, 0);
    }

    public FloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs, 0);
    }

    public FloatActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        isDraggable = false;
        mHandler = new FloatActionButtonHandler();
        mWindowLayoutParams = new WindowManager.LayoutParams();

        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        mMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(mMetrics);

        // 获取系统状态栏的高度
        Resources resources = mContext.getResources();
        int statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if(statusBarHeightId > 0){
            mStatusBarHeight = resources.getDimensionPixelSize(statusBarHeightId);
        }else {
            mStatusBarHeight = 0;
        }

        btnCore = new CoreView(mContext);
        btnCore.setBackgroundResource(R.drawable.core_view_bg);
        LayoutParams coreLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnCore.setLayoutParams(coreLayoutParams);
        btnCore.setChecked(true);
        this.addView(btnCore, 0);

        btnCore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showRecordAndWatchView();
                } else {
                    hideRecordAndWatchView();
                }
            }
        });
        // 通过ViewTreeObserver注册一下onPreDraw监听(我们会在onPreDraw中进行绘制初始化)
        getViewTreeObserver().addOnPreDrawListener(this);
    }

    /**
     * 影藏录制、观看按钮视图
     */
    private void hideRecordAndWatchView() {
        Log.i(TAG, "影藏录制、观看按钮视图");
    }

    /**
     * 显示录制、观看按钮视图
     */
    private void showRecordAndWatchView() {
        Log.i(TAG, "显示录制、观看按钮视图");
    }

    /**
     * 绘制坐标初始化
     * @return
     */
    @Override
    public boolean onPreDraw() {
        // 1. 移除onPreDraw监听（这个初始化只需要进行一次）
        getViewTreeObserver().removeOnPreDrawListener(this);
        // 2. FloatActionButton添加至视图所需的LayoutParams
        mWindowLayoutParams.x = 0;
        mWindowLayoutParams.y = mMetrics.heightPixels - mStatusBarHeight - getMeasuredHeight();
        // 3. 更新FloatActionButton的Layout
        mWindowManager.updateViewLayout(this, mWindowLayoutParams);
        // 4. 初始化状态值
        isDraggable = true;
        isOnRight = false;
        moveToEdge(false);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(TAG, "dispatchTouchEvent()");
        // 悬浮按钮为不可见状态
        if (getVisibility() != View.VISIBLE){
            return true;
        }
        // 悬浮按钮为不可移动状态
        if (isDraggable){
            return true;
        }
        // 缓存当前位置
        mScreenTouchX = ev.getRawX();
        mScreenTouchY = ev.getRawY();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                // 1. 记录事件点相对于屏幕的位置
                mScreenActionDownX = ev.getRawX();
                mScreenActionDownY = ev.getRawY();
                // 2. 记录事件点相对于当前View的位置
                mLocalTouchX = ev.getX();
                mLocalTouchY = ev.getY();
                // 3. 移动状态为false
                isMoveAccept = false;
                // 4. 记录ACTION_DOWN的时间
                mTouchDownTime = ev.getDownTime();
                // 5.触摸事件移动速率计算器初始化
                if (mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                // 1. 如果ACTION_DOWN事件没有经过处理
                if (mTouchDownTime != ev.getDownTime()){
                    return true;
                }
                // 2. 如果ACTION_MOVE离ACTION_DOWN位置小于位移的最低限制
                float mMoveMinLimitScaled = MOVE_MIN_LIMIT * mMetrics.density;
                if(!isMoveAccept &&
                        Math.abs(mScreenTouchX - mScreenActionDownY) < mMoveMinLimitScaled  &&
                        Math.abs(mScreenTouchY - mScreenActionDownY) < mMoveMinLimitScaled) {
                    return true;
                }
                // 3. 移动状态为true
                isMoveAccept = true;
                // 4. 更新touch位置
                mHandler.updateTouchPosition(mScreenTouchX, mScreenTouchY);
                // 5. 触摸事件移动速度计算
                mVelocityTracker.addMovement(ev);
                mVelocityTracker.computeCurrentVelocity(1000);
                break;
            case MotionEvent.ACTION_UP | MotionEvent.ACTION_CANCEL:
                // 1. 如果ACTION_DOWN事件没有经过处理
                if (mTouchDownTime != ev.getDownTime()){
                    return true;
                }
                mHandler.removeMessages(FloatActionButtonHandler.ANIMATION_IN_TOUCH);

                // 通过动画形式移动到屏幕的一端
                if (isMoveAccept) {
                    // 速度計算
                    mVelocityTracker.addMovement(ev);
                    mVelocityTracker.computeCurrentVelocity(1000);
                    moveToEdge(true);
                }
                // 動かされていなければ、クリックイベントを発行
                else {
                    // 一番上のViewからたどって、1つ処理したら終了
                    final int size = getChildCount();
                    for (int i = size - 1; i >= 0; i--) {
                        if (getChildAt(i).performClick()) {
                            break;
                        }
                    }
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 松手FloatActionButton移动至屏幕一端
     * @param b
     */
    private void moveToEdge(boolean b) {
        // 1. 获取FloatActionButton当前位置
        final int currentX = getXByTouch();
        final int currentY = getYByTouch();
        // 2. 判断是否向屏幕右端移动
        final boolean isMoveRightEdge = currentX > (mMetrics.widthPixels - getWidth()) / 2;
        /*final int goalPositionX = isMoveRightEdge ? mPositionLimitRect.right : mPositionLimitRect.left;
        final int goalPositionY = Math.min(Math.max(mPositionLimitRect.top, currentY), mPositionLimitRect.bottom);*/
    }

    /**
     * 通过触摸事件的相对屏幕的X坐标位置 - 相对View本身X坐标位置 = View在屏幕上的X坐标位置
     * @return FloatActionButton控件在屏幕上的X坐标位置
     */
    private int getXByTouch() {
        return (int)(mScreenTouchY - mLocalTouchY);
    }

    /**
     * 通过触摸事件的相对屏幕的Y坐标位置 - 相对View本身Y坐标位置 = View在屏幕上的Y坐标位置
     * @return FloatActionButton控件在屏幕上的Y坐标位置
     */
    private int getYByTouch() {
        return (int) (mScreenTouchY - mLocalTouchY);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(TAG, "onInterceptTouchEvent()");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent()");
        return super.onTouchEvent(event);
    }


}
