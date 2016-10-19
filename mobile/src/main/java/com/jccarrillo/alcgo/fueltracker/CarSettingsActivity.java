package com.jccarrillo.alcgo.fueltracker;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jccarrillo.alcgo.fueltracker.domain.CarInfo;
import com.jccarrillo.alcgo.fueltracker.repository.CarInfoRepository;
import com.jccarrillo.alcgo.fueltracker.util.Permissions;

import java.io.File;

public class CarSettingsActivity extends AppCompatActivity {

    public static String BUNDLE_CAR = "carsettingsactivity:car";
    private static final int REQUEST_CAMERA = 0x14;
    private static final int REQUEST_FILE = 0x42;
    private static final int REQUEST_PERMISSIONS = 0xAB7;

    private CarInfo mCarInfo;
    private MenuItem mMenuAdd;
    private MenuItem mMenuChange;
    private EditText mEditTextName;
    private EditText mEditTextBrand;
    private View mFrameLayoutPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_settings);

        getBundleArguments();
        initialize();
        populate();
        listeners();
    }

    private void getBundleArguments(){
        Bundle args =  getIntent().getExtras();
        if (args != null)
            if( args.containsKey( BUNDLE_CAR ) )
                mCarInfo = (CarInfo) args.getSerializable(BUNDLE_CAR);
    }

    private void initialize(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEditTextName = (EditText) findViewById(R.id.editText);
        mEditTextBrand = (EditText) findViewById(R.id.editText2);
        mFrameLayoutPhoto = findViewById(R.id.frameLayout);
    }

    private void populate(){
        if( mCarInfo != null ){
            mEditTextName.setText( mCarInfo.getModel() );
            mEditTextBrand.setText( mCarInfo.getCompany() );
        }
        fixMenu();
    }

    private void listeners(){
        mFrameLayoutPhoto.setOnClickListener( mOnPhotoClick );
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == android.R.id.home ) {
            finish();
        } else if( id == R.id.menu_action_change ){
            // Change
            if( check() )
                save();
        } else if( id == R.id.menu_action_add ){
            // Add
            if( check() )
                add();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean check(){
        if( mEditTextName.getText().toString().trim().length() == 0 ){
            Toast.makeText( this, R.string.incorrect_car_name, Toast.LENGTH_SHORT ).show();
            mEditTextName.requestFocus();
            return false;
        }
        if( mEditTextBrand.getText().toString().trim().length() == 0 ){
            Toast.makeText( this, R.string.incorrect_car_brand, Toast.LENGTH_SHORT ).show();
            mEditTextBrand.requestFocus();
            return false;
        }
        return true;
    }

    private void save(){
        mCarInfo.setCompany( mEditTextBrand.getText().toString() );
        mCarInfo.setModel( mEditTextName.getText().toString() );
        CarInfoRepository.setCarInfo( mCarInfo );
        Intent data = new Intent();
        data.putExtra( BUNDLE_CAR, mCarInfo );
        setResult( RESULT_OK, data );
        finish();
    }

    private void add(){
        mCarInfo = new CarInfo();
        mCarInfo.setCompany( mEditTextBrand.getText().toString() );
        mCarInfo.setModel( mEditTextName.getText().toString() );
        CarInfoRepository.setCarInfo( mCarInfo );
        Intent data = new Intent();
        data.putExtra( BUNDLE_CAR, mCarInfo );
        setResult( RESULT_OK, data );
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_value, menu);
        mMenuAdd = menu.findItem(R.id.menu_action_add);
        mMenuChange = menu.findItem(R.id.menu_action_change);
        fixMenu();
        return true;
    }

    private void fixMenu(){
        if( mMenuChange != null )
            mMenuChange.setVisible( mCarInfo != null );
        if( mMenuAdd != null )
            mMenuAdd.setVisible( mCarInfo == null );
    }

    private View.OnClickListener mOnPhotoClick = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {

            String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };

            if( !Permissions.hasPermissions( CarSettingsActivity.this, permissions ) )
                CarSettingsActivity.this.requestPermissions( permissions, REQUEST_PERMISSIONS );
            else
                pickImagesDialog();

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String[] perms = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if( Permissions.onRequestPermissionsResult( requestCode, permissions, grantResults, REQUEST_PERMISSIONS, perms ) )
            pickImagesDialog();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickImagesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder( CarSettingsActivity.this );
        builder.setTitle(R.string.choose_option);
        String[] items = new String[ 2 ];
        items[ 0 ] = getString( R.string.gallery );
        items[ 1 ] = getString( R.string.camera );
        if( items.length > 2 )
            items[ 2 ] = getString( R.string.remove_image );

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which){
                    case 0:
                        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        startActivityForResult(i, REQUEST_FILE);
                        break;
                    case 1:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        File fil = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fil));
                        try {
                            startActivityForResult(cameraIntent, REQUEST_CAMERA);
                        }catch ( Exception e ){
                            Log.e(CarSettingsActivity.class.toString(), "Error launching camera intent", e );
                        }
                        break;
                    case 2:
                        break;
                }

            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}
