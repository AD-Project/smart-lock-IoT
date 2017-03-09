package com.emc.adgoal.smartlock;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    private PhoneListAdapter adapter;
    RemoteLock remoteLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayUseLogoEnabled(false);

        //PhoneNumber ph = new PhoneNumber("9902444588");
        PhoneNumber ph = new PhoneNumber(getApplicationContext());
        this.remoteLock = new RemoteLock(ph, "192.168.43.252", 80);
        customToast("Phone Number: " + ph.getPhoneNumber());

        setupListViewAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.acl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addButton:
                addAccess();
                break;
            case R.id.Sync:
                syncPhoneList();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void removeAccess(View v) {
        PhoneNumber itemToRemove = (PhoneNumber) v.getTag();
        if (true == remoteLock.removeFromAcl(itemToRemove))
        {
            adapter.remove(itemToRemove);
            customToast("Removed " + itemToRemove + " successfully");
        }
        else
        {
            customToast("Failed to remove " + itemToRemove);
        }
    }

    private void setupListViewAdapter() {
        adapter = new PhoneListAdapter(UpdateActivity.this, R.layout.access_list_item, remoteLock.getAcl());
        ListView atomPaysListView = (ListView)findViewById(R.id.accessList);
        atomPaysListView.setAdapter(adapter);
    }

    private void syncPhoneList() {
        adapter.clear();
        adapter = new PhoneListAdapter(UpdateActivity.this, R.layout.access_list_item, remoteLock.getAcl());
        ListView atomPaysListView = (ListView)findViewById(R.id.accessList);
        atomPaysListView.setAdapter(adapter);
    }

    static final int PICK_CONTACT=1;

    public void addAccess() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String cNumber = "";
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:"+cNumber);
                        }
                        //String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.));
                        cNumber = cNumber.replaceAll("\\s+", "");
                        String finalPhoneNumber = cNumber.substring(cNumber.length() - 10);
                        PhoneNumber itemToAdd = new PhoneNumber(finalPhoneNumber);
                        if (true == remoteLock.addToAcl(itemToAdd))
                        {
                            adapter.insert(itemToAdd, 0);
                            customToast("Added " + itemToAdd + " successfully");
                        }
                        else
                        {
                            customToast("Failed to add " + itemToAdd);
                        }
                    }
                }
                break;
        }
    }

    public void customToast(String text)
    {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(20);
        toastMessage.setTextColor(Color.rgb(0,191,255));
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setCompoundDrawablePadding(10);
        toastView.setBackgroundColor(Color.TRANSPARENT);
        toast.show();
    }
}