package com.example.aidlpermissionclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aidlpermission.IStudentService;

public class MainActivity extends AppCompatActivity {

    private int ACCESS_STUDENT_SERVICE_PERMISSION = 1;
    private String ACCESS_STUDENT_SERVICE = "com.example.aidlpermission.ACCESS_STUDENT_SERVICE";
    private IStudentService mService;
    private EditText edtId, edtName, edtPhone, edtAddress, edtEmail;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        edtId = findViewById(R.id.editID);
        edtName = findViewById(R.id.editName);
        edtPhone = findViewById(R.id.editCall);
        edtAddress = findViewById(R.id.editAddress);
        edtEmail = findViewById(R.id.editMail);
        txtResult = findViewById(R.id.textResult);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setAction("com.example.aidlpermission.ACTION");
        intent.setPackage("com.example.aidlpermission");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = IStudentService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    public void handlerAddStudent(View view) {
        runtimePermission();
        if (mService != null) {
            try {
                mService.addStudent(Integer.parseInt(edtId.getText().toString()), edtName.getText().toString(), edtPhone.getText().toString(), edtAddress.getText().toString(), edtEmail.getText().toString());
                StringBuilder result = new StringBuilder();
                for (Student student : mService.getStudents()) {
                    result.append(student.toString() + "\n");
                }
                txtResult.setText(result.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void runtimePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_STUDENT_SERVICE) == PackageManager.PERMISSION_GRANTED) {
            bindService();
        } else {
            requestAccessStudentServicePermission();
        }
    }

    private void requestAccessStudentServicePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_STUDENT_SERVICE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission to access and use student service")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_STUDENT_SERVICE}, ACCESS_STUDENT_SERVICE_PERMISSION);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_STUDENT_SERVICE}, ACCESS_STUDENT_SERVICE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_STUDENT_SERVICE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}