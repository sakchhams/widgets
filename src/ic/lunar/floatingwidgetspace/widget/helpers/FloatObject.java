package ic.lunar.floatingwidgetspace.widget.helpers;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import ic.lunar.floatingwidgetspace.FloatingWidget;
import ic.lunar.floatingwidgetspace.R;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

import java.util.Set;

/**
 * Created by Sakchham on 7/19/13.
 */
public class FloatObject extends StandOutWindow
{
    //Unique id designated to to minimized icon
    public static int FLOAT_UNIQUE_ID = 2009;
    //Stores window state minimized or visible
    public static boolean IS_MINIMIZED = false;
    //Request Code to stop the FloatObject service
    public static int REQUEST_STOP = 888;


    //Default standout-window callbacks NOTHING TO DO
    @Override
    public String getAppName() {
        return null;
    }

    //Default standout-window callbacks NOTHING TO DO
    @Override
    public int getAppIcon() {
        return 0;
    }

    //Default standout-window callbacks NOTHING TO DO
    @Override
    public void createAndAttachView(int id, FrameLayout frame) {

    }

    //Attach the view to RelativeLayout to make it visible
    @Override
    public void createAndAttachView(int id, RelativeLayout relativeLayout) {

        Drawable drawable = getResources().getDrawable(R.drawable.floating_icon);
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(drawable);
        relativeLayout.addView(imageView);

        IS_MINIMIZED = true;
    }

    //Layout parameters for the object
    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        Drawable drawable = getResources().getDrawable(R.drawable.floating_icon);
        int width = drawable.getMinimumWidth();
        int height = drawable.getMinimumHeight();
        return new StandOutLayoutParams(id,width,height,StandOutLayoutParams.RIGHT,StandOutLayoutParams.TOP);
    }

    //Callback on double tap of the window
    //Show all hidden windows on double tap
    @Override
    public boolean onDoubleTap(int id, Window window, MotionEvent event) {
        Set<Integer> ids = StandOutWindow.sWindowCache.getCacheIds(FloatingWidget.class);
        for (int i: ids)
        {
            if (i != FLOAT_UNIQUE_ID)
            {
                //Send data to the Floating Widget service to restore the hidden Widgets
                getStandOutContext().sendData(FLOAT_UNIQUE_ID,FloatingWidget.class,i,0,null);
                IS_MINIMIZED = false;
                break;
            }
        }
        stopSelf();
        return super.onDoubleTap(id, window, event);
    }

    //Revert the flag on close
    @Override
    public void onDestroy() {
        super.onDestroy();
        IS_MINIMIZED = false;
    }

    //On Request stop by FloatingWindow service
    @Override
    public void onReceiveData(int id, int requestCode, Bundle data, Class<? extends StandOutWindow> fromCls, int fromId) {
        if (fromCls == FloatingWidget.class && requestCode == REQUEST_STOP)
            IS_MINIMIZED = false;
            stopSelf();
    }

    //Stand-out window flags
    @Override
    public int getFlags(int id) {
        return StandOutFlags.FLAG_DECORATION_MAXIMIZE_DISABLE
                | StandOutFlags.FLAG_BODY_MOVE_ENABLE
                | StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
                | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                | StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE
                ;
    }
}
