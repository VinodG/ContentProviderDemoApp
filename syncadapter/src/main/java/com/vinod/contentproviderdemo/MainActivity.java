package com.vinod.contentproviderdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText etInput;
    private TextView tvCurrentUserName;
    private MyObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControls();
        observer = new MyObserver(null);
        setDataChangeListener();
    }

    private void setDataChangeListener() {
        getContentResolver().registerContentObserver(MyContentProvider.CONTENT_URI,true, observer  );
    }

    private void initControls() {
        etInput =  ((EditText)findViewById(R.id.etInput));
        tvCurrentUserName=  ((TextView)findViewById(R.id.tvCurrentUserName));
        tvCurrentUserName.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onClkSend(View view) {
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.NAME,
                etInput.getText().toString());
        values.put(MyContentProvider.GRADE,
                etInput.getText().toString()+"_GRADE");
        Uri uri = getContentResolver().insert(
                MyContentProvider.CONTENT_URI, values);
        toast(uri.toString());
    }
    public void onClkRead(View view) {
        // Retrieve student records
        Cursor c =  getContentResolver().query
        (MyContentProvider.CONTENT_URI, null, null, null, "name");
        updateUI(c);

    }

    private void updateUI(Cursor c) {
        try {
            final StringBuilder str =new StringBuilder();
            if (c.moveToFirst()) {
                do{
                    str.append( c.getString(c.getColumnIndex(MyContentProvider._ID)) +
                            ", " +  c.getString(c.getColumnIndex( MyContentProvider.NAME)) +
                            ", " + c.getString(c.getColumnIndex( MyContentProvider.GRADE))+"\n");
                } while (c.moveToNext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCurrentUserName.setText(str);
                    }
                });


            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            c.close();
        }
    }

    private void toast(String str) {
        Toast.makeText(getBaseContext(),str+"", Toast.LENGTH_LONG).show();
    }
    class MyObserver extends ContentObserver {
        private String TAG = MyObserver.class.getSimpleName();

        public MyObserver(Handler handler) {
            super(handler);
            Log.e(TAG,new Exception().getStackTrace()[0].getMethodName());
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
            Log.e(TAG,new Exception().getStackTrace()[0].getMethodName()+"-"+1);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.e(TAG,new Exception().getStackTrace()[0].getMethodName()+"-URI" );
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            updateUI(cursor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().
                unregisterContentObserver(observer);
    }
}
