package com.mercury_wireless.snappy_ui;


import java.lang.Thread.UncaughtExceptionHandler;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


public final class SnappyHorizontalScrollView extends HorizontalScrollView {
	private final class MyGestureDetector extends android.view.GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
		MyGestureDetector() {
			super();
		}


		@Override
		public final boolean onFling(
				final android.view.MotionEvent e1,
				final android.view.MotionEvent e2,
				final float velocityX,
				final float velocityY) {
			if (null == mItems) {
				return false;
			}

			try {
				// right to left
				if (e1.getX() - e2.getX() > motion_min_distance && Math.abs(velocityX) > motion_threshold_velocity) {
					final int featureWidth = getMeasuredWidth();
					final int count = mItems.getCount();
					mActiveFeature = mActiveFeature < count - 1 ? mActiveFeature + 1 : count - 1;
					smoothScrollTo(mActiveFeature * featureWidth, 0);
					return true;
				}
				// left to right
				else if (e2.getX() - e1.getX() > motion_min_distance && Math.abs(velocityX) > motion_threshold_velocity) {
					final int featureWidth = getMeasuredWidth();
					mActiveFeature = mActiveFeature > 0 ? mActiveFeature - 1 : 0;
					smoothScrollTo(mActiveFeature * featureWidth, 0);
					return true;
				}
			}
			// Log the error
			catch (final Exception e) {
				if (is_debug) {
					Log.e(package_name, "onFling - error:" + e.getMessage(), e);
				}

				// Handle the exception
				final UncaughtExceptionHandler a = Thread.getDefaultUncaughtExceptionHandler();
				if (null != a) {
					a.uncaughtException(Thread.currentThread(), e);
				}
			}
			return false;
		}


		@Override
		public final boolean onTouch(
				final View v,
				final android.view.MotionEvent event) {
			if (null == mItems) {
				return false;
			}

			// If the user swipes
			if (mGestureDetector.onTouchEvent(event)) {
				return true;
			}

			final int action = event.getAction();
			if (action == ACTION_UP || action == ACTION_CANCEL) {
				final int scrollX = getScrollX();
				final int featureWidth = v.getMeasuredWidth();
				mActiveFeature = (scrollX + featureWidth / 2) / featureWidth;
				final int scrollTo = mActiveFeature * featureWidth;
				smoothScrollTo(scrollTo, 0);
				return true;
			}
			else {
				return false;
			}
		}
	}


	private static final int	ACTION_CANCEL		= android.view.MotionEvent.ACTION_CANCEL;


	private static final int	ACTION_UP			= android.view.MotionEvent.ACTION_UP;


	private final LinearLayout	internalWrapper;


	private boolean				is_debug;


	private int					mActiveFeature		= 0;


	private GestureDetector		mGestureDetector	= null;


	private BaseAdapter			mItems				= null;


	private final int			motion_min_distance;


	private final int			motion_threshold_velocity;


	private final String		package_name;


	public SnappyHorizontalScrollView(final Context context) {
		this(context, null, 0);
	}


	public SnappyHorizontalScrollView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}


	public SnappyHorizontalScrollView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);

		this.internalWrapper = new LinearLayout(getContext());
		this.internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.internalWrapper.setOrientation(LinearLayout.HORIZONTAL);
		addView(this.internalWrapper);

		setOnTouchListener(null);

		if (isInEditMode()) {
			this.package_name = getClass().getSimpleName();
			this.is_debug = false;

			this.motion_min_distance = 5;
			this.motion_threshold_velocity = 300;
		}
		else {
			this.package_name = context.getPackageName();
			int id = 0;

			id = getResources().getIdentifier("ga_debug", "bool", package_name);
			this.is_debug = 0 != id ? getResources().getBoolean(id) : false;

			id = getResources().getIdentifier("ui_motionMinDistance", "integer", package_name);
			this.motion_min_distance = 0 != id ? getResources().getInteger(id) : 5;

			id = getResources().getIdentifier("ui_motionThresholdVelocity", "integer", package_name);
			this.motion_threshold_velocity = 0 != id ? getResources().getInteger(id) : 300;
		}
	}


	public final void setFeatureItems(final BaseAdapter items) {
		this.setOnTouchListener(null);
		this.mGestureDetector = null;
		this.mItems = items;
		this.internalWrapper.removeAllViews();

		// ...
		// Create the view for each screen in the scroll view
		// ...
		if (null != this.mItems) {
			for (int i = 0, max = this.mItems.getCount(); i < max; i++) {
				final View featureLayout = this.mItems.getView(i, null, null);
				this.internalWrapper.addView(featureLayout);
			}
			final MyGestureDetector m = new MyGestureDetector();
			this.setOnTouchListener(m);
			this.mGestureDetector = new GestureDetector(getContext(), m);
		}
	}
}
