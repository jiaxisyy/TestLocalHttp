package com.example.shuangxiang.testlocalhttp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    private EditText ettestreadAddress;
    private Spinner sptestread;
    private Button btntestread;
    private TextView tvtestread;
    private EditText ettestwriteAddress;
    private Spinner sptestwrite;
    private Button btntestwrite;
    private TextView tvtestwrite;
    private EditText mEttestwriteInfo;
    private int mLength;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    String read = (String) message.obj;
                    tvtestread.setText(read.toString());
                    break;
                case 2:
                    String write = (String) message.obj;
                    tvtestwrite.setText(write.toString());
                    break;
            }

            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        read();
        write();
    }

    private void write() {
        btntestwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String writeAddress = ettestwriteAddress.getText().toString().trim();
                String writeType = (String) sptestwrite.getSelectedItem();
                final String info = mEttestwriteInfo.getText().toString().toString();
                if (writeAddress.equals("") || info.equals("")) {
                    Toast.makeText(MainActivity.this, "输入参数", Toast.LENGTH_SHORT).show();
                }
                final String writeUrl = "http://127.0.0.1:9800/a/api?t=" + writeType + "&a=" +
                        writeAddress + "&l=1";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] bytes = Util.sendPost(writeUrl, info.getBytes());
                        StringBuffer stringBuffer = new StringBuffer();
                        Log.d("TEST", bytes.length + "");
                        for (int i = bytes.length - 1; i >= 0; i--) {
                            String s = Integer.toString(bytes[i]);
                            if (s.length() == 1) {
                                s = '0' + s;
                            }
                            Log.d("TEST", s + "");
                            stringBuffer.append(s + "\n");
                        }
                        Message message = Message.obtain();
                        message.what = 2;
                        message.obj = stringBuffer.toString();
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });


    }

    private void read() {
        btntestread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String readAddress = ettestreadAddress.getText().toString().trim();
                String readType = (String) sptestread.getSelectedItem();
                int length = 0;
                if (readAddress.equals("")) {
                    Toast.makeText(MainActivity.this, "输入地址", Toast.LENGTH_SHORT).show();
                }

                final String readUrl = "http://127.0.0.1:9800/a/api?t=" + readType;
                Log.d("TEST", readUrl.toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {

//                        Map<String, String> map = new HashMap<>();
//                        String data = Util.submitPostData(readUrl, map, "utf-8");
                        byte[] response = Util.sendPost(readUrl, null);
                        StringBuffer stringBuffer = new StringBuffer();
                        Log.d("TEST", response.length + "");
                        for (int i = response.length - 1; i >= 0; i--) {
                            String s = Integer.toString(response[i]);
                            if (s.length() == 1) {
                                s = '0' + s;
                            }
                            Log.d("TEST", s + "");
                            stringBuffer.append(s + "\n");
                        }
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = stringBuffer.toString();
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private void initialize() {

        ettestreadAddress = (EditText) findViewById(R.id.et_test_readAddress);
        sptestread = (Spinner) findViewById(R.id.sp_test_read);
        btntestread = (Button) findViewById(R.id.btn_test_read);
        tvtestread = (TextView) findViewById(R.id.tv_test_read);
        ettestwriteAddress = (EditText) findViewById(R.id.et_test_writeAddress);
        mEttestwriteInfo = (EditText) findViewById(R.id.et_test_write_info);
        sptestwrite = (Spinner) findViewById(R.id.sp_test_write);
        btntestwrite = (Button) findViewById(R.id.btn_test_write);
        tvtestwrite = (TextView) findViewById(R.id.tv_test_write);

    }

    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
}
