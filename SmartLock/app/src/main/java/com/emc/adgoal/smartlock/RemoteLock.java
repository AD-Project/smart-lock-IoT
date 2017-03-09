package com.emc.adgoal.smartlock;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ravoop on 9/15/2016.
 */
public class RemoteLock {

    public RemoteLock(PhoneNumber phoneNumber, String ipAddress, int port)
    {
        this.phoneNumber = phoneNumber;
        this.lockNumber = "KA 01 HJ 9758";
        this.locked = true;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public boolean lock()
    {
        RestClient restClient = new RestClient(getBaseLockUrl() + "/lock_lock" );
        try
        {
            JSONObject response = restClient.doGet(null, getReqParams());
            setLocked(true);
            Log.i(module, "Lock " + getLockNumber() + " locked from phone number " + getPhoneNumber() + "!");
            Log.i(module, "Response: " + response.toString());
            return true;
        }
        catch (Exception e)
        {
            Log.e(module, "Failed to lock: " + e.getMessage());
        }
        return false;
    }

    public boolean unlock()
    {
        RestClient restClient = new RestClient(getBaseLockUrl() + "/lock_unlock" );
        try
        {
            JSONObject response = restClient.doGet(null, getReqParams());
            setLocked(false);
            Log.i(module, "Lock " + getLockNumber() + " unlocked from phone number " + getPhoneNumber() + "!");
            return true;
        }
        catch (Exception e)
        {
            Log.e(module, "Failed to unlock: " + e.getMessage());
        }
        return false;
    }

    public boolean addToAcl(PhoneNumber phoneNumber)
    {
        RestClient restClient = new RestClient(getBaseLockUrl() + "/acl_add" );
        try
        {
            Map<String,String> reqParams = getReqParams();
            reqParams.put("newuser",phoneNumber.getPhoneNumber());
            JSONObject response = restClient.doGet(null, reqParams);
            Log.i(module, "Number " + phoneNumber.getPhoneNumber() + " unlocked from phone number " + getPhoneNumber() + "!");
            return true;
        }
        catch (Exception e)
        {
            Log.e(module, "Failed to add to ACL: " + e.getMessage());
        }
        return false;
    }

    public boolean removeFromAcl(PhoneNumber phoneNumber)
    {
        RestClient restClient = new RestClient(getBaseLockUrl() + "/acl_delete" );
        try
        {
            Map<String,String> reqParams = getReqParams();
            reqParams.put("deleteuser",phoneNumber.getPhoneNumber());
            JSONObject response = restClient.doGet(null, reqParams);
            Log.i(module, "Number " + phoneNumber.getPhoneNumber() + " lost access "+ "!");
            return true;
        }
        catch (Exception e)
        {
            Log.e(module, "Failed to remove from ACL: " + e.getMessage());
        }
        return false;
    }

    public List<PhoneNumber> getAcl()
    {
        RestClient restClient = new RestClient(getBaseLockUrl() + "/acl_report");
        List<PhoneNumber> acl = new ArrayList<PhoneNumber>();
        try
        {
            JSONObject response = restClient.doGet(null, getReqParams());
            JSONArray array = response.getJSONArray("acl_list");
            for ( int i = 0; i < array.length(); ++i )
            {
                PhoneNumber newPhone = new PhoneNumber(array.getString(i));
                if (!newPhone.getPhoneNumber().isEmpty()) {
                    acl.add(newPhone);
                }
            }
            Log.i(module, "Fetched back " + acl.size() + " objects");
        }
        catch (Exception e)
        {
            Log.e(module, "Failed to get ACL: " + e.getMessage());
        }
        return acl;
    }

    private Map<String, String> getReqParams()
    {
        Map<String, String> map = new HashMap<String, String>(1);
        map.put("ph", getPhoneNumber().toString());
        return map;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLockNumber() {
        return lockNumber;
    }

    public boolean isLocked(Context context) throws Exception
    {
        updateLock(context);
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    private void updateLock(Context context) throws Exception
    {
        RestClient restClient = new RestClient(getBaseLockUrl() + "/lock_status" );
        try
        {
            JSONObject response = restClient.doGet(null, getReqParams());
            Toast.makeText(context, "Response: " + response.toString(), Toast.LENGTH_SHORT);
            Log.i(module, "Response: " + response.toString());
            String status = (String)response.get("status");
            if ( status.equals("locked")) {
                setLocked(true);
            }
            else
            {
                setLocked(false);
            }
        }
        catch (Exception e)
        {
            Log.e(module, "Failed to get status: " + e.getMessage());
            setLocked(true);
            throw e;
        }
    }

    private String getBaseLockUrl()
    {
        return "http://" + ipAddress + ":" + Integer.toString(port);
    }

    private PhoneNumber phoneNumber;
    private String lockNumber;
    private static final String module = "RmtLck";
    private boolean locked;
    private String ipAddress;
    private int     port;
}
