package com.example.mist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QRScanActivity extends AppCompatActivity implements View.OnClickListener{

    private  static final int CAMERA_PERMISSION_CODE= 223;
    private static final int READ_STORAGE_PERMISSION_CODE = 144;
    private static final int WRITE_STORAGE_PERMISSION_CODE = 144;
    private static final String TAG ="MyTag" ;
    private String specialString;

    Button qrCodeButton;
    TextView back_button;
    TextView tvResult;
    ImageView qrImgage;

    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;


    InputImage inputImage;
    BarcodeScanner scanner;

    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference image_ref;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        qrCodeButton = findViewById(R.id.scanButton);
        qrImgage = findViewById(R.id.imgQr);
        tvResult = findViewById(R.id.tvResult);
        back_button = (TextView) findViewById(R.id.back_profile_button);
        back_button.setOnClickListener(this);

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
                        //Handle picture from gallery
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
                builder.setTitle("Pick an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            if(i == 0){
                                //handler for camera
                               /* Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraLauncher.launch(cameraIntent);*/
                                scanCode();

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

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result ->
    {
        if(result.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(QRScanActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            tvResult.setText("Result : " + result.getContents());
            setLocalUser();
            if(result.getContents().startsWith("m/") == true)
            {
                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);

                        if(userProfile != null){
                            String username = userProfile.getUsername();
                            String email = userProfile.getEmail();

                            //parsing the qr code to get the value of the balance we are adding to the wallet
                            userProfile.addBalance(Float.valueOf(result.getContents().substring(result.getContents().indexOf("/")+1,result.getContents().indexOf("-"))));
                            float wallet = userProfile.getWallet();

                            HashMap User = new HashMap<>();
                            User.put("email",email);
                            User.put("username",username);
                            User.put("wallet", wallet);
                            reference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(QRScanActivity.this, "Money successfully added to the wallet", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(QRScanActivity.this, "Failed to add money to the wallet", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(QRScanActivity.this, "Something wrong", Toast.LENGTH_LONG).show();
                    }
                });
            }else{

                image_ref = FirebaseDatabase.getInstance().getReference().child("images");
                image_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean gameExistInStore = true;
//                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//
//                            String image_data = snapshot.getValue().toString();
//                            //System.out.println(image_data);
//                            // System.out.println(image_data.substring(image_data.indexOf("Game Name=") + 10,image_data.indexOf("}")));
//                            if(result.getContents().trim().equals(image_data.substring(image_data.indexOf("Game Name=") + 10,image_data.indexOf("}")).trim())){
//                                gameExistInStore = true;
//                                break;
//                            }
//                        }

                        if(gameExistInStore == true){
                            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User userProfile = snapshot.getValue(User.class);

                                    if(userProfile != null){
                                        String username = userProfile.getUsername();
                                        String email = userProfile.getEmail();
                                        float wallet = userProfile.getWallet();
                                        userProfile.addGame(result.getContents());
                                        ArrayList<String> library = userProfile.getLibrary();

                                        HashMap User = new HashMap<>();
                                        User.put("email",email);
                                        User.put("username",username);
                                        User.put("wallet", wallet);
                                        User.put("library",library);
                                        reference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(QRScanActivity.this, "Game successfully added to the library", Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Toast.makeText(QRScanActivity.this, "Failed to add the game to the library", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(QRScanActivity.this, "Something wrong", Toast.LENGTH_LONG).show();
                                }
                            });

                        }else{Toast.makeText(QRScanActivity.this, "The game doesn't exist in MIST", Toast.LENGTH_LONG).show();}


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });

    public void setLocalUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userID = user.getUid();

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
                                    String data = barcode.getDisplayValue();/// data de prelucrat de aici pt wallet/joc
                                    tvResult.setText("Result : " + data);
                                    setLocalUser();
                                    if(data.startsWith("m/") == true)
                                    {
                                        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                User userProfile = snapshot.getValue(User.class);

                                                if(userProfile != null){
                                                    String username = userProfile.getUsername();
                                                    String email = userProfile.getEmail();

                                                    //parsing the qr code to get the value of the balance we are adding to the wallet
                                                    userProfile.addBalance(Float.valueOf(data.substring(data.indexOf("/")+1,data.indexOf("-"))));
                                                    float wallet = userProfile.getWallet();

                                                    HashMap User = new HashMap<>();
                                                    User.put("email",email);
                                                    User.put("username",username);
                                                    User.put("wallet", wallet);
                                                    reference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(QRScanActivity.this, "Money successfully added to the wallet", Toast.LENGTH_LONG).show();
                                                            }
                                                            else{
                                                                Toast.makeText(QRScanActivity.this, "Failed to add money to the wallet", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(QRScanActivity.this, "Something wrong", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else{

                                        image_ref = FirebaseDatabase.getInstance().getReference().child("images");
                                        image_ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                boolean gameExistInStore = false;
                                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                                                    String image_data = snapshot.getValue().toString();
                                                    //System.out.println(image_data);
                                                   // System.out.println(image_data.substring(image_data.indexOf("Game Name=") + 10,image_data.indexOf("}")));
                                                    if(data.equals(image_data.substring(image_data.indexOf("Game Name=") + 10,image_data.indexOf("}")))){
                                                        gameExistInStore = true;
                                                        break;
                                                    }
                                                }

                                                if(gameExistInStore == true){
                                                    reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            User userProfile = snapshot.getValue(User.class);

                                                            if(userProfile != null){
                                                                String username = userProfile.getUsername();
                                                                String email = userProfile.getEmail();
                                                                float wallet = userProfile.getWallet();
                                                                userProfile.addGame(data);
                                                                ArrayList<String> library = userProfile.getLibrary();

                                                                HashMap User = new HashMap<>();
                                                                User.put("email",email);
                                                                User.put("username",username);
                                                                User.put("wallet", wallet);
                                                                User.put("library",library);
                                                                reference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task task) {
                                                                        if(task.isSuccessful()){
                                                                            Toast.makeText(QRScanActivity.this, "Game successfully added to the library", Toast.LENGTH_LONG).show();
                                                                        }
                                                                        else{
                                                                            Toast.makeText(QRScanActivity.this, "Failed to add the game to the library", Toast.LENGTH_LONG).show();

                                                                        }
                                                                    }
                                                                });
                                                            }


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Toast.makeText(QRScanActivity.this, "Something wrong", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                                }else{Toast.makeText(QRScanActivity.this, "The game doesn't exist in MIST", Toast.LENGTH_LONG).show();}


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
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
        switch (view.getId()){

            case R.id.back_profile_button:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
    }
}