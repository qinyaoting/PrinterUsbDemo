package com.hnj.as_usbxpdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private LinearLayout linearLayoutUSBDevices;
    private Button search_print_bt,ptprint_bt,closeusb_bt;
    private TextView usbconnt_tv;
    public static USBUtil usbUtil;
    public static boolean Paperstate=false;
    private ArrayList<String> usblist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        SharedPreferencesUtil.initPreferences(this);
        usbconnt_tv=(TextView)findViewById(R.id.usbconnt_tv);
        search_print_bt=(Button)findViewById(R.id.search_print_bt);
        ptprint_bt=(Button)findViewById(R.id.ptprint_bt);
        closeusb_bt=(Button)findViewById(R.id.closeusb_bt);
        linearLayoutUSBDevices = (LinearLayout) findViewById(R.id.linearLayoutUSBDevices);
        usbUtil=new USBUtil();
        usbUtil.init(this,usbconnt_tv);
        search_print_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                linearLayoutUSBDevices.removeAllViews();
                ArrayList<String> deviceS=usbUtil.getUSBDevice();
                for(int i=0;i<deviceS.size();i++)
                {
                    Button btDevice = new Button(
                            linearLayoutUSBDevices.getContext());
                    btDevice.setGravity(Gravity.CENTER_VERTICAL
                            | Gravity.LEFT);
                    String[] USBID=deviceS.get(i).split(":");
                    btDevice.setText("USB设备：VID="+USBID[0]+"   PID="+USBID[1]);
                    btDevice.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            SharedPreferencesUtil.saveVID(Integer.parseInt(USBID[0]));
                            SharedPreferencesUtil.savePID(Integer.parseInt(USBID[1]));
                            linearLayoutUSBDevices.removeAllViews();
                        }
                    });
                    linearLayoutUSBDevices.addView(btDevice);
                }
            }
        });
        closeusb_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    usbUtil.CloseUSB(); //关闭USB
            }
        });

        ptprint_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,NormalPrintActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        usbUtil.PrinterConnect();
    }

}