package com.mr_xiaogui163.app_0003_sendmessage;

import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.os.Handler;
import android.os.HandlerThread;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private final String TAG  = "SendMessage";
    private int mButtonCount = 0;
    private int GetMessageCount2 = 0;
    private int GetMessageCount3 = 0;
    private Thread mThread;
    private MyTread myThread;
    private Handler mHandler;
    private HandlerThread mHandleThread;
    private Handler mHandleThreadHandler;


    class mRunnable implements Runnable{
        int count = 0;
        public void run(){
            for(;;){
                Log.d(TAG,"Thread count "+count);
                count++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyTread extends Thread{
        private Looper mLooper;
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            synchronized (this){
                mLooper = Looper.myLooper();
                notifyAll();
            }
            Looper.loop();
        }

        public Looper getLooper(){
            if(!isAlive()){
                return null;
            }
            synchronized (this) {
                while (isAlive() && mLooper == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                return mLooper;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button)findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d(TAG,"Send Message count "+mButtonCount);
                mButtonCount++;

                Message msg = new Message();
                mHandler.sendMessage(msg);//给线程2发送msg

                //给线程3发送msg
                mHandleThreadHandler.post(new Runnable() {
                    @Override
                    //线程3收到消息后回调函数被调用
                    public void run() {
                        Log.d(TAG,"Get Message for mHandleThread: "+GetMessageCount3);
                        GetMessageCount3++;
                    }
                });
            }
        });

        mThread = new Thread(new mRunnable(),"MessageTread");//线程1
        mThread.start();

        myThread = new MyTread();//线程2
        myThread.start();

        //线程2 的 Handler
        mHandler = new Handler(myThread.getLooper(),new Handler.Callback(){
            @Override
            //线程2收到消息后回调函数被调用
            public boolean handleMessage(Message msg) {
                Log.d(TAG,"Get Message for mHandler : "+GetMessageCount2);
                GetMessageCount2++;
                return false;
            }
        });

        //线程3
        mHandleThread = new HandlerThread("mHandleThread");
        mHandleThread.start();

        //线程3 的 Handler
        mHandleThreadHandler = new Handler(mHandleThread.getLooper());
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
}
