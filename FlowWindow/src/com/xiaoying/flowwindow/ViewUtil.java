package com.xiaoying.flowwindow;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

/**
 * Usage: 与View相关的工具类
 * 
 * @author zyy_owen@ivg.com
 */
public class ViewUtil {
	
	private ViewUtil() {}
	
	/**
	 * Change px to dip
	 * 
	 * @param context
	 * @param pixs
	 * @return
	 */
	public static float pxToDip(Context context, int pixs) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return ((float) (pixs * 160)) / dm.densityDpi;
	}

	/**
	 * Change dip to px
	 * 
	 * @param context
	 * @param dips
	 * @return
	 */
	public static int dipToPx(Context context, float dips) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) (dips * ((float) dm.densityDpi / 160));
	}

	/**
	 * Change px to sp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * Change sp to px
	 * 
	 * @param spValue
	 * @param fontScale（DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
     * Measure a view.
     * @param child
     */
    public static void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
    
    /**
     * Set the size of ViewGroup by backgroud image resource.
     * @param parent
     */
    public static void setSizeByBackgroudImage(ViewGroup parent) {
    	measureView(parent);
    	ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) parent.getLayoutParams();
    	lp.width = parent.getMeasuredWidth();
    	lp.height = parent.getMeasuredHeight();
    	parent.setLayoutParams(lp);
    }
    
    public static void layoutViewAuto(View view) {
		measureView(view);
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		lp.width = view.getMeasuredWidth();
		lp.height = view.getMeasuredHeight();
	}
}
