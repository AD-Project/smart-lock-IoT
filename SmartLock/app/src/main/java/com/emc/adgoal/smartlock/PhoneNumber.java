package com.emc.adgoal.smartlock;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by ravoop on 9/14/2016.
 */
public class PhoneNumber {

    public PhoneNumber(Context applicationContext)
    {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        //m_phoneNumber = "9986224629";
        m_phoneNumber = myDevice.getName();
    }

    public String getPhoneNumber() {
        return m_phoneNumber;
    }

    public PhoneNumber(String deviceName)
    {
        m_phoneNumber = deviceName;
    }

    @Override
    public String toString()
    {
        return getPhoneNumber();
    }

    private String m_phoneNumber;
}
