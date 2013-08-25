package ic.lunar.floatingwidgetspace;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import wei.mark.standout.StandOutWindow;

import java.util.ArrayList;

public class PickAppWidgets extends Activity {

    protected static Intent widget = new Intent();
    protected static int id = 2;

    private final int REQUEST_PICK_APPWIDGET = 0x0754;
    private final int REQUEST_CREATE_APPWIDGET = 0x86c;

    private AppWidgetHost mAppWidgetHost;
    protected static AppWidgetHost smAppWidgetHost;
    private AppWidgetManager mAppWidgetManager;
    protected static AppWidgetManager smAppWidgetManager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        smAppWidgetManager = mAppWidgetManager;
        mAppWidgetHost = new AppWidgetHost(this, R.id.host_widget);
        smAppWidgetHost = mAppWidgetHost;

        selectWidget();
        Toast.makeText(this,"Pick a widget from the list",Toast.LENGTH_LONG).show();
	}


    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent,REQUEST_PICK_APPWIDGET);
    }

    void addEmptyData(Intent pickIntent) {
        ArrayList<AppWidgetProviderInfo> customInfo =
                new ArrayList<AppWidgetProviderInfo>();
        pickIntent.putParcelableArrayListExtra(
                AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
        pickIntent.putParcelableArrayListExtra(
                AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (resultCode == RESULT_OK ) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                configureWidget(data);
            }
            else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                smAppWidgetHost = mAppWidgetHost;
                smAppWidgetManager = mAppWidgetManager;
                widget = data;
                if(widget == null && BuildConfig.DEBUG) Log.e("Floating Widgets","Fasten seat belts and prepare for crash");
                StandOutWindow.show(this,FloatingWidget.class,++id);
                finish();
            }
        }
        else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId =
                    data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }


    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo =
                mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent =
                    new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            smAppWidgetHost = mAppWidgetHost;
            smAppWidgetManager = mAppWidgetManager;
            widget = data;
            if(widget == null) Log.e("Floating Widgets","Fasten seat belts and prepare for crash");
            StandOutWindow.show(this,FloatingWidget.class,++id);
            finish();
        }
    }
}
