package com.touchscreen_tester;
/* Shows finger locations and traces their trajectories on touchscreen. Notes
 *   1) Allows up to 20 fingers to be tracked. Not sure how many the hardware controller supports.
 *   2) Based on "TouchPaint.java" provided by Android samples, similar to DevTools "PointerLocation" (but not as good!)
 *   To do: 
 *   	o Use pointer IDs from getPointerID to make sure fingers are not confused
 *      o Different colors for different fingers
 *      o Circles showing major axis size
 *      o replace Fade with save points to cursor.csv 
 * R Kakarala: Started 21 July 2011
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;


public class TesterView extends View {
	private static final int MAXFINGERS = 20; // count the toes too! 

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private final Rect mRect = new Rect();
	private final Paint mPaint;
	private float mCurX;
	private float mCurY;
	private float[] mPrevX = new float[MAXFINGERS]; // previous values of x,y to draw line
	private float[] mPrevY = new float[MAXFINGERS];
	
	public boolean bShowSamples = true;
	
	public TesterView(Context c) {
		super(c);
		setFocusable(true);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 255, 255, 255);
	}

	/* if clear command under App's view Menu is used */
	public void clear() {
		if (mCanvas != null) {
			mPaint.setARGB(0xff, 255, 255, 255);
			mCanvas.drawPaint(mPaint);
			invalidate();
		}
	}

	@Override protected void onSizeChanged(int w, int h, int oldw,
			int oldh) {
		int curW = mBitmap != null ? mBitmap.getWidth() : 0;
		int curH = mBitmap != null ? mBitmap.getHeight() : 0;
		if (curW >= w && curH >= h) {
			return;
		}

		if (curW < w) curW = w;
		if (curH < h) curH = h;

		Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
				Bitmap.Config.RGB_565);
		newBitmap.eraseColor(Color.WHITE);  /* screen goes white */
		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (mBitmap != null) {
			newCanvas.drawBitmap(mBitmap, 0, 0,   null); 
		} 
		mBitmap = newBitmap;
		mCanvas = newCanvas;	
			
		/* initialize previous finger locations so we know later they need to be filled for the first time */
		for (int finger=0; finger<MAXFINGERS; finger++){
			mPrevX[finger]=-1;
			mPrevY[finger]=-1;
		}
	}

	@Override protected void onDraw(Canvas canvas) {
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, 0, 0, null);
		} 
	}

	@Override public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
        int numPtrs;
		switch (action) {

		case MotionEvent.ACTION_DOWN:  /* primary pointer */
		case MotionEvent.ACTION_POINTER_DOWN: /* any subsequent pointer */
		    numPtrs = event.getPointerCount();
			for (int finger = 0; finger < numPtrs; finger++) {
				mPrevX[finger] = mPrevY[finger] = -1;
			}
			break;
		case MotionEvent.ACTION_MOVE: /* any number of pointers move */
			int N = event.getHistorySize();
			numPtrs = event.getPointerCount();
			for (int histndx = 0; histndx < N; histndx++) {
				for (int finger = 0; finger < numPtrs; finger++) { 
					mCurX = event.getHistoricalX(finger, histndx);
					mCurY = event.getHistoricalY(finger, histndx);
					drawPoint(mCurX, mCurY,
							event.getHistoricalPressure(finger, histndx),
							event.getHistoricalSize(finger, histndx), finger); 
				}
			}  
			for (int finger = 0; finger < numPtrs; finger++) {
				mCurX = event.getX(finger);
				mCurY = event.getY(finger);
				drawPoint(mCurX, mCurY, event.getPressure(finger), event.getSize(finger),finger);
			} 
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP: /* all pointers are up */
		case MotionEvent.ACTION_CANCEL: 

			for (int finger=0; finger<MAXFINGERS; finger++){
				mPrevX[finger]=-1;
				mPrevY[finger]=-1;
			}

			break;
		}

		return true;
	}
	
	private void drawLine(int finger) {
		mPaint.setARGB(255,0,0,255);  /* blue */
		mCanvas.drawLine(mPrevX[finger],mPrevY[finger],mCurX,mCurY,mPaint);
	}
	
	private void drawPoint(float x, float y, float pressure, float width, int finger) {
		//Log.i("TouchPaint", "Drawing: " + x + "x" + y + " p="
		//        + pressure + " width=" + width);
	
		int lowX,lowY,highX,highY;
		if (mBitmap != null) {
			float radius = (float) 2.0; 		
			mPaint.setARGB(255,255,0,0);  
			if (bShowSamples)
				mCanvas.drawCircle(x, y, radius, mPaint);  // current point
			if ((mPrevX[finger]==-1 && mPrevY[finger]==-1)) { /* we don't need to draw a line */
				mPrevX[finger] = x;
				mPrevY[finger] = y;
				mRect.set((int) (x - 3*radius), (int) (y - 3*radius),
	                      (int) (x + 3*radius), (int) (y + 3*radius));
			} else {
				/* we do need to draw line as we have a valid previous location */
				drawLine(finger);
				if (mPrevX[finger] <= mCurX){ 
					lowX = (int) (mPrevX[finger] - 3*radius);
					highX = (int) (mCurX + 3*radius); 
				} else {
					lowX = (int) (mCurX - 3*radius);
					highX = (int) (mPrevX[finger] + 3*radius); 
				}
				
				if (mPrevY[finger] <= mCurY){ 
					lowY = (int) (mPrevY[finger] - 3*radius);
					highY = (int) (mCurY + 3*radius); 
				} else {
					lowY = (int) (mCurY - 3*radius);
					highY = (int) (mPrevY[finger] + 3*radius); 
				}
				mRect.set(lowX,lowY,highX,highY);
				mPrevX[finger] = mCurX;
				mPrevY[finger] = mCurY;
			}
			
	    
			invalidate(mRect); 
		}
	}
	
	
}

