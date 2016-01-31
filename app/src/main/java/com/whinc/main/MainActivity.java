package com.whinc.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.whinc.tinylog.Log;

public class MainActivity extends AppCompatActivity
        implements CheckBox.OnCheckedChangeListener, AdapterView.OnItemSelectedListener{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ((CheckBox)findViewById(R.id.enable_ckb)).setOnCheckedChangeListener(this);
        ((CheckBox)findViewById(R.id.print_line_info_ckb)).setOnCheckedChangeListener(this);
        ((CheckBox)findViewById(R.id.intercept_log_ckb)).setOnCheckedChangeListener(this);
        Spinner spinner = (Spinner) findViewById(R.id.level_spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.log_level)));
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.enable_ckb:
                Log.enable(isChecked);
                break;
            case R.id.print_line_info_ckb:
                Log.enablePrintLineInfo(isChecked);
                break;
            case R.id.intercept_log_ckb:
                if (isChecked) {
                    // 可多层嵌套的拦截器
                    Log.setInterceptor(new Log.Interceptor() {
                        @Override
                        public boolean onIntercept(int level, String tag, String msg) {
                            Log.setInterceptor(new Log.Interceptor() {
                                @Override
                                public boolean onIntercept(int level, String tag, String msg) {
                                    Log.e(TAG, "intercept 2");
                                    return false;
                                }
                            });
                            Log.e(TAG, "intercept 1 with level:" + level);
                            return false;
                        }
                    });
                } else {
                    Log.setInterceptor(null);
                }
                break;
            default:
                break;
        }
    }

    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.verbose_btn:
                Log.v(TAG, "msg only");
                Log.v(TAG, "msg with print call stack", 3);
                Log.v(TAG, "msg with print throwable", new Throwable());
                Log.v(TAG, new Throwable());
                break;
            case R.id.debug_btn:
                Log.d(TAG, "msg only");
                Log.d(TAG, "msg with print call stack", 3);
                Log.d(TAG, "msg with print throwable", new Throwable());
                Log.d(TAG, new Throwable());
                break;
            case R.id.info_btn:
                Log.i(TAG, "msg only");
                Log.i(TAG, "msg with print call stack", 3);
                Log.i(TAG, "msg with print throwable", new Throwable());
                Log.i(TAG, new Throwable());
                break;
            case R.id.warn_btn:
                Log.w(TAG, "msg only");
                Log.w(TAG, "msg with print call stack", 3);
                Log.w(TAG, "msg with print throwable", new Throwable());
                Log.w(TAG, new Throwable());
                break;
            case R.id.error_btn:
                Log.e(TAG, "msg only");
                Log.e(TAG, "msg with print call stack", 3);
                Log.e(TAG, "msg with print throwable", new Throwable());
                Log.e(TAG, new Throwable());
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0 :
                Log.setLevel(Log.LEVEL_VERBOSE);
                break;
            case 1 :
                Log.setLevel(Log.LEVEL_DEBUG);
                break;
            case 2 :
                Log.setLevel(Log.LEVEL_INFO);
                break;
            case 3 :
                Log.setLevel(Log.LEVEL_WARN);
                break;
            case 4 :
                Log.setLevel(Log.LEVEL_ERROR);
                break;
            default:
                Log.setLevel(Log.LEVEL_VERBOSE);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.setLevel(Log.LEVEL_VERBOSE);
    }
}
