package cn.uans3k.view.viewgroup;

import com.nineoldandroids.view.ViewHelper;

import cn.uans3k.R;
import cn.uans3k.utils.MoveTool;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * @author  uans3k
 */
public class DismissHeaderLayout extends FrameLayout{
	protected int mDuration = 500;
	protected int mHeaderId = 0;
	protected float mCurrentY = 0;
	protected ABScroller mScroller = null;
	protected float mLastTouchY = 0;
	protected float mCurrentTouchY = 0;
	protected View mHeader = null;
	protected View mContent = null;
	protected boolean mAutoEnable = false;
	private boolean mScrollEnable=true;
	private boolean mCanDrag;
	private float mTouchSlop;
	private float mLastTouchX;

	private int mScrollHeight =0;

	private onScrollListener mScrollCallback=null;


	public void setScrollCallback(onScrollListener mScrollCallback) {
		this.mScrollCallback = mScrollCallback;
	}

	public DismissHeaderLayout(Context context) {
		this(context, null);
	}

	public DismissHeaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray attr = context.obtainStyledAttributes(attrs,
				R.styleable.DismissHeaderLayout);
		if (attr != null) {
			mHeaderId = attr.getResourceId(R.styleable.DismissHeaderLayout_ab_barId,
					mHeaderId);
			mAutoEnable = attr.getBoolean(
					R.styleable.DismissHeaderLayout_ab_autoenable, mAutoEnable);
			attr.recycle();
		}
		mCurrentY = 0;
		if (mAutoEnable) {
			mScroller = new ABScroller();
		}
		setClickable(true);
		setLongClickable(true);
	}


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int count = getChildCount();
		if (getChildCount() != 2) {
			throw new IllegalArgumentException("two  children only");
		}
		if (mHeaderId != 0) {
			mHeader = findViewById(mHeaderId);
			if (mHeader == null) {
				throw new IllegalArgumentException("can't find Bar !!");
			}
			for(int i=0;i<count;i++){
				if(getChildAt(i)==mHeader){
					continue;
				}else{
					mContent=getChildAt(i);
				}
			}

		} else {
			mHeader=getChildAt(0);
			mContent=getChildAt(1);
		}

	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layoutHead();
		layoutContent();
		mScrollHeight=mHeader.getHeight();
	}

	private void layoutContent() {
		int l=0;
		int r=l+mContent.getMeasuredWidth();
		int t=mHeader.getMeasuredHeight();
		int b=t+mContent.getMeasuredHeight();
		mContent.layout(l,t,r,b);
	}

	private void layoutHead() {
		int l=0;
		int r=l+mHeader.getMeasuredWidth();
		int t=0;
		int b=t+mHeader.getMeasuredHeight();
		mHeader.layout(l,t,r,b);
	}



	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mAutoEnable) {
				mScroller.abortScroll();
			}
			mLastTouchY = ev.getY();
			mLastTouchX = ev.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mScrollEnable) {
				float offsetY = ev.getY() - mLastTouchY;// reverse direction
				float offsetX=ev.getX()-mLastTouchX;
				mLastTouchY = ev.getY();
				mLastTouchX=ev.getX();
				if (Math.abs(offsetY) > Math.abs(offsetX)) {
					doMove(offsetY);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mAutoEnable) {
				doRelease();
			}
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}


	private void doRelease() {
		if (isOverBound()) {
			openBar();
		} else {
			closeBar();
		}
	}
	
	public void openBar(){
		mScroller.start(0, getDuration());
	}
	public void closeBar(){
		mScroller.start(-mScrollHeight, getDuration());
	}
	
	private void doMove(float offsetY) {
		float tempY = MoveTool
				.getBound(mCurrentY, offsetY, -mScrollHeight, 0);
		if(mScrollCallback!=null){
			mScrollCallback.onScroll(offsetY,tempY-mCurrentY,tempY,mCurrentY);
		}
		if (tempY != mCurrentY) {
			mCurrentY = tempY;
			ViewHelper.setTranslationY(mHeader, mCurrentY);
			ViewHelper.setTranslationY(mContent, mCurrentY);
		}
	}
	
	public void setScrollEnable(boolean enable){
		this.mScrollEnable=enable;
	}
	
	

	public class ABScroller implements Runnable {
		private boolean mIsRunning;
		private float mLastY;
		private Scroller mScorller = new Scroller(getContext());

		public void run() {
			boolean finish = !mScorller.computeScrollOffset();
			float offsetY = mScorller.getCurrY() - mLastY;
			if (!finish) {
				mLastY = mScorller.getCurrY();
				doMove(offsetY);
				post(this);
			} else {
				reset();
			}
		}

		public void abortScroll() {
			if (mIsRunning) {
				if (!mScorller.isFinished()) {
					mScorller.forceFinished(true);
				}
			}
		}

		private void reset() {
			mIsRunning = false;
			mLastY = 0f;
			removeCallbacks(this);
		}

		public void start(float targetY, int duration) {

			int offsetY = (int) (targetY - mCurrentY);
			mLastY = 0f;
			mScorller.startScroll(0, 0, 0, offsetY);
			removeCallbacks(this);
			post(this);
		}
	}

	public int getDuration() {
		return (int) (mCurrentY > mScrollHeight / 2 ? (1 - mCurrentY
				/ (mScrollHeight + 1))
				* mDuration : mCurrentY * mDuration / (mScrollHeight + 1));
	}

	public boolean isOverBound() {
		return mCurrentY>-mScrollHeight / 2f;
	}

	public interface onScrollListener{
		void onScroll(float touchOffset, float scrolOffset, float currScrollY, float oldScrollY);
	}
}
