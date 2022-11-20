package com.example.mist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class QRScanActivity extends AppCompatActivity implements View.OnClickListener{

    private  static final int CAMERA_PERMISSION_CODE= 223;
    private static final int READ_STORAGE_PERMISSION_CODE = 144;
    private static final int WRITE_STORAGE_PERMISSION_CODE = 144;
    private static final String TAG ="MyTag" ;

    Button qrCodeButton;
    TextView tvResult;
    ImageView qrImgage;

    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;

    InputImage inputImage;
    BarcodeScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        qrCodeButton = findViewById(R.id.scanButton);
        qrImgage = findViewById(R.id.imgQr);
        tvResult = findViewById(R.id.tvResult);

        scanner = BarcodeScanning.getClient();
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //Handle picture from camera
                        Intent data = result.getData();
                        try {
                            Bitmap photo =(Bitmap) data.getExtras().get("data");
                            inputImage = InputImage.fromBitmap(photo, 0);
                            inputImage = InputImage.fromFilePath(QRScanActivity.this, data.getData());
                            processImageQr();
                        }catch (Exception e){
                            Log.d(TAG, "onActivityResult: "+e.getMessage());
                        }
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //Handle picture from gallety
                        Intent data = result.getData();
                        try {
                            inputImage = InputImage.fromFilePath(QRScanActivity.this, data.getData());
                            processImageQr();
                        }catch (Exception e){
                            Log.d(TAG, "onActivityResult: "+e.getMessage());
                        }
                    }
                }
        );

        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here we open dialog to choose between camera and gallery
                String [] options = {"camera","gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(QRScanActivity.this);
                builder.setTitle("Pick a option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            if(i == 0){
                                //handler for camera
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraLauncher.launch(cameraIntent);
                            }else{
                                // handler for gallery
                                Intent storageIntent = new Intent();
                                storageIntent.setType("image/*");
                                storageIntent.setAction(Intent.ACTION_GET_CONTENT);
                                galleryLauncher.launch(storageIntent);
                            }
                    }
                });
                builder.show();
            }
        });


    }

    private void processImageQr() {

        qrImgage.setVisibility(View.GONE);
        tvResult.setVisibility(View.VISIBLE);
        Task<List<Barcode>> result = scanner.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        for (Barcode barcode : barcodes) {
                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            switch (valueType) {
                                case Barcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    tvResult.setText("SSID : " + ssid + "\n" +
                                            "Password : " + password + "\n" +
                                            "Type : " + type + "\n");

                                    break;
                                case Barcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    tvResult.setText("Title : " + title + "\n" +
                                            "Url : " + url + "\n");
                                    break;
                                default:
                                    String data = barcode.getDisplayValue();
                                    tvResult.setText("Result : " + data);
                                    break;
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
    }


    public void checkPermission(String permission, int requestCode) {
        //Checking if permission granted or not
        if (ContextCompat.checkSelfPermission(QRScanActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            //Take Permission
            ActivityCompat.requestPermissions(QRScanActivity.this, new String[] {permission},requestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(QRScanActivity.this, "Camera permission Denied", Toast.LENGTH_SHORT).show();
            } else {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_PERMISSION_CODE);
            }
        } else if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(QRScanActivity.this, "Storage permission Denied", Toast.LENGTH_SHORT).show();
            } else {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_PERMISSION_CODE);
            }
        } else if (requestCode == WRITE_STORAGE_PERMISSION_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(QRScanActivity.this, "Storage permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {

    }
}