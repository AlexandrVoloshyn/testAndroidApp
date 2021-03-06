package com.example.admin.testandroidapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private OpenGLView openGLView;
    private GL2JNIView nativeGLView;
    private int glDelay = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        nativeGLView = new GL2JNIView(getApplication());
//        setContentView(nativeGLView);

        setContentView(R.layout.activity_main);

        setTitle("TabHost");

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.fibonacci);
        tabSpec.setIndicator("Fibonacci");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("2D");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("File");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        openGLView = (OpenGLView) findViewById(R.id.openGLViewJava);
        openGLView.onPause();

        nativeGLView = new GL2JNIView(openGLView.getContext());
        nativeGLView.setLayoutParams(openGLView.getLayoutParams());
        nativeGLView.onPause();
        ((LinearLayout) findViewById(R.id.tab2)).addView(nativeGLView);
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

    private final int fibonacciTimes = 10;
    private final int numberOfFibonacci = 40;

    public void onFibonacciButtonJava(View view) {
        TextView tv = (TextView) findViewById(R.id.fibonacciTextJava);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        for(int i = fibonacciTimes; i != 0; --i)
            new Fibonacci().getFibonacci(numberOfFibonacci);
        int fibo = new Fibonacci().getFibonacci(numberOfFibonacci);

        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        long i = timestamp1.getTime() - timestamp.getTime();
        tv.setText(Long.toString(i));
    }

    public void onFibonacciButtonNative(View view) {
        TextView tv = (TextView) findViewById(R.id.fibonacciTextNative);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        int fibo = fibo(fibonacciTimes);

        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        long i = timestamp1.getTime() - timestamp.getTime();
        tv.setText(Long.toString(i));
    }

    public void onOpenGLJava(View view) {
        Button javaButton = (Button) findViewById(R.id.openGLButtonJava);
        try {
            openGLView.clearTimes();
            openGLView.onResume();

            TimeUnit.SECONDS.sleep(glDelay);
            openGLView.onPause();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        javaButton.setText(Integer.toString(openGLView.getTimes()));
    }

    public void onOpenGLCpp(View view) {
        Button cppButton = (Button) findViewById(R.id.openGLButtonCpp);
        try {
            GL2JNILib.clearTimer();
            nativeGLView.onResume();

            TimeUnit.SECONDS.sleep(glDelay);
            nativeGLView.onPause();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cppButton.setText(Integer.toString(GL2JNILib.getTimer()));
    }

    public void onWriteFileJava(View view){
        Button javaButton = (Button) findViewById(R.id.fileJavaButton);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            OutputStream outputStream =  new BufferedOutputStream((new FileOutputStream("/sdcard/newFile.txt")));
            for(int i = 0; i < 1000000; ++i)
                outputStream.write("Hello world!\n".getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        long i = timestamp1.getTime() - timestamp.getTime();
        javaButton.setText(Long.toString(i));
    }

    public void onWriteFileCpp(View view){
        Button cppButton = (Button) findViewById(R.id.fileNativeButton);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        write();

        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        long i = timestamp1.getTime() - timestamp.getTime();
        cppButton.setText(Long.toString(i));
    }

    static {
        System.loadLibrary("native-lib");
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native int fibo(int num);
    public static native void write();

    // Used to load the 'native-lib' library on application startup
}
