package com.touchscreen_tester;  
/* 
   This file implements the menu.  The main file is "TesterView.java", which implements finger tracking
   
   R Kakarala, started 21 July 2011
   */

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class TouchscreenTesterActivity extends Activity {

    /** Menu ID for the command to clear the window. */
    private static final int CLEAR_ID = Menu.FIRST;
    /** Menu ID for the command to toggle fading. */
    private static final int SAMPLES_ID = Menu.FIRST+1;
    /** Menu ID for about **/
    private static final int ABOUT_ID = Menu.FIRST+2;

    /** The view responsible for drawing the window. */
    TesterView mView;
 
    
    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* make the activity full screen */
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);   
        
        // Create and attach the view that is responsible for painting.
        mView = new TesterView(this);
        setContentView(mView);
        mView.requestFocus();

        // Restore the fading option if we are being thawed from a
        // previously saved state.  Note that we are not currently remembering
        // the contents of the bitmap.
        // by default, fading is off.  
        
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CLEAR_ID, 0, "Clear");
        menu.add(0, SAMPLES_ID, 0, "Samples").setCheckable(true);
        menu.add(0, ABOUT_ID, 0, "Version "+this.getString(R.string.Version)); /* idea from http://www.connorgarvey.com/blog/?p=9 */
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
     //   menu.findItem(SAMPLES_ID).setChecked(mSamples);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CLEAR_ID:
                mView.clear();
                return true;
            case SAMPLES_ID:
            	mView.bShowSamples = !mView.bShowSamples;
             	return true;
            case ABOUT_ID:
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        // If fading mode is enabled, then as long as we are resumed we want
        // to run pulse to fade the contents.
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save away the fading state to restore if needed later.  Note that
        // we do not currently save the contents of the display.
    }

    @Override protected void onPause() {
        super.onPause();
        // Make sure to never run the fading pulse while we are paused or
        // stopped.
      }

  
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                // Upon receiving the fade pulse, we have the view perform a
                // fade and then enqueue a new message to pulse at the desired
                // next time.
                default:
                    super.handleMessage(msg);
            }
        }
    };

   
}