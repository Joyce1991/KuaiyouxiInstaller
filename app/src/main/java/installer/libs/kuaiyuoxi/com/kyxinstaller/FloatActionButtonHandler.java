package installer.libs.kuaiyuoxi.com.kyxinstaller;

import android.os.Handler;

/**
 * Created by jalen-pc on 2015/8/3.
 */
public class FloatActionButtonHandler extends Handler {

    public static final int ANIMATION_IN_TOUCH = 1;
    private float mTouchPositionX;
    private float mTouchPositionY;

    /**
     * 更新触摸位置的坐标值
     *
     * @param positionX 触摸X坐标
     * @param positionY 触摸Y坐标
     */
    void updateTouchPosition(float positionX, float positionY) {
        mTouchPositionX = positionX;
        mTouchPositionY = positionY;
    }

}
