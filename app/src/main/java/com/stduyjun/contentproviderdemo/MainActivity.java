package com.stduyjun.contentproviderdemo;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        getContacts();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 获取联系人
     */
    private void getContacts() {
        final ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);//查询

        final CursorAdapter cursorAdapter = new CursorAdapter(this, cursor, true) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                TextView textView = new TextView(MainActivity.this);
                textView.setPadding(30,30,30,30);

                return textView;

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String peopleName =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                ((TextView) view).setText(peopleName); //姓名
            }
        };
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                cursorAdapter.getItem(i);
                final String id = ((Cursor) cursorAdapter.getItem(i)).getString(((Cursor) cursorAdapter.getItem(i)).getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));

                final EditText editText = new EditText(MainActivity.this);
                editText.setText(((Cursor) cursorAdapter.getItem(i)).getString(((Cursor) cursorAdapter.getItem(i)).getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));

                /**
                 * 对话框
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("修改联系人");
                builder.setMessage("姓名:");
                builder.setView(editText);
                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除联系人
                        contentResolver.delete(ContactsContract.Data.CONTENT_URI,ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + "=?",new String[]{id});
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //更新联系人
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, editText.getText().toString());
                        contentResolver.update(ContactsContract.Data.CONTENT_URI, contentValues, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + "=?", new String[]{id});
                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


}
