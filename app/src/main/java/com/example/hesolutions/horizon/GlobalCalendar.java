package com.example.hesolutions.horizon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.allin.activity.action.SysApplication;
import com.google.common.collect.HashBiMap;
import com.homa.hls.database.DatabaseManager;
import com.homa.hls.database.Device;
import com.homa.hls.database.Gateway;
import com.homa.hls.datadeal.DevicePacket;
import com.homa.hls.datadeal.Message;
import com.homa.hls.socketconn.DeviceSocket;
import com.mylibrary.WeekViewEvent;
import com.mylibrary.WeekView;
import com.mylibrary.MonthLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GlobalCalendar extends Activity{
    Button addevent;
    Button today;
    Button oneday;
    Button threedays;
    Button sevendays;
    private WeekView mWeekView;
    Handler myHandler;
    Runnable myRunnable;
    String loginname;
    MonthLoader.MonthChangeListener mMonthChangeListener = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_calendar);
        addevent =(Button)findViewById(R.id.addevent);
        today = (Button)findViewById(R.id.today);
        oneday = (Button)findViewById(R.id.oneday);
        threedays = (Button)findViewById(R.id.threedays);
        sevendays = (Button)findViewById(R.id.sevendays);
        mWeekView = (WeekView) findViewById(R.id.weekView);
        loginname = DataManager.getInstance().getUsername();
        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GlobalCalendar.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 3*60*1000);
        mMonthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                // Populate the week view with some events.
                /*
                List<WeekViewEvent> events;
                events = DataManager.getInstance().getevents();
                */
                List<WeekViewEvent> events;
                events = DataManager.getInstance().getnewevents();
                return events;
            }

        };
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(mMonthChangeListener);


        WeekView.EventLongPressListener mEventLongPressListener = new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {

                if (event.getName().equals(loginname))
                {
                    final Gateway gateways = SysApplication.getInstance().getCurrGateway(GlobalCalendar.this);
                    final List<WeekViewEvent> listevent = DataManager.getInstance().getnewevents();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(GlobalCalendar.this.getParent());
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Do you want to remove this event?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (gateways!=null) {
                                Iterator<WeekViewEvent> eventIterator = listevent.iterator();
                                while(eventIterator.hasNext())
                                {
                                    WeekViewEvent event1 = eventIterator.next();
                                    if (event.getId() == event1.getId())
                                    {
                                        eventIterator.remove();
                                        //CheckCurrent(event1);
                                        break;
                                    }
                                }
                                DataManager.getInstance().setnewevents(listevent);
                                dialog.cancel();
                                Intent startNewActivityIntent = new Intent(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                ActivityStack activityStack = (ActivityStack) getParent();
                                activityStack.push("RemoveEvent", startNewActivityIntent);
                            }else
                            {
                                Toast.makeText(GlobalCalendar.this, "Gateway Error, please check the gateway and then try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }else
                {
                    Toast.makeText(GlobalCalendar.this, "Do not have permission!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(mEventLongPressListener);

        WeekView.EventClickListener mEventClickListener = new WeekView.EventClickListener()
        {
            @Override
            public void onEventClick(final WeekViewEvent event, RectF eventRect)
            {
                Toast.makeText(GlobalCalendar.this, "Created by " + event.getName() + "\nStarting at "
                        + event.getStartTime().getTime()+
                        "\nFinishing at " + event.getEndTime().getTime()
                        +"\nName is: " + event.getName() + "\nSectors included: " + event.getdeviceList().toString(), Toast.LENGTH_SHORT).show();
            }

        };
        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(mEventClickListener);


        // not necessary
        /*
        backtouser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewActivityIntent = new Intent(GlobalCalendar.this, UserPage.class);
                startActivity(startNewActivityIntent);
            }
        });
*/
        addevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bitmap = getScreenShot(rootView);
                DataManager.getInstance().setBitmap(bitmap);

                HashMap<String, HashMap<String, ArrayList<Device>>> sector = DataManager.getInstance().getsector();
                HashMap<String, ArrayList<Device>> sectorinformation = sector.get(loginname);
                if (sectorinformation!=null && !sectorinformation.isEmpty()) {
                    Intent startNewActivityIntent = new Intent(GlobalCalendar.this, CalendarTask.class);
                    startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ActivityStack activityStack = (ActivityStack) getParent();
                    activityStack.push("SecondActivity", startNewActivityIntent);
                    DataManager.getInstance().setactivity(activityStack.popid());
                }else{
                    Toast.makeText(GlobalCalendar.this, "You have not been assigned to any sector yet.", Toast.LENGTH_SHORT).show();
                }
                /*
                Intent startNewActivityIntent = new Intent(GlobalCalendar.this, CalendarTask.class);
                startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startNewActivityIntent);
                */
            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeekView.goToToday();
            }
        });
        oneday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneday.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
                threedays.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                sevendays.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                mWeekView.setNumberOfVisibleDays(1);
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
            }
        });
        threedays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threedays.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
                oneday.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                sevendays.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                mWeekView.setNumberOfVisibleDays(3);
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
            }
        });
        sevendays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sevendays.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
                oneday.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                threedays.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                mWeekView.setNumberOfVisibleDays(7);
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));

            }
        });

    }
    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache(),0,100,600,
                screenView.getDrawingCache().getHeight()-100);
        screenView.setDrawingCacheEnabled(false);

        return bitmap;
    }
/*
    public void CheckCurrent(WeekViewEvent event)
    {
        Calendar cur = Calendar.getInstance();
        if (cur.before(event.getEndTime())&&cur.after(event.getStartTime()))
        {
            ArrayList<String> sectorlist = event.getdeviceList();
            String username = event.getName();
            HashMap<String, HashMap<String, ArrayList<Device>>> sector = DataManager.getInstance().getsector();
            HashMap<String, ArrayList<Device>> sectorinformation = sector.get(username);
            for (String sectorname:sectorlist)
            {
                ArrayList<Device> deviceArrayList = sectorinformation.get(sectorname);
                if (deviceArrayList!=null) {
                    for (Device device : deviceArrayList) {
                        Device thedevice = DatabaseManager.getInstance().getDeviceInforName(device.getDeviceName());
                        if (thedevice.getChannelMark() != 5) {
                            byte[] data;
                            data = new byte[]{(byte) 17, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                            thedevice.getDeviceAddress(), (short) 0, data), thedevice.getGatewayMacAddr(), thedevice.getGatewayPassword(),
                                    thedevice.getGatewaySSID(), GlobalCalendar.this));
                            thedevice.setCurrentParams(data);
                            DatabaseManager.getInstance().updateDevice(thedevice);
                        }
                    }
                }
            }
        }
    }
*/

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();
        myHandler.removeCallbacks(myRunnable);
        myHandler.postDelayed(myRunnable,3*60*1000);

    }
    @Override
    public void onResume() {
        super.onResume();
        myHandler.postDelayed(myRunnable, 6*30*1000);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        myHandler.removeCallbacks(myRunnable);
        mWeekView.setMonthChangeListener(mMonthChangeListener);
    }

    @Override
    public void onBackPressed()
    {
        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        myHandler.removeCallbacks(myRunnable);
    }
}
