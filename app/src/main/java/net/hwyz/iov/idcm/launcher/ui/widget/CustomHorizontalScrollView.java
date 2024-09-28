package net.hwyz.iov.idcm.launcher.ui.widget;

import static net.hwyz.iov.idcm.launcher.ui.widget.CustomHorizontalScrollView.ScrollOrientation.IDLE;
import static net.hwyz.iov.idcm.launcher.ui.widget.CustomHorizontalScrollView.ScrollOrientation.LEFT;
import static net.hwyz.iov.idcm.launcher.ui.widget.CustomHorizontalScrollView.ScrollOrientation.RIGHT;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

import androidx.annotation.NonNull;

import net.hwyz.iov.idcm.launcher.observer.Interaction;
import net.hwyz.iov.idcm.launcher.observer.InteractionObserverObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * 自定义水平滚动视图
 *
 * @author hwyz_leo
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {

    private final CustomHandler mHandler = new CustomHandler(this);
    // 是否滑动中
    private boolean mIsSlide = false;
    // 是否正在执行动画
    private boolean mIsAnimation = false;
    // 最小滚动距离
    private int mTouchSlop;
    // 开始触摸时X坐标
    private float mStartTouchX;
    // 当前滑动的X坐标
    private int mCurrentScrollX = 0;
    // X轴滑动的速率
    private int mVelocityX = 0;
    // 吸附X坐标
    private int mAdsorptionX = 0;
    // X轴固定坐标点
    private final int mCoordinate_0 = 0;
    private final int mCoordinate_768 = 768;
    private final int mCoordinate_2560 = 2560;
    private static final int TIME = 600;
    // 滚动辅助
    private OverScroller mScroller;
    // 动画辅助
    private ValueAnimator mValueAnimator;

    // 滑动方向枚举
    public enum ScrollOrientation {LEFT, RIGHT, IDLE}

    // 当前滑动方向
    private ScrollOrientation mOrientation = IDLE;
    private final int viewScrollTag = -1;


    // 时间间隔
    private final int mDelayMillis = 10;


    public CustomHorizontalScrollView(Context context) {
        super(context);
        initView(context);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    private void initView(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mScroller = (OverScroller) get(this, "mScroller");
        getViewTreeObserver().addOnScrollChangedListener(() -> {
            switch (getScrollX()) {
                case mCoordinate_0:
                case mCoordinate_768:
                case mCoordinate_2560:
                    if (!mIsAnimation) {
                        slideEndNotify();
                    }
                    break;
            }
            int currentScrollX = getScrollX();
            if (currentScrollX > mCurrentScrollX) {
                this.mOrientation = RIGHT;
            } else if (currentScrollX < mCurrentScrollX) {
                this.mOrientation = LEFT;
            }
            mCurrentScrollX = currentScrollX;
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mStartTouchX = event.getX();
                mCurrentScrollX = 0;
                mVelocityX = 0;
                mHandler.removeCallbacksAndMessages(null);
                mIsAnimation = false;
                if (!mScroller.isFinished()) {
                    cancelAnimation();
                    if (getScrollX() != mAdsorptionX && getScrollX() != mCoordinate_2560) {
                        startAnimation(getScrollX(), lastScrollX(mOrientation, getScrollX()));
                    } else if (mIsSlide) {
                        slideEndNotify();
                    }
                }
            }
            return super.dispatchTouchEvent(event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                mHandler.removeCallbacksAndMessages(null);
                cancelAnimation();
                boolean canMove = Math.abs(event.getX() - mStartTouchX) >= mTouchSlop;
                if (canMove) {
                    slideStartNotify();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsAnimation = false;
                mVelocityX = 0;
                mHandler.sendMessageDelayed(mHandler.obtainMessage(viewScrollTag), mDelayMillis * 2);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 自定义处理器
     */
    private static class CustomHandler extends Handler {
        private WeakReference<CustomHorizontalScrollView> weakRef;

        public CustomHandler(CustomHorizontalScrollView scrollView) {
            weakRef = new WeakReference<>(scrollView);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            weakRef.get().autoScrollTo(msg.what);
        }
    }

    /**
     * 自动滑动
     *
     * @param what 消息类型
     */
    private void autoScrollTo(int what) {
        if (what == viewScrollTag) {
            if (mVelocityX == 0) {
                int scrollX = getScrollX();
                if (needSlide(scrollX)) {
                    int lastScrollX = lastScrollX(mOrientation, scrollX);
                    if (lastScrollX != scrollX && scrollX != mCoordinate_2560) {
                        cancelAnimation();
                        startAnimation(scrollX, lastScrollX);
                    } else {
                        slideEndNotify();
                    }
                } else if (mIsSlide) {
                    slideEndNotify();
                }
            }
        }
    }

    /**
     * 滑动开始通知
     */
    private void slideStartNotify() {
        mIsAnimation = true;
        if (!mIsSlide) {
            mIsSlide = true;
            InteractionObserverObject.INSTANCE.notify(Interaction.SLIDE_START);
        }
    }

    /**
     * 滑动结束通知
     */
    private void slideEndNotify() {
        mIsSlide = false;
        mIsAnimation = false;
        InteractionObserverObject.INSTANCE.notify(Interaction.SLIDE_END);
    }

    @Override
    public void fling(int velocityX) {
        super.fling(velocityX);
        mVelocityX = velocityX;
        mHandler.removeCallbacksAndMessages(null);
        mIsAnimation = false;
        cancelAnimation();
        int finalX = mScroller.getFinalX();
        if (velocityX > 0) {
            this.mOrientation = RIGHT;
        } else {
            this.mOrientation = LEFT;
        }
        mAdsorptionX = lastScrollX(mOrientation, finalX);
        mScroller.fling(getScrollX(), 0, velocityX, 0, mAdsorptionX, mAdsorptionX, 0, 0);
        if (mAdsorptionX == getScrollX()) {
            slideEndNotify();
        }
    }

    /**
     * 获取实例成员变量
     *
     * @param instance     实例对象
     * @param variableName 变量名
     * @return 对应对象
     */
    public Object get(Object instance, String variableName) {
        Class targetClass = instance.getClass().getSuperclass();
        HorizontalScrollView superInst = (HorizontalScrollView) targetClass.cast(instance);
        Field field;
        try {
            field = targetClass.getDeclaredField(variableName);
            field.setAccessible(true);
            return field.get(superInst);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算滚动最后位置
     *
     * @param orientation    滚动方向
     * @param currentScrollX 当前滚动X坐标
     * @return 滚动最后位置
     */
    private int lastScrollX(ScrollOrientation orientation, int currentScrollX) {
        int count = 0;
        if (mCoordinate_0 < currentScrollX && currentScrollX <= mCoordinate_768) {
            if (orientation == LEFT) {
                count = mCoordinate_0;
            } else if (orientation == RIGHT) {
                count = mCoordinate_768;
            }
        } else if (mCoordinate_768 < currentScrollX && currentScrollX <= mCoordinate_2560) {
            if (orientation == LEFT) {
                count = mCoordinate_768;
            } else if (orientation == RIGHT) {
                count = mCoordinate_2560;
            }
        }
        return count;
    }

    /**
     * 开始动画效果
     *
     * @param start 开始X坐标
     * @param end   结束X坐标
     */
    private void startAnimation(int start, int end) {
        try {
            mValueAnimator = ValueAnimator.ofInt(start, end);
            mValueAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                scrollTo(value, 0);
            });
            mValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    cancelAnimation();
                    mIsAnimation = false;
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
            mValueAnimator.setDuration(TIME);
            mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mValueAnimator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消动画效果
     */
    private void cancelAnimation() {
        try {
            if (mValueAnimator != null) {
                mValueAnimator.removeAllListeners();
                mValueAnimator.removeAllUpdateListeners();
                if (mValueAnimator.isRunning()) {
                    mValueAnimator.cancel();
                }
            }
            mValueAnimator = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否需要滑动
     *
     * @param scrollX 当前X坐标
     * @return 是否需要滑动
     */
    private boolean needSlide(int scrollX) {
        switch (scrollX) {
            case mCoordinate_0:
            case mCoordinate_768:
            case mCoordinate_2560:
                return false;
        }
        return true;
    }
}
