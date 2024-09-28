package net.hwyz.iov.idcm.launcher.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.RelativeLayout
import kotlin.math.abs

/**
 * 自定义相对布局
 *
 * @author hwyz_leo
 */
class CustomRelativeLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    // 开始X坐标
    private var mStartX = 0f
    // 最小滚动距离
    private var mTouchSlop = 0
    // 是否长按
    var isLongClick = false

    init {
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                if (isLongClick.not()) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isLongClick.not()) {
                    if (abs(event.x - mStartX) < mTouchSlop) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
            else -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(event)
    }

}