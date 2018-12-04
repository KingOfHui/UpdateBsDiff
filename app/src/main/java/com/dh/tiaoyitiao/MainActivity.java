package com.dh.tiaoyitiao;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.tiaoyitiao.utils.ApkUtils;
import com.dh.tiaoyitiao.utils.BsPatch;
import com.dh.tiaoyitiao.utils.Constants;
import com.dh.tiaoyitiao.utils.DownloadUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    private SunView sv;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("bspatch");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ApkUpdateTask().execute();

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
        // 找到控件
        sv = findViewById(R.id.sv);
        // 设置日出时间
        sv.setSunrise(05, 39);
        // 设置日落时间
        sv.setSunset(18, 48);
        // 获取系统 时 分
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        // 设置当前时间
        sv.setCurrentTime(9, 22);
    }

    public void sure(View view) {
        String hour = ((EditText) findViewById(R.id.hour)).getText().toString().trim();
        String minus = ((EditText) findViewById(R.id.hour)).getText().toString().trim();
        sv.setCurrentTime(Integer.parseInt(hour), Integer.parseInt(minus));
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();

//    public native String getLength(String s, int i);

    class ApkUpdateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //1.下载差分包
                Log.d("dh", "开始下载");
//                File patchFile = DownloadUtils.download(Constants.URL_PATCH_DOWNLOAD);
                File patchFile = new File(Constants.NEW_APK_PATH);
                //获取当前应用的apk文件/data/app/app
                String oldfile = ApkUtils.getSourceApkPath(MainActivity.this, getPackageName());
                //2.合并得到最新版本的APK文件
                String newfile = Constants.NEW_APK_PATH;
                String patchfile = patchFile.getAbsolutePath();
                BsPatch.patch(oldfile, newfile, patchfile);

                Log.d("dh", "oldfile:"+oldfile);
                Log.d("dh", "newfile:"+newfile);
                Log.d("dh", "patchfile:"+patchfile);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //3.安装
            if(result){
                Toast.makeText(MainActivity.this, "您正在进行无流量更新", Toast.LENGTH_SHORT).show();
                ApkUtils.installApk(MainActivity.this, Constants.NEW_APK_PATH);
            }
        }

    }
}
