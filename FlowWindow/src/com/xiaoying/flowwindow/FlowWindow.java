package com.xiaoying.flowwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;

/**
 * Usage: 浮动窗口按钮的管理类
 * 
 * @author zyy_owen@ivg.com
 */
public class FlowWindow {
	
	/** 长按事件的延迟时间 **/
	private static final int LONG_CLICK_DELAY = 600;
	
	/** 最小移动距离，如果小于这个移动距离，图标会有稍微的移动，但是处理事件的时候不认为这是在移动 **/
	private static final int MIN_MOVE_SPACE = 10;
	
	/** 消息-长按事件 **/
	private static final int MSG_LONG_CLICK_EVENT = 0x0;
	
	private static FlowWindow mFlowWindow;
	
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayoutParams;
	private View mFlowView;
	private ImageButton mIbtnIcon;
	private Point mDefaultPosition = new Point(10, 10); // 默认位置
	private Point mMaxSize = new Point(0, 0); // 最大尺寸
	private float mAlpha = 0.8f;

	private boolean mMovable = true; // 是否可移动，默认可以移动
	private boolean mShowing = false; // 是否正在显示
	
	private FlowWindow(Context context) {
		mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		initView(context);
	}
	
	/**
	 * 获取浮动控件管理的单一实例
	 * @param context
	 * @return
	 */
	public static FlowWindow getInstance(Context context) {
		if(mFlowWindow == null) {
			mFlowWindow = new FlowWindow(context);
		}
		return mFlowWindow;
	}
	
	/**
	 * 显示浮动控件
	 */
	public void showFlowView() {
		if(mShowing) {
			return;
		}
		mWindowManager.addView(mFlowView, mLayoutParams);
		mShowing = true;
	}
	
	/**
	 * 隐藏浮动控件
	 */
	public void hideFlowView() {
		if(!mShowing) {
			return;
		}
		mWindowManager.removeView(mFlowView);
		mShowing = false;
	}
	
	/**
	 * 设置点击（短按）事件监听
	 * @param listener
	 */
	public void setOnClickListener(View.OnClickListener listener) {
		this.mClickListener = listener;
		mIbtnIcon.setOnClickListener(mClickListener);
	}
	
	/**
	 * 设置长按事件监听
	 * @param listener
	 */
	public void setOnLongClickListener(View.OnLongClickListener listener) {
		this.mLongClickListener = listener;
		mIbtnIcon.setOnClickListener(mClickListener);
	}
	
	/**
	 * 设置浮动控件是否可以移动
	 * @param movable
	 */
	public void setMovable(boolean movable) {
		this.mMovable = movable;
	}
	
	/**
	 * 设置默认位置
	 * @param x
	 * @param y
	 */
	public void setDefaultPosition(int x, int y) {
		this.mDefaultPosition.set(x, y);
		mLayoutParams.x = mDefaultPosition.x;
		mLayoutParams.y = mDefaultPosition.y;
		if(mShowing) { // 如果正在显示，更新视图
			mWindowManager.updateViewLayout(mFlowView, mLayoutParams);
		}
	}
	
	/**
	 * 设置透明度，值在0.0f~1.0f之间，0.0f为全透明，1.0f为不透明
	 * @param alpha
	 */
	public void setAlpha(float alpha) {
		this.mAlpha = alpha;
		mLayoutParams.alpha = mAlpha;
		if(mShowing) { // 如果正在显示，更新视图
			mWindowManager.updateViewLayout(mFlowView, mLayoutParams);
		}
	}

	private void initView(Context context) {
		Resources resource = context.getResources();
		String packageName = context.getPackageName();
		int layoutId = resource.getIdentifier("layout_flowwindow", "layout", packageName);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFlowView = inflater.inflate(layoutId, null);
		mLayoutParams = new WindowManager.LayoutParams();
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		mLayoutParams.format = PixelFormat.RGBA_8888;
		mLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;
		mLayoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		mLayoutParams.alpha = mAlpha;
		ViewUtil.measureView(mFlowView);
		DisplayMetrics dm = resource.getDisplayMetrics();
		mMaxSize.x = dm.widthPixels > dm.heightPixels ? dm.heightPixels / 6 : dm.widthPixels / 6;
		mMaxSize.y = mMaxSize.x; // 默认长宽相等
		int measuredWidth = mFlowView.getMeasuredWidth();
		int measuredHeight = mFlowView.getMeasuredHeight();
		mLayoutParams.width = measuredWidth > mMaxSize.x ? mMaxSize.x : measuredWidth;
		mLayoutParams.height = measuredHeight > mMaxSize.y ? mMaxSize.y : measuredHeight;
		mLayoutParams.x = mDefaultPosition.x;
		mLayoutParams.y = mDefaultPosition.y;
//		mIbtnIcon = (ImageButton) mFlowView.findViewById(R.id.ibtn_flowwindow_icon);
		int buttonId = resource.getIdentifier("ibtn_flowwindow_icon", "id", packageName);
		mIbtnIcon = (ImageButton) mFlowView.findViewById(buttonId);
		mIbtnIcon.setOnTouchListener(mTouchListener);
		mIbtnIcon.setOnClickListener(mClickListener);
	}
	
	/**
	 * 消息处理Handler<br>
	 * <p>用来处理长按事件等消息
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_LONG_CLICK_EVENT: // 长按事件
				if(null != mLongClickListener) {
					mLongClickListener.onLongClick(mIbtnIcon);
				}
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 触摸事件监听
	 */
	@SuppressLint("ClickableViewAccessibility")
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		PointF startPoint = new PointF(0.0f, 0.0f); // 开始触摸的点
		PointF lastPoint = new PointF(0.0f, 0.0f); // 上一个记录的点
		float pathLenght = 0.0f; // 路径长度，也就是移动的轨迹长度
		long startTime = 0L; // 开始时间，ACTION_DOWN的时间
		boolean moving = false; // 是否移动

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mMovable) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					startPoint.set(event.getRawX(), event.getRawY());
					lastPoint.set(startPoint);
					pathLenght = 0.0f;
					startTime = System.currentTimeMillis();
					moving = false;
					mHandler.sendEmptyMessageDelayed(MSG_LONG_CLICK_EVENT, LONG_CLICK_DELAY); // 延迟发送
					break;
				case MotionEvent.ACTION_MOVE:
					mLayoutParams.x += (int) (event.getRawX() - lastPoint.x);
					mLayoutParams.y += (int) (event.getRawY() - lastPoint.y);
					mWindowManager.updateViewLayout(mFlowView, mLayoutParams);
					lastPoint.set(event.getRawX(), event.getRawY());
					pathLenght += spacing(lastPoint, startPoint);
					moving =  pathLenght > MIN_MOVE_SPACE;
					break;
				case MotionEvent.ACTION_UP:
					mHandler.removeMessages(MSG_LONG_CLICK_EVENT); // 移除长按消息，如果这个消息还没有被处理
					if(System.currentTimeMillis() - startTime >= LONG_CLICK_DELAY && !moving) {
						// 不是移动操作，而且距离点下时间大于长按事件延迟，就认为是长按事件，返回true，中断onClick的处理。
						return true;
					}
					break;
				default:
					break;
				}
			}
			if(moving) {
				mHandler.removeMessages(MSG_LONG_CLICK_EVENT); // 如果是移动，移除长按消息
			}
			return moving;
		}
	};
	
	/**
	 * 点击事件监听(默认)
	 */
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			System.out.println("OnClick=====================>>>>>>>>>>>>>>>>>>>>");
			
		}
	};
	
	/**
	 * 长按事件监听(默认)
	 */
	private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			System.out.println("OnLongClick=====================>>>>>>>>>>>>>>>>>>>>");
			return true;
		}
	};
	

    
    /**
     * 计算两点之间的距离
     * @param point1
     * @param point2
     * @return
     */
	@SuppressLint("FloatMath")
	private float spacing(PointF point1, PointF point2) {
		float x = point1.x - point2.x;
		float y = point1.y - point2.y;
//        //如果在API8以下的版本使用，采用FloatMath.sqrt()会更快，但是在API8和以上版本，Math.sqrt()更快
//        //原文：Use java.lang.Math#sqrt instead of android.util.FloatMath#sqrt() since it is faster as of API 8
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
//            return FloatMath.sqrt(x * x + y * y);
//        }
        return (float) Math.sqrt(x * x + y * y);
	}
}
