package com.hnj.as_usbxpdemo;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.hnj.dp_nusblist.USBFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class USBUtil {
    private UsbManager mUsbManager ;
    private HashMap<String, UsbDevice> deviceList;
    private Iterator<UsbDevice> deviceIterator ;
    private USBFactory usbfactory;
    private Context mcontext;
    private Handler mHandler;
    private final int CONNECTRESULT= 0x05;
    private TextView usbconnt_tv;
    public void init(Context context, TextView musbconnt_tv)
    {
        mcontext=context;
        usbconnt_tv=musbconnt_tv;
        mHandler=new MHandler();
        usbfactory= USBFactory.getUsbFactory(mHandler);
        mUsbManager = (UsbManager)mcontext.getSystemService(Context.USB_SERVICE);
    }
    public USBFactory GetUSBFactory()
    {
        return usbfactory;
    }
    public void CloseUSB()
    {
        if(usbfactory!=null)
        {
            usbfactory.CloseUSB();
        }
    }

    public ArrayList<String> getUSBDevice()
    {

        mUsbManager = (UsbManager) mcontext.getApplicationContext().getSystemService(Context.USB_SERVICE);
        deviceList = mUsbManager.getDeviceList();
        deviceIterator = deviceList.values().iterator();
        ArrayList<String> Adevice =new ArrayList<String>();
        if (deviceList.size() > 0) {
            // 初始化选择对话框布局，并添加按钮和事件
            while (deviceIterator.hasNext()) { // 这里是if不是while，说明我只想支持一种device
                final UsbDevice device = deviceIterator.next();
                device.getInterfaceCount();
                int vid=device.getVendorId();
                int pid=device.getProductId();
                Adevice.add(""+vid+":"+pid);
            }
        }

        return Adevice;
    }
    private boolean accredit=false;
    private boolean isfind=false;//是否找到对应打印机
    private String VID,PID;
    //自动搜索并连接USB设备
    @SuppressLint("RtlHardcoded")
    public void PrinterConnect() {

        if(usbfactory.connectstate)
        {
            return;
        }
        int mvid=SharedPreferencesUtil.getVID();//获取上次连接成功打印机的VID
        int mpid=SharedPreferencesUtil.getPID();//获取上次连接成功打印机的PID
        isfind=false;
        deviceList = mUsbManager.getDeviceList();
        deviceIterator = deviceList.values().iterator();
        if (deviceList.size() > 0) {
            // 初始化选择对话框布局，并添加按钮和事件
            while (deviceIterator.hasNext()) { // 这里是if不是while，说明我只想支持一种device
                final UsbDevice device = deviceIterator.next();
                device.getInterfaceCount();
                int vid=device.getVendorId();
                int pid=device.getProductId();

                if(vid==mvid&&pid==mpid||(vid==1157&&pid==22337)||(vid==1155&&pid==22336)||(vid==4070&&pid==33054)||(vid==19267&&pid==13624))//判断是不是打印机编号
                {
                    isfind=true;
                    PendingIntent mPermissionIntent ;

                    mPermissionIntent = PendingIntent.getBroadcast(mcontext.getApplicationContext(), 0, new Intent(mcontext.getApplicationInfo().packageName), PendingIntent.FLAG_IMMUTABLE);

                    if (!mUsbManager.hasPermission(device)) {
                        if(!accredit)
                        {
                            mUsbManager.requestPermission(device,mPermissionIntent);
                            accredit=true;
                        }

                        mHandler.sendEmptyMessageDelayed(CONNECTRESULT, 2000);
                    } else {
                        accredit=false;
                       boolean t=usbfactory.connectUsb(mUsbManager, device);
                        if(t)
                        {
                            VID=vid+"";
                            PID=pid+"";
                            mHandler.removeMessages(CONNECT);
                            //连接成功在这里调用打印的方法
                        }

                        mHandler.removeMessages(CONNECTRESULT);
                        mHandler.sendEmptyMessageDelayed(CONNECTRESULT, 2000);
                    }
                }
            }
            if(!isfind)//没有搜索到对应打印机继续搜索
            {
                mHandler.removeMessages(CONNECT);
                mHandler.sendEmptyMessageDelayed(CONNECT, 1000);
            }
        }

    }

    private final int CONNECT=0x25;

    @SuppressLint("HandlerLeak")
    class MHandler extends Handler {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTRESULT: {
                    if(usbfactory!=null)
                    {
                        if(usbfactory.getconnectstate())
                        {
                            usbfactory.is_connectdevice();
                            usbconnt_tv.setText("打印机连接成功   ["+"VID:"+VID+",PID:"+PID+"]");
                            mHandler.removeMessages(CONNECTRESULT);
                            mHandler.sendEmptyMessageDelayed(CONNECTRESULT, 2000);
//							 Log.e("---------", "==============连接成功=======1111");
                        }else {
                            usbconnt_tv.setText("打印机连接失败");
                            mHandler.removeMessages(CONNECT);
                            mHandler.sendEmptyMessageDelayed(CONNECT,100);
                            Log.e("---------", "==============连接失败=======000");
                        }
                    }

                    break;
                }
                case CONNECT:{
                    PrinterConnect();//自动连接
                    break;
                }
                case USBFactory.CHECKPAGE_RESULT:{
                    if(msg.getData().getString("state").equals("1"))
                    {
//							Toast_Util.ToastString(getApplicationContext(), "printer has paper");//打印机有纸
                        MainActivity.Paperstate=true;

                    }else {
                        MainActivity.Paperstate=false;
							Toast_Util.ToastString(mcontext.getApplicationContext(), "打印机缺纸");//打印机没有纸
                    }
                    break;
                }
                case USBFactory.PRINTSTATE:{
                    boolean printstate=msg.getData().getBoolean("printstate");
                    if(printstate)
                    {
                        Toast_Util.ToastString(mcontext.getApplicationContext(), "打印完成");//打印完成
                    }else
                    {
                        Toast_Util.ToastString(mcontext.getApplicationContext(), "打印失败");//打印失败
                    }

                    break;
                }
            }
        }
    }


}
