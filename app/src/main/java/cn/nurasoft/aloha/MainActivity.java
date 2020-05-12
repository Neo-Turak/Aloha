package cn.nurasoft.aloha;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    TextView ip, database,user,password;
    Button connect,add, default_val;
    EditText temperature;
    ListView list;
    ArrayList<LifeUtil> lists;

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    SQLHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = findViewById(R.id.ip);
        connect = findViewById(R.id.OK);
        add=findViewById(R.id.add);
        temperature = findViewById(R.id.temps);
        list=findViewById(R.id.list);
        default_val =findViewById(R.id.def);
        database =findViewById(R.id.database);
        user=findViewById(R.id.username);
        password=findViewById(R.id.password);

        lists=new ArrayList<LifeUtil>();
        final UserAdapter adapter=new UserAdapter(this,lists);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);


        default_val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip.setText("192.168.0.11");
                database.setText("Products");
                user.setText("sa");
                password.setText("root");
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setTitle("Need Internet Permission");
                        builder.setMessage("Need to get internet permission for connecting sql server");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 100);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else if (permissionStatus.getBoolean(Manifest.permission.INTERNET, false)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Need Storage Permission");
                        builder.setMessage("This app needs storage permission.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sentToSettings = true;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 101);
                                Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        //just request the permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                    }
                }
                 helper=new SQLHelper(ip.getText().toString(), database.getText().toString(),user.getText().toString(),password.getText().toString());
              //  test();
              getList();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLHelper db=new SQLHelper(ip.getText().toString(), database.getText().toString(),user.getText().toString(),password.getText().toString());
                db.Add(Float.parseFloat(temperature.getText().toString()),date());
                getList();
            }
        });
    }
        // 查询
        private void test() {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    String ret = helper.Query();
                    Message msg = new Message();
                    msg.what = 1001;
                    Bundle data = new Bundle();
                    data.putString("result", ret);
                    msg.setData(data);
                    mHandler.sendMessage(msg);
                }
            };
            new Thread(run).start();
        }

        private void getList(){
        Runnable run=new Runnable() {
            @Override
            public void run() {
                ArrayList<LifeUtil> ret= helper.all();
                Message msg=new Message();
                msg.what=1001;
                Bundle data=new Bundle();
                data.putSerializable("result",ret);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        };
        new Thread(run).start();
        }
        public String date(){
        String result;
        Date d=new Date();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return result=format.format(d);
        }



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1001) {
                String str = msg.getData().getString("result");
                Log.e("result",str);
               if (str.equals("2")){
              final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
              builder.setTitle("Success!");
              builder.setMessage("Connection success!ready to go!");
              builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                  }
              });
              builder.create();
              builder.show();
               }else {
                   Toast toast=Toast.makeText(getApplicationContext(),"Connection failed!",Toast.LENGTH_SHORT);
                   toast.show();
               }
            }
        };
    };
 @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg){
            if (msg.what==1001){
                ArrayList<LifeUtil> str =(ArrayList<LifeUtil>) msg.getData().getSerializable("result");
                ArrayAdapter adapter=new UserAdapter(MainActivity.this,str);
                list.setAdapter(adapter);
            }
        }
    };
}
