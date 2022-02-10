package com.luckynum.utils;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.luckynum.MainActivity;
import com.luckynum.interfaces.ContactsInterface;
import com.luckynum.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsManager implements LoaderManager.LoaderCallbacks<Cursor> {

    private MainActivity activity;
    public String[] PROJECTION_NUMBERS = new String[]
            {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
    public String[] PROJECTION_DETAILS =  new String[]
            {ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI};


    protected Map<Long, List<String>> phones = new HashMap<>();
    protected List<Contact> contacts = new ArrayList<>();
    ContactsInterface contactsInterface;

    public ContactsManager(MainActivity activity) {
        this.activity = activity;
        this.contactsInterface = activity;
    }

    public void init() {
        LoaderManager.getInstance(activity).initLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id,
                                         @Nullable Bundle args) {
        switch (id) {
            case 0:
                return new CursorLoader(
                        activity,
                        ContactsContract.CommonDataKinds.
                                Phone.CONTENT_URI,
                        PROJECTION_NUMBERS,
                        null,
                        null,
                        null
                );
            default:
                return new CursorLoader(
                        activity,
                        ContactsContract.Contacts.CONTENT_URI,
                        PROJECTION_DETAILS,
                        null,
                        null,
                        null
                );
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader,
                               Cursor data) {
        switch (loader.getId()) {
            case 0:
                phones = new HashMap<>();
                if (data != null) {
                    while (!data.isClosed() && data.moveToNext()) {
                        long contactId = data.getLong(0);
                        String phone = data.getString(1);
                        List<String> list = new ArrayList<>();
                        if (phones.containsKey(contactId)) {
                            list = (List<String>) phones.get(contactId);
                        } else {
                            list = new ArrayList<>();
                            phones.put(contactId, list);
                        }
                        list.add(phone);
                    }
                    data.close();
                }
                LoaderManager.getInstance(activity)
                        .initLoader(1,null,this);
                break;
            case 1:
                if (data!=null) {
                    while (!data.isClosed() && data.moveToNext()) {
                        long contactId = data.getLong(0);
                        String name = data.getString(1);
                        List<String> contactPhones =
                                phones.get(contactId);
                        if (contactPhones != null) {
                            for (String phone :
                                    contactPhones) {
                                addContact(new Contact(contactId, name, phone));
                            }
                        }
                    }
                    data.close();
                    contactsInterface.getContacts(contacts);
                }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void addContact(Contact contact) {
        contacts.add(contact);
    }
}
