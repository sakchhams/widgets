package ic.lunar.floatingwidgetspace;

import android.app.Notification;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import ic.lunar.floatingwidgetspace.widget.helpers.FloatObject;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

import java.util.Set;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.*;
import static ic.lunar.floatingwidgetspace.widget.helpers.FloatObject.FLOAT_UNIQUE_ID;

/**
 * Created by Sak on 6/24/13.
 */
public class FloatingWidget extends StandOutWindow {


    AppWidgetHost mAppWidgetHost;
    AppWidgetManager mAppWidgetManager ;
    StandOutWindow context;
    RelativeLayout relLayout = null;
    float alpha = 1.0f;
    


    @Override
    public int getThemeStyle() {
        return android.R.style.Theme_Light;
    }

    @Override
    public String getPersistentNotificationTitle(int id) {
        return super.getPersistentNotificationTitle(id);
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return super.getPersistentNotificationMessage(id);
    }


    @Override
    public int getHiddenIcon() {
        return -1;
    }

    @Override
    public String getHiddenNotificationTitle(int id) {
        return null;
    }

    @Override
    public String getHiddenNotificationMessage(int id) {
        return null;
    }

    @Override
    public Intent getHiddenNotificationIntent(int id) {
        return null;
    }

    @Override
    public Notification getHiddenNotification(int id) {
        return null;
    }

    @Override
    public String getAppName() {
        return "Widgets Everywhere";
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
    }


    //TODO : Need to get double tap event before it is passed to the AppWidget
    @Override
    public boolean onDoubleTap(int id, Window window, MotionEvent event) {

        new WidgetResizeFrame(getWindow(id),id);
        return super.onDoubleTap(id, window, event);

    }

    @Override
    public boolean onKeyEvent(int id, Window window, KeyEvent event) {
        if ((event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP && event.getRepeatCount() <= 2))
        {
            alpha += 0.10f;
            if(alpha > 1)alpha=1.0f;
            // ? change alpha values below HoneyComb
        }
        else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN && event.getRepeatCount() <= 2)
        {
            alpha -= 0.10f;
            if(alpha < 0.30f)alpha=0.30f;
            // ? change alpha values below HoneyComb
        }
        return super.onKeyEvent(id, window, event);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new FloatingWidgetBinder();
    }

    public class FloatingWidgetBinder extends Binder
    {
        FloatingWidget getService()
        {
            return FloatingWidget.this;
        }
    }

    @Override
    public void createAndAttachView(int id, RelativeLayout relativeLayout) {

        context = getStandOutContext();
        relLayout = relativeLayout;
        mAppWidgetManager = PickAppWidgets.smAppWidgetManager;
        mAppWidgetHost = PickAppWidgets.smAppWidgetHost;
        if (PickAppWidgets.widget == null) {
            throw new AssertionError();
        }
        createWidget(PickAppWidgets.widget);
    }

    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        RelativeLayout relativeLayout = relLayout;
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo =
                mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView =
                mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);


        RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
        rlps.addRule(CENTER_IN_PARENT);

        relativeLayout.addView(hostView,rlps);
        mAppWidgetHost.startListening();
        Log.v("WidgetsEvery0", appWidgetInfo.label);
   }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppWidgetHost.stopListening();
    }


    @Override
    public boolean onClose(int id, Window window) {
        return super.onClose(id, window);
    }

    @Override
    public int getFlags(int id) {
        return StandOutFlags.FLAG_DECORATION_MAXIMIZE_DISABLE
                | StandOutFlags.FLAG_BODY_MOVE_ENABLE
                | StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
                | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
                | StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE
                ;
    }


    @Override
    public boolean onShow(int id, Window window,boolean cached) {
        return super.onShow(id, window,cached);
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        return new StandOutLayoutParams(id, 100, 100,
                StandOutLayoutParams.AUTO_POSITION,
                StandOutLayoutParams.AUTO_POSITION, 100, 100);
    }

    @Override
    public Animation getShowAnimation(int id) {
        if (isExistingId(id)) {
            // restore
            return AnimationUtils.loadAnimation(this,
                    R.anim.fadein);
        } else {
            // show
            return super.getShowAnimation(id);
        }
    }

    @Override
    public Animation getHideAnimation(int id) {
        return AnimationUtils.loadAnimation(this,
                R.anim.fadeout);
    }

    //On recieving data from FloatingObject service
    @Override
    public void onReceiveData(int id, int requestCode, Bundle data, Class<? extends StandOutWindow> fromCls, int fromId) {
        if (fromCls == FloatObject.class && requestCode == 0)
        {
            Set<Integer> active_ids = getExistingIds();
            for (int i : active_ids)
            {
                getStandOutContext().show(i);
            }
        }
    }

    private class WidgetResizeFrame
    {
        /*
          * Widget Resize frame to resize,minimize,close a widget and/or to add a new widget to screen
          * Triggered on long press
         */
        private WidgetResizeFrame(Window window,int id)
        {
            startWidgetResize(window,id);
        }

        private void startWidgetResize(final Window window,final int id)
        {

            //Show the settings dock
            View dock = getStandOutContext().mLayoutInflater.inflate(R.layout.settings_dock,null);
            final RelativeLayout layout = new RelativeLayout(FloatingWidget.this);
            RelativeLayout.LayoutParams paramss = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
            paramss.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layout.addView(dock,paramss);
            StandOutLayoutParams params = new StandOutLayoutParams(DEFAULT_ID,MATCH_PARENT,WRAP_CONTENT,StandOutLayoutParams.CENTER,StandOutLayoutParams.BOTTOM);
            getStandOutContext().mWindowManager.addView(layout,params);

            //Display the re-size frame around widgets
            final RelativeLayout relativeLayout = (RelativeLayout) window.findViewById(R.id.host_widget);
            relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.resize_rame));

            //Add the drag handle for x-axis
            final ImageView resize_x = new ImageView(FloatingWidget.this);
            resize_x.setImageResource(R.drawable.resize_x);
            RelativeLayout.LayoutParams paramsResizeX = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            paramsResizeX.addRule(ALIGN_PARENT_LEFT);
            paramsResizeX.addRule(CENTER_VERTICAL);
            relativeLayout.addView(resize_x, paramsResizeX);

            //Add the drag handle for y-axis
            final ImageView resize_y = new ImageView(FloatingWidget.this);
            resize_y.setImageResource(R.drawable.resize_y);
            RelativeLayout.LayoutParams paramsResizeY = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            paramsResizeY.addRule(ALIGN_PARENT_TOP);
            paramsResizeY.addRule(CENTER_HORIZONTAL);
            relativeLayout.addView(resize_y,paramsResizeY);

            //Add the finished button to remove the resize options
            final ImageView resize_done = new ImageView(FloatingWidget.this);
            resize_done.setImageResource(R.drawable.resize_ok);
            RelativeLayout.LayoutParams paramsResizeDone = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            paramsResizeDone.addRule(ALIGN_PARENT_TOP);
            paramsResizeDone.addRule(ALIGN_PARENT_RIGHT);
            relativeLayout.addView(resize_done,paramsResizeDone);


            // on resize along x-axis
            // TODO : This code needs to be worked on
            OnTouchListener dragResizeX = new OnTouchListener() {

                float x = 0,x2 = 0,dist = 0;
                final int minWidth = ((AppWidgetHostView)relativeLayout.getChildAt(0)).getAppWidgetInfo().minWidth;

                private void resizeWindow(float disty)
                {
                    disty += window.getWidth();
                    int dist = Math.round(disty);
                    if (dist < minWidth)dist = minWidth;
                    window.edit().setSize(dist,window.getHeight()).commit();

                }

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        x = motionEvent.getX();
                        System.out.println("Action Down: \n X - " +x);
                        return true;
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        x2 = motionEvent.getX();
                        if (x2 > x)
                        {
                            //Motion up
                            dist = x2-x;
                            if (dist > 0)dist *= -1;
                            System.out.println("Dist "+dist);
                            resizeWindow(dist);

                        }
                        else if (x > x2)
                        {
                            // Motion Down
                            dist = x-x2;
                            if (dist < 0)dist *=-1;
                            System.out.println("Dist "+dist);
                            resizeWindow(dist);
                        }
                    }
                    return true;
                }
            };

            // on resize along y-axis
            // TODO : This code needs to be worked on
            OnTouchListener dragResizeY = new OnTouchListener() {
                float y = 0,y2 = 0,dist = 0;
                final int minHeight = ((AppWidgetHostView)relativeLayout.getChildAt(0)).getAppWidgetInfo().minHeight;

                private void resizeWindow(float disty)
                {
                    disty += window.getHeight();
                    int dist = Math.round(disty);
                    if (dist < minHeight)dist = minHeight;
                    window.edit().setSize(window.getWidth(),dist).commit();
                }

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        y = motionEvent.getY();
                        System.out.println("Action Down: \n Y - " +y);
                        return true;
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        y2 = motionEvent.getY();
                        if (y2 > y)
                        {
                            //Motion up
                            dist = y2-y;
                            if (dist > 0)dist *= -1;
                            System.out.println("Dist "+dist);
                            resizeWindow(dist);

                        }
                        else if (y > y2)
                        {
                            // Motion Down
                            dist = y-y2;
                            if (dist < 0)dist *=-1;
                            System.out.println("Dist "+dist);
                            resizeWindow(dist);
                        }
                    }
                    return true;
                }
            };

            // on add more widgetsClicked
            layout.findViewById(R.id.add_button).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Remove the resize frame
                    removeViews(relativeLayout,layout,id,window,resize_x,resize_y,resize_done);
                    // Minimize all active widgets
                    Set<Integer> active_id = getExistingIds();
                    for (Integer i : active_id)
                    {
                        getStandOutContext().hide(i);
                    }
                    StandOutWindow.show(getStandOutContext(),FloatObject.class, FLOAT_UNIQUE_ID);
                    // start app widget picker
                    getStandOutContext().startActivity(new Intent(getApplicationContext(),PickAppWidgets.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });

            // on close button click
            layout.findViewById(R.id.close_button).setOnClickListener(new OnClickListener() {

                private void removeView(RelativeLayout main,RelativeLayout dock,int id,Window window,ImageView... view)
                {
                    if (Build.VERSION.SDK_INT >= 11)
                        window.setLayoutParams(new StandOutLayoutParams(id,window.getWidth(),window.getHeight(),Math.round(window.getX()),Math.round(window.getY())));
                    else
                        window.setLayoutParams(new StandOutLayoutParams(id,window.getWidth(),window.getHeight()));
                    StandOutWindow.sWindowCache.putCache(id,FloatingWidget.class,window);
                    getStandOutContext().mWindowManager.removeView(dock);
                    main.setBackgroundDrawable(null);
                    for (int i = 0;i < view.length;i++)
                    {
                        main.removeView(view[i]);
                        view[i].invalidate();
                    }
                    getStandOutContext().close(id);
                }

                @Override
                public void onClick(View view) {
                    this.removeView(relativeLayout,layout,id,window,resize_x,resize_y,resize_done);
                }
            });

            //on hide button click
            layout.findViewById(R.id.hide_button).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Remove the configuration views
                    removeViews(relativeLayout,layout,id,window,resize_x,resize_y,resize_done);
                    //Hide all active windows
                    Set<Integer> active_id = getExistingIds();
                    for (Integer i : active_id)
                    {
                        getStandOutContext().hide(i);
                    }
                    //Show the minimized icon
                    StandOutWindow.show(getStandOutContext(),FloatObject.class, FLOAT_UNIQUE_ID);
                }
            });


            layout.findViewById(R.id.set_button).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                     // Settings activity under construction
                }
            });

            //register the on touch listners for various views
            resize_x.setOnTouchListener(dragResizeX);

            resize_y.setOnTouchListener(dragResizeY);

            resize_done.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    removeViews(relativeLayout,layout,id,window,resize_x,resize_y,resize_done);

                }
            });
        }

        private void removeViews(RelativeLayout main,RelativeLayout dock,int id,Window window,ImageView... view)
        {
            if (Build.VERSION.SDK_INT >= 11)
                window.setLayoutParams(new StandOutLayoutParams(id,window.getWidth(),window.getHeight(),Math.round(window.getX()),Math.round(window.getY())));
            else
                window.setLayoutParams(new StandOutLayoutParams(id,window.getWidth(),window.getHeight()));
            StandOutWindow.sWindowCache.putCache(id,FloatingWidget.class,window);
            getStandOutContext().mWindowManager.removeView(dock);
            main.setBackgroundDrawable(null);
            for (int i = 0;i < view.length;i++)
            {
                main.removeView(view[i]);
                view[i].invalidate();
            }
        }
    }

}
