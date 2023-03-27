package com.example.control_20_servo;

import static android.R.layout.simple_list_item_1;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ImageView imgMenuListDevice, imgBluetoothConnection;
    TextView txtNameBluetoothConnection, txtBackListDevice;
    View layoutListDevice;
    ProgressBar pgbRefreshListDevice;
    ListView lvListDevice;
    ArrayAdapter<String> arrayAdapterListDevice;

    LinearLayout llSelectSettingPreset, llSelectSettingTour;

    public List<DataPreset> listDataPreset;
    ArrayAdapter<String> arrayAdapterListDataTour1, arrayAdapterListDataTour2, arrayAdapterListDataTour3, arrayAdapterListDataTour4;
    public ListView lvListDataPreset, lvListDataTour1, lvListDataTour2, lvListDataTour3, lvListDataTour4;
    public RelativeLayout rlShowDataSettingMode;
    ImageView imgAddPreset, imgSaveDataToLocalFile, imgGetDataFromLocalFile, imgSyncDataPresetFromDevice, imgSyncDataTourFromDevice;
    ImageView imgOkAddPreset, imgDeleteAddPreset, imgCancelAddPreset;
    ImageView imgAddTourMode1, imgAddTourMode2, imgAddTourMode3, imgAddTourMode4;
    ImageView imgSendDataTourMode1ToDevice, imgSendDataTourMode2ToDevice, imgSendDataTourMode3ToDevice, imgSendDataTourMode4ToDevice, imgSendDataTourMode5ToDevice;
    ImageView imgOkAddTour, imgDeleteAddTour, imgCancelAddTour;
    TextView txtCurrentModeRunDevice;

    Spinner spnShowPresetOpenTourMode5, spnShowPresetCloseTourMode5, spnShowPresetTourMode1To4;
    EditText edtSetTimeDelayPresetOfTour;
    EditText edtSetNamePreset;
    EditText[] edtAngleServo = new EditText[20];

    ProgressBar prbSyncDataWithDevice;

    public static RelativeLayout rlBackgroundFragmentAddPreset, rlBackgroundFragmentAddTour;
    View layoutSetupPreset, layoutSetupTour;

    int currentSelectedPresetList = 0;
    int currentSelectedTourMode = 0;
    int currentSelectedPresetOfTourList = 0;
    boolean isSyncPresetWithDevice = false;
    boolean isSyncTourWithDevice = false;

    public static int REQUEST_BLUETOOTH = 1;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static int REQUEST_CODE_SELECT_FILE_SETTING = 3;

    //variable for save name motor
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String presetStr = "preset";
    String tourStr = "tour";
    String dataPresetCurrent = "";
    String dataTourCurrent = "";

    File fileSaveText;
    String file_folder_name_save_setting = "Servo";
//    String file_name_local_save_setting = "Servo.txt";
    String file_name_download_save_setting = "fileNameServo";

    String TAG = "MainActivity";
    String deviceName;
    private BluetoothDevice mmDevice;
    //private UUID deviceUUID;

    ParcelUuid[] mDeviceUUIDs;
    ConnectedThread mConnectedThread;
    //    private Handler handler;
    Object[] ObjectBluetooth;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        setContentView(R.layout.activity_main);
        checkPermissions();
        initLayout();
        initBluetooth();
        initDataLocal();
//        loadDataPreset(sharedPreferences.getString(presetStr, ""));
//        loadDataTour(sharedPreferences.getString(tourStr, ""));
        loadDataPreset(dataPresetCurrent);
        loadDataTour(dataTourCurrent);

        imgMenuListDevice.setOnClickListener(onClickImgMenuListDevice);
        txtBackListDevice.setOnClickListener(onClickTxtBackListDevice);
        lvListDevice.setOnItemClickListener(onClickLvListDevice);
        imgAddPreset.setOnClickListener(onClickImgAddPreset);
        imgSaveDataToLocalFile.setOnClickListener(onClickImgSaveDataToLocalFile);
        imgGetDataFromLocalFile.setOnClickListener(onClickImgGetDataFromLocalFile);
        imgSyncDataPresetFromDevice.setOnClickListener(onClickImgSyncDataPresetFromDevice);
        imgSyncDataTourFromDevice.setOnClickListener(onClickImgSyncDataTourFromDevice);
        imgOkAddPreset.setOnClickListener(onClickImgOkAddPreset);
        imgDeleteAddPreset.setOnClickListener(onClickImgDeleteAddPreset);
        imgCancelAddPreset.setOnClickListener(onClickImgCancelAddPreset);
        lvListDataPreset.setOnItemClickListener(onClickLvListDataPreset);
        llSelectSettingPreset.setOnClickListener(onClickLlSelectSettingPreset);
        llSelectSettingTour.setOnClickListener(onClickLlSelectSettingTour);
        imgAddTourMode1.setOnClickListener(onClickImgAddTourMode1);
        imgAddTourMode2.setOnClickListener(onClickImgAddTourMode2);
        imgAddTourMode3.setOnClickListener(onClickImgAddTourMode3);
        imgAddTourMode4.setOnClickListener(onClickImgAddTourMode4);
        imgOkAddTour.setOnClickListener(onClickImgOkAddTour);
        imgDeleteAddTour.setOnClickListener(onClickImgDeleteAddTour);
        imgCancelAddTour.setOnClickListener(onClickImgCancelAddTour);

        lvListDataTour1.setOnItemClickListener(onClickLvListDataTour1);
        lvListDataTour1.setOnTouchListener(onTouchLvListDataTour1);
        lvListDataTour2.setOnItemClickListener(onClickLvListDataTour2);
        lvListDataTour2.setOnTouchListener(onTouchLvListDataTour2);
        lvListDataTour3.setOnItemClickListener(onClickLvListDataTour3);
        lvListDataTour3.setOnTouchListener(onTouchLvListDataTour3);
        lvListDataTour4.setOnItemClickListener(onClickLvListDataTour4);
        lvListDataTour4.setOnTouchListener(onTouchLvListDataTour4);

    }

    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        }
        else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }

    public void initLayout() {
        imgMenuListDevice = findViewById(R.id.imgMenuListDevice);
        imgBluetoothConnection = findViewById(R.id.imgBluetoothConnection);
        txtNameBluetoothConnection = findViewById(R.id.txtNameBluetoothConnection);

        pgbRefreshListDevice = findViewById(R.id.pgbRefreshListDevice);
        lvListDevice = findViewById(R.id.lvListDevice);
        txtBackListDevice = findViewById(R.id.txtBackListDevice);

        layoutListDevice = findViewById(R.id.layoutListDevice);

        lvListDataPreset = findViewById(R.id.lvListDataPreset);
        rlShowDataSettingMode = findViewById(R.id.rlShowDataSettingMode);

        lvListDataTour1 = findViewById(R.id.lvListDataTour1);
        lvListDataTour2 = findViewById(R.id.lvListDataTour2);
        lvListDataTour3 = findViewById(R.id.lvListDataTour3);
        lvListDataTour4 = findViewById(R.id.lvListDataTour4);

        llSelectSettingPreset = findViewById(R.id.llSelectSettingPreset);
        llSelectSettingTour = findViewById(R.id.llSelectSettingTour);

        imgAddPreset = findViewById(R.id.imgAddPreset);
        imgSaveDataToLocalFile = findViewById(R.id.imgSaveDataToLocalFile);
        imgGetDataFromLocalFile = findViewById(R.id.imgGetDataFromLocalFile);
        imgSyncDataPresetFromDevice = findViewById(R.id.imgSyncDataPresetFromDevice);
        imgSyncDataTourFromDevice = findViewById(R.id.imgSyncDataTourFromDevice);

        imgOkAddPreset = findViewById(R.id.imgOkAddPreset);
        imgDeleteAddPreset = findViewById(R.id.imgDeleteAddPreset);
        imgCancelAddPreset = findViewById(R.id.imgCancelAddPreset);

        imgAddTourMode1 = findViewById(R.id.imgAddTourMode1);
        imgAddTourMode2 = findViewById(R.id.imgAddTourMode2);
        imgAddTourMode3 = findViewById(R.id.imgAddTourMode3);
        imgAddTourMode4 = findViewById(R.id.imgAddTourMode4);
        imgSendDataTourMode1ToDevice = findViewById(R.id.imgSendDataTourMode1ToDevice);
        imgSendDataTourMode2ToDevice = findViewById(R.id.imgSendDataTourMode2ToDevice);
        imgSendDataTourMode3ToDevice = findViewById(R.id.imgSendDataTourMode3ToDevice);
        imgSendDataTourMode4ToDevice = findViewById(R.id.imgSendDataTourMode4ToDevice);
        imgSendDataTourMode5ToDevice = findViewById(R.id.imgSendDataTourMode5ToDevice);

        imgOkAddTour = findViewById(R.id.imgOkAddTour);
        imgDeleteAddTour = findViewById(R.id.imgDeleteAddTour);
        imgCancelAddTour = findViewById(R.id.imgCancelAddTour);

        txtCurrentModeRunDevice = findViewById(R.id.txtCurrentModeRunDevice);

        edtSetTimeDelayPresetOfTour = findViewById(R.id.edtSetTimeDelayPresetOfTour);

        rlBackgroundFragmentAddPreset = findViewById(R.id.rlBackgroundFragmentAddPreset);
        rlBackgroundFragmentAddTour = findViewById(R.id.rlBackgroundFragmentAddTour);

        layoutSetupPreset = findViewById(R.id.layoutSetupPreset);
        layoutSetupTour = findViewById(R.id.layoutSetupTour);

        edtSetNamePreset = findViewById(R.id.edtSetNamePreset);
        edtAngleServo[0] = findViewById(R.id.edtAngleServo1);
        edtAngleServo[1] = findViewById(R.id.edtAngleServo2);
        edtAngleServo[2] = findViewById(R.id.edtAngleServo3);
        edtAngleServo[3] = findViewById(R.id.edtAngleServo4);
        edtAngleServo[4] = findViewById(R.id.edtAngleServo5);
        edtAngleServo[5] = findViewById(R.id.edtAngleServo6);
        edtAngleServo[6] = findViewById(R.id.edtAngleServo7);
        edtAngleServo[7] = findViewById(R.id.edtAngleServo8);
        edtAngleServo[8] = findViewById(R.id.edtAngleServo9);
        edtAngleServo[9] = findViewById(R.id.edtAngleServo10);
        edtAngleServo[10] = findViewById(R.id.edtAngleServo11);
        edtAngleServo[11] = findViewById(R.id.edtAngleServo12);
        edtAngleServo[12] = findViewById(R.id.edtAngleServo13);
        edtAngleServo[13] = findViewById(R.id.edtAngleServo14);
        edtAngleServo[14] = findViewById(R.id.edtAngleServo15);
        edtAngleServo[15] = findViewById(R.id.edtAngleServo16);
        edtAngleServo[16] = findViewById(R.id.edtAngleServo17);
        edtAngleServo[17] = findViewById(R.id.edtAngleServo18);
        edtAngleServo[18] = findViewById(R.id.edtAngleServo19);
        edtAngleServo[19] = findViewById(R.id.edtAngleServo20);

        spnShowPresetOpenTourMode5 = findViewById(R.id.spnShowPresetOpenTourMode5);
        spnShowPresetCloseTourMode5 = findViewById(R.id.spnShowPresetCloseTourMode5);
        spnShowPresetTourMode1To4 = findViewById(R.id.spnShowPresetTourMode1To4);

        prbSyncDataWithDevice = findViewById(R.id.prbSyncDataWithDevice);
    }

    public void initBluetooth() {
        //--------------------------------------for bluetooth--------------------------------------------------------
        BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                return;
            }
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }
        //------------------------------------------------------------------------------------------------------
    }

    public void initDataLocal(){
        //---------------------------------------------------save name motor--------------------------------------
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
    }

    public boolean loadDataPreset(String jsonData){
        listDataPreset = new ArrayList<>();
        DataPresetAdapter adapter = new DataPresetAdapter(this, R.layout.list_view_data_preset, listDataPreset);
        //-------------------
        if(!jsonData.equals("")){
            try {
//                Log.i("incomingMessage", jsonData);
                JSONArray jsonArrayDataReceive = new JSONArray(jsonData);
                for(int i = 0; i < jsonArrayDataReceive.length(); i++){
                    JSONObject jsonObjectData = jsonArrayDataReceive.getJSONObject(i);
                    String name = jsonObjectData.getString("1");
                    JSONArray jsonArrayAngle = jsonObjectData.getJSONArray("2");
                    int[] angle = new int[20];
                    for(int j = 0; j < jsonArrayAngle.length(); j++){
                        angle[j] = jsonArrayAngle.getInt(j);
                    }
                    listDataPreset.add(new DataPreset(i, name, angle));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        lvListDataPreset.setAdapter(adapter);
        return true;
    }

    public void loadDataTour(String dataTour){
        //update list preset to spinner
        updateListPresetToSpinner();
        arrayAdapterListDataTour1 = new ArrayAdapter<String>(MainActivity.this, simple_list_item_1);
        arrayAdapterListDataTour2 = new ArrayAdapter<String>(MainActivity.this, simple_list_item_1);
        arrayAdapterListDataTour3 = new ArrayAdapter<String>(MainActivity.this, simple_list_item_1);
        arrayAdapterListDataTour4 = new ArrayAdapter<String>(MainActivity.this, simple_list_item_1);
//        String dataTour = sharedPreferences.getString(tourStr, "");
        JSONObject jsonObjectTour = new JSONObject();
        //data example" {"1":[{"1":"ngoc","2":2}], "2":[{"1":"tuyet","2":2}], "3":[{"1":"tuyet","2":2}], "4":[{"1":"tuyet","2":2}], "5":[1,2]}
        if(!dataTour.equals("")){
            try {
                jsonObjectTour = new JSONObject(dataTour);
                JSONArray jsonArrayMode1 = jsonObjectTour.getJSONArray("1");
                for(int i = 0; i < jsonArrayMode1.length(); i++){
                    JSONObject jsonObjectDataMode1 = jsonArrayMode1.getJSONObject(i);
                    String name = jsonObjectDataMode1.getString("1");
                    int time = jsonObjectDataMode1.getInt("2");
                    arrayAdapterListDataTour1.add("Name: " + name + "\n" + "Time: " + String.valueOf(time) + "s");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonArrayMode2 = jsonObjectTour.getJSONArray("2");
                for(int i = 0; i < jsonArrayMode2.length(); i++){
                    JSONObject jsonObjectDataMode2 = jsonArrayMode2.getJSONObject(i);
                    String name = jsonObjectDataMode2.getString("1");
                    int time = jsonObjectDataMode2.getInt("2");
                    arrayAdapterListDataTour2.add("Name: " + name + "\n" + "Time: " + String.valueOf(time) + "s");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonArrayMode3 = jsonObjectTour.getJSONArray("3");
                for(int i = 0; i < jsonArrayMode3.length(); i++){
                    JSONObject jsonObjectDataMode3 = jsonArrayMode3.getJSONObject(i);
                    String name = jsonObjectDataMode3.getString("1");
                    int time = jsonObjectDataMode3.getInt("2");
                    arrayAdapterListDataTour3.add("Name: " + name + "\n" + "Time: " + String.valueOf(time) + "s");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonArrayMode4 = jsonObjectTour.getJSONArray("4");
                for(int i = 0; i < jsonArrayMode4.length(); i++){
                    JSONObject jsonObjectDataMode4 = jsonArrayMode4.getJSONObject(i);
                    String name = jsonObjectDataMode4.getString("1");
                    int time = jsonObjectDataMode4.getInt("2");
                    arrayAdapterListDataTour4.add("Name: " + name + "\n" + "Time: " + String.valueOf(time) + "s");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jsonArrayMode5 = jsonObjectTour.getJSONArray("5");
                int openMode5 = jsonArrayMode5.getInt(0);
                int closeMode5 = jsonArrayMode5.getInt(1);
                spnShowPresetOpenTourMode5.setSelection(openMode5);
                spnShowPresetCloseTourMode5.setSelection(closeMode5);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        lvListDataTour1.setAdapter(arrayAdapterListDataTour1);
        lvListDataTour2.setAdapter(arrayAdapterListDataTour2);
        lvListDataTour3.setAdapter(arrayAdapterListDataTour3);
        lvListDataTour4.setAdapter(arrayAdapterListDataTour4);
    }

    public void updateListPresetToSpinner(){
        List<String> listMode1To4 = new ArrayList<>();
        List<String> listOpenMode5 = new ArrayList<>();
        List<String> listCloseMode5 = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray();
            if(!dataPresetCurrent.equals("")){
                jsonArray = new JSONArray(dataPresetCurrent);
            }
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("1");
                listMode1To4.add(name);
//                listOpenMode5.add(name);
//                listCloseMode5.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i = 1; i < 5; i++){
            listOpenMode5.add("Mode " + i);
            listCloseMode5.add("Mode " + i);
        }

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapterMode1and2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listMode1To4);
        ArrayAdapter adapterOpenMode3 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listOpenMode5);
        ArrayAdapter adapterCloseMode3 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listCloseMode5);
        adapterMode1and2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterOpenMode3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCloseMode3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spnShowPresetTourMode1To4.setAdapter(adapterMode1and2);
        spnShowPresetOpenTourMode5.setAdapter(adapterOpenMode3);
        spnShowPresetCloseTourMode5.setAdapter(adapterCloseMode3);
    }

    View.OnClickListener onClickImgMenuListDevice = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            layoutListDevice.setVisibility(View.VISIBLE);
//                imgRefreshListDevice.setVisibility(View.VISIBLE);
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            List<String> s = new ArrayList<String>();
//                s.add("---Thiết bị đã ghép đôi---");
            for (BluetoothDevice bt : pairedDevices) {
                s.add(bt.getName() + "\n" + bt.getAddress());
            }
            ObjectBluetooth = pairedDevices.toArray();
//                s.add("---Thiết bị hiện có---");
            arrayAdapterListDevice = new ArrayAdapter<String>(
                    MainActivity.this,
                    simple_list_item_1,
                    s);
            lvListDevice.setAdapter(arrayAdapterListDevice);
        }
    };

    View.OnClickListener onClickTxtBackListDevice = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            layoutListDevice.setVisibility(View.INVISIBLE);
            pgbRefreshListDevice.setVisibility(View.INVISIBLE);
        }
    };

    AdapterView.OnItemClickListener onClickLvListDevice = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(mmDevice != null && isConnected(mmDevice)){
                mConnectedThread.cancel();
                mmDevice = null;
                layoutListDevice.setVisibility(View.INVISIBLE);
                setLayoutDisconnectBluetooth();
                return;
            }
            pgbRefreshListDevice.setVisibility(View.VISIBLE);
            BluetoothDevice bluetoothDeviceConnect = (BluetoothDevice) ObjectBluetooth[i];

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
            }
            deviceName = bluetoothDeviceConnect.getName();
            String deviceAddress = bluetoothDeviceConnect.getAddress();

            Log.d(TAG, "onItemClick: deviceName = " + deviceName);
            Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);
            Log.d(TAG, "Trying to Pair with " + deviceName);
            bluetoothDeviceConnect.createBond();

            mDeviceUUIDs = bluetoothDeviceConnect.getUuids();

            Log.d(TAG, "Trying to create UUID: " + deviceName);

            for (ParcelUuid uuid : mDeviceUUIDs) {
                Log.d(TAG, "UUID: " + uuid.getUuid().toString());
            }
            ConnectThread connect = new ConnectThread(bluetoothDeviceConnect, MY_UUID_INSECURE);
            connect.start();
        }
    };

    View.OnClickListener onClickImgAddPreset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddPreset.setVisibility(View.VISIBLE);
            imgDeleteAddPreset.setVisibility(View.INVISIBLE);
            disableLayout(rlShowDataSettingMode);
            edtSetNamePreset.getText().clear();
            for(int i = 0; i < 20; i++){
                edtAngleServo[i].getText().clear();
            }
            currentSelectedPresetList = lvListDataPreset.getAdapter().getCount() + 1;
        }
    };

    View.OnClickListener onClickImgSaveDataToLocalFile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            JSONArray jsonArrayPreset = new JSONArray();
            if(listDataPreset.size() == 0){
                Toast.makeText(MainActivity.this, "No data to Save", Toast.LENGTH_SHORT).show();
                return;
            }
            //data example [{"1":"ngoc","2":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]}]
            for(int i = 0; i < listDataPreset.size(); i++){
                DataPreset dataPreset = listDataPreset.get(i);
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArrayAngle = new JSONArray();
                try {
                    jsonObject.put("1", dataPreset.getName());
                    int[] angle = dataPreset.getAngle();
                    for(int j = 0; j < angle.length; j++){
                        jsonArrayAngle.put(angle[j]);
                    }
                    jsonObject.put("2", jsonArrayAngle);
                    jsonArrayPreset.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            Log.i("jsonArrayData", jsonArray.toString());
//            editor.putString(presetStr, jsonArrayPreset.toString());

            //data example" {"1":[{"1":"ngoc","2":2}], "2":[{"1":"tuyet","2":2}], "3":[{"1":"tuyet","2":2}], "2":[{"4":"tuyet","2":2}], "5":[1,2]}
            JSONObject jsonObjectTour = new JSONObject();
            if(lvListDataTour1.getCount() > 0){
                JSONArray jsonArrayTour1 = new JSONArray();
                for(int i = 0; i < lvListDataTour1.getCount(); i++){
                    String data = (String) lvListDataTour1.getItemAtPosition(i);
                    String dataName = data.substring(6,data.indexOf("\nTime: "));
                    String dataTime = data.substring(data.indexOf("\nTime: ") + 7, data.length()-1);
//                    Log.i("jsonArrayData", "Name: " + dataName + " - Time: " + dataTime);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("1", dataName);
                        jsonObject.put("2", Integer.valueOf(dataTime));
                        jsonArrayTour1.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jsonObjectTour.put("1", jsonArrayTour1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(lvListDataTour2.getCount() > 0){
                JSONArray jsonArrayTour2 = new JSONArray();
                for(int i = 0; i < lvListDataTour2.getCount(); i++){
                    String data = (String) lvListDataTour2.getItemAtPosition(i);
                    String dataName = data.substring(6,data.indexOf("\nTime: "));
                    String dataTime = data.substring(data.indexOf("\nTime: ") + 7, data.length()-1);
//                    Log.i("jsonArrayData", "Name: " + dataName + " - Time: " + dataTime);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("1", dataName);
                        jsonObject.put("2", Integer.valueOf(dataTime));
                        jsonArrayTour2.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jsonObjectTour.put("2", jsonArrayTour2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(lvListDataTour3.getCount() > 0){
                JSONArray jsonArrayTour3 = new JSONArray();
                for(int i = 0; i < lvListDataTour3.getCount(); i++){
                    String data = (String) lvListDataTour3.getItemAtPosition(i);
                    String dataName = data.substring(6,data.indexOf("\nTime: "));
                    String dataTime = data.substring(data.indexOf("\nTime: ") + 7, data.length()-1);
//                    Log.i("jsonArrayData", "Name: " + dataName + " - Time: " + dataTime);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("1", dataName);
                        jsonObject.put("2", Integer.valueOf(dataTime));
                        jsonArrayTour3.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jsonObjectTour.put("3", jsonArrayTour3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(lvListDataTour4.getCount() > 0){
                JSONArray jsonArrayTour4 = new JSONArray();
                for(int i = 0; i < lvListDataTour4.getCount(); i++){
                    String data = (String) lvListDataTour4.getItemAtPosition(i);
                    String dataName = data.substring(6,data.indexOf("\nTime: "));
                    String dataTime = data.substring(data.indexOf("\nTime: ") + 7, data.length()-1);
//                    Log.i("jsonArrayData", "Name: " + dataName + " - Time: " + dataTime);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("1", dataName);
                        jsonObject.put("2", Integer.valueOf(dataTime));
                        jsonArrayTour4.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jsonObjectTour.put("4", jsonArrayTour4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                JSONArray jsonArrayTour5 = new JSONArray();
                jsonArrayTour5.put(spnShowPresetOpenTourMode5.getSelectedItemPosition());
                jsonArrayTour5.put(spnShowPresetCloseTourMode5.getSelectedItemPosition());
                jsonObjectTour.put("5", jsonArrayTour5);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject jsonObjectFile = new JSONObject();
            try {
                jsonObjectFile.put("preset", jsonArrayPreset);
                jsonObjectFile.put("tour", jsonObjectTour);
                Log.i("jsonArrayData", jsonObjectFile.toString());
                saveTextFileToDownloadFolder(jsonObjectFile.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    View.OnClickListener onClickImgGetDataFromLocalFile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            data.setType("*/*");
            data = Intent.createChooser(data, "Choose a file");
            if(data.resolveActivity(getPackageManager()) != null){
                startActivityForResult(data, REQUEST_CODE_SELECT_FILE_SETTING);
            }
        }
    };

    View.OnClickListener onClickImgSyncDataPresetFromDevice = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mmDevice != null && isConnected(mmDevice)) {
                JSONObject jsonObjectData = new JSONObject();
                try {
                    jsonObjectData.put("type", "save_preset");
                    JSONArray jsonArray = new JSONArray(dataPresetCurrent);
                    jsonObjectData.put("data", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                byte[] bytes = jsonObjectData.toString().getBytes(Charset.defaultCharset());
                mConnectedThread.write(bytes);
                Toast.makeText(MainActivity.this, "DONE", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener onClickImgSyncDataTourFromDevice = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mmDevice != null && isConnected(mmDevice)) {
                JSONObject jsonObjectData = new JSONObject();
                try {
                    jsonObjectData.put("type", "save_tour");
                    JSONArray jsonArrayMode5 = new JSONArray();
                    jsonArrayMode5.put(spnShowPresetOpenTourMode5.getSelectedItemPosition());
                    jsonArrayMode5.put(spnShowPresetCloseTourMode5.getSelectedItemPosition());
                    JSONObject jsonObject = new JSONObject(dataTourCurrent);
                    jsonObject.put("5", jsonArrayMode5);
                    dataTourCurrent = jsonObject.toString();
                    jsonObjectData.put("data", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                byte[] bytes = jsonObjectData.toString().getBytes(Charset.defaultCharset());
                mConnectedThread.write(bytes);
                Toast.makeText(MainActivity.this, "DONE", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener onClickImgOkAddPreset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(edtSetNamePreset.getText().toString().equals("")){
                Toast.makeText(MainActivity.this, "Enter Name!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(currentSelectedPresetList > lvListDataPreset.getAdapter().getCount()){
                Log.i("jsonArrayData", String.valueOf(lvListDataPreset.getAdapter().getCount()));
                for(int i = 0; i < lvListDataPreset.getAdapter().getCount(); i++){
                    DataPreset dataPreset = (DataPreset) listDataPreset.get(i);
                    if(edtSetNamePreset.getText().toString().equals(dataPreset.getName())){
                        Toast.makeText(MainActivity.this, "Not Enter The Same Name!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        Log.i("jsonArrayData", "null");
                    }
                }
            }
            rlBackgroundFragmentAddPreset.setVisibility(View.INVISIBLE);
            enableLayout(rlShowDataSettingMode);
            JSONArray jsonArrayData = new JSONArray();
            try {
                JSONObject jsonObjectData = new JSONObject();
                JSONArray jsonArrayAngle = new JSONArray();
                if(!dataPresetCurrent.equals("")){
                    jsonArrayData = new JSONArray(dataPresetCurrent);
                }
                jsonObjectData.put("1", edtSetNamePreset.getText().toString());
                for(int i = 0; i < 20; i++){
                    if(!edtAngleServo[i].getText().toString().equals(""))
                    {
                        jsonArrayAngle.put(Integer.valueOf(edtAngleServo[i].getText().toString()));
                    }
                    else{
                        jsonArrayAngle.put(0);
                    }
                }
                jsonObjectData.put("2",jsonArrayAngle);
                if(currentSelectedPresetList > lvListDataPreset.getAdapter().getCount()){
                    jsonArrayData.put(jsonObjectData);
                }
                else{
                    jsonArrayData.put(currentSelectedPresetList, jsonObjectData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dataPresetCurrent = jsonArrayData.toString();
            loadDataPreset(dataPresetCurrent);
            updateListPresetToSpinner();
//            editor.putString(presetStr, dataPreset);
//            editor.commit();

        }
    };

    View.OnClickListener onClickImgDeleteAddPreset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddPreset.setVisibility(View.INVISIBLE);
            enableLayout(rlShowDataSettingMode);
            try {
                JSONArray jsonArrayData = new JSONArray(dataPresetCurrent);
                jsonArrayData.remove(currentSelectedPresetList);
                dataPresetCurrent = jsonArrayData.toString();
                loadDataPreset(dataPresetCurrent);
//                editor.putString(presetStr, dataPresetCurrent);
//                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateListPresetToSpinner();
        }
    };

    View.OnClickListener onClickImgCancelAddPreset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddPreset.setVisibility(View.INVISIBLE);
            enableLayout(rlShowDataSettingMode);
        }
    };

    AdapterView.OnItemClickListener onClickLvListDataPreset = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            rlBackgroundFragmentAddPreset.setVisibility(View.VISIBLE);
            imgDeleteAddPreset.setVisibility(View.VISIBLE);
            disableLayout(rlShowDataSettingMode);
            DataPreset dataPreset = listDataPreset.get(i);
            int[] angle = dataPreset.getAngle();
            for(int j = 0; j < angle.length; j++){
                edtSetNamePreset.setText(dataPreset.getName());
                edtAngleServo[j].setText(String.valueOf(angle[j]));
            }
            currentSelectedPresetList = i;
        }
    };

    View.OnClickListener onClickLlSelectSettingPreset = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            llSelectSettingPreset.setBackgroundColor(R.color.darkMenu);
            llSelectSettingTour.setBackgroundColor(android.R.color.transparent);
            layoutSetupPreset.setVisibility(View.VISIBLE);
            layoutSetupTour.setVisibility(View.INVISIBLE);
        }
    };

    View.OnClickListener onClickLlSelectSettingTour = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            llSelectSettingTour.setBackgroundColor(R.color.darkMenu);
            llSelectSettingPreset.setBackgroundColor(android.R.color.transparent);
            layoutSetupPreset.setVisibility(View.INVISIBLE);
            layoutSetupTour.setVisibility(View.VISIBLE);
            rlBackgroundFragmentAddPreset.setVisibility(View.INVISIBLE);
        }
    };

    View.OnClickListener onClickImgAddTourMode1 = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.INVISIBLE);
            disableLayout(rlShowDataSettingMode);
            currentSelectedTourMode = 1;
            currentSelectedPresetOfTourList = lvListDataTour1.getAdapter().getCount() + 1;
        }
    };

    View.OnClickListener onClickImgAddTourMode2 = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.INVISIBLE);
            disableLayout(rlShowDataSettingMode);
            currentSelectedTourMode = 2;
            currentSelectedPresetOfTourList = lvListDataTour2.getAdapter().getCount() + 1;
        }
    };

    View.OnClickListener onClickImgAddTourMode3 = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.INVISIBLE);
            disableLayout(rlShowDataSettingMode);
            currentSelectedTourMode = 3;
            currentSelectedPresetOfTourList = lvListDataTour3.getAdapter().getCount() + 1;
        }
    };

    View.OnClickListener onClickImgAddTourMode4 = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.INVISIBLE);
            disableLayout(rlShowDataSettingMode);
            currentSelectedTourMode = 4;
            currentSelectedPresetOfTourList = lvListDataTour4.getAdapter().getCount() + 1;
        }
    };

    View.OnClickListener onClickImgOkAddTour = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            if(edtSetTimeDelayPresetOfTour.getText().toString().equals("") || edtSetTimeDelayPresetOfTour.getText().toString().equals("0")){
                Toast.makeText(MainActivity.this, "Enter Time!", Toast.LENGTH_SHORT).show();
                return;
            }
            rlBackgroundFragmentAddTour.setVisibility(View.INVISIBLE);
            enableLayout(rlShowDataSettingMode);
            if(currentSelectedTourMode == 1){
//                String dataJsonTour = sharedPreferences.getString(tourStr, "");
                JSONObject jsonObjectTour = new JSONObject();
                try {
                    if(!dataTourCurrent.equals("")){
                        jsonObjectTour = new JSONObject(dataTourCurrent);
                        if(jsonObjectTour.has("1")){
                            JSONArray jsonArrayMode1 = jsonObjectTour.getJSONArray("1");
                            JSONObject jsonObjectMode1 = new JSONObject();
                            jsonObjectMode1.put("1", spnShowPresetTourMode1To4.getSelectedItem().toString());
                            jsonObjectMode1.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            if(currentSelectedPresetOfTourList > lvListDataTour1.getAdapter().getCount()){
                                jsonArrayMode1.put(jsonObjectMode1);
                            }
                            else{
                                jsonArrayMode1.put(currentSelectedPresetOfTourList, jsonObjectMode1);
                            }
                            jsonObjectTour.put("1", jsonArrayMode1);
                        }
                        else{
                            JSONArray jsonArrayMode1 = new JSONArray();
                            JSONObject jsonObjectMode1 = new JSONObject();
                            jsonObjectMode1.put("1",spnShowPresetTourMode1To4.getSelectedItem().toString());
                            jsonObjectMode1.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            jsonArrayMode1.put(jsonObjectMode1);
                            jsonObjectTour.put("1", jsonArrayMode1);
                            Log.i("jsonArrayData", jsonObjectTour.toString());
                        }
//                        editor.putString(tourStr, jsonObjectTour.toString());
                        dataTourCurrent = jsonObjectTour.toString();
                    }
                    else{
                        JSONArray jsonArrayMode1 = new JSONArray();
                        JSONObject jsonObjectMode1 = new JSONObject();
                        jsonObjectMode1.put("1",spnShowPresetTourMode1To4.getSelectedItem().toString());
                        jsonObjectMode1.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                        jsonArrayMode1.put(jsonObjectMode1);
                        jsonObjectTour.put("1", jsonArrayMode1);
                        Log.i("jsonArrayData", jsonObjectTour.toString());
//                        editor.putString(tourStr, jsonObjectTour.toString());
                        dataTourCurrent = jsonObjectTour.toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(currentSelectedTourMode == 2){
                JSONObject jsonObjectTour = new JSONObject();
                try {
                    if(!dataTourCurrent.equals("")){
                        jsonObjectTour = new JSONObject(dataTourCurrent);
                        if(jsonObjectTour.has("2")){
                            JSONArray jsonArrayMode2 = jsonObjectTour.getJSONArray("2");
                            JSONObject jsonObjectMode2 = new JSONObject();
                            jsonObjectMode2.put("1", spnShowPresetTourMode1To4.getSelectedItem().toString());
                            jsonObjectMode2.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            if(currentSelectedPresetOfTourList > lvListDataTour2.getAdapter().getCount()){
                                jsonArrayMode2.put(jsonObjectMode2);
                            }
                            else{
                                jsonArrayMode2.put(currentSelectedPresetOfTourList, jsonObjectMode2);
                            }
                            jsonObjectTour.put("2", jsonArrayMode2);
                        }
                        else{
                            JSONArray jsonArrayMode2 = new JSONArray();
                            JSONObject jsonObjectMode2 = new JSONObject();
                            jsonObjectMode2.put("1",spnShowPresetTourMode1To4.getSelectedItem().toString());
                            jsonObjectMode2.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            jsonArrayMode2.put(jsonObjectMode2);
                            jsonObjectTour.put("2", jsonArrayMode2);
                            Log.i("jsonArrayData", jsonObjectTour.toString());
                        }
//                        editor.putString(tourStr, jsonObjectTour.toString());
                        dataTourCurrent = jsonObjectTour.toString();
                    }
                    else{
                        JSONArray jsonArrayMode2 = new JSONArray();
                        JSONObject jsonObjectMode2 = new JSONObject();
                        jsonObjectMode2.put("1",spnShowPresetTourMode1To4.getSelectedItem().toString());
                        jsonObjectMode2.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                        jsonArrayMode2.put(jsonObjectMode2);
                        jsonObjectTour.put("2", jsonArrayMode2);
                        Log.i("jsonArrayData", jsonObjectTour.toString());
//                        editor.putString(tourStr, jsonObjectTour.toString());
                        dataTourCurrent = jsonObjectTour.toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(currentSelectedTourMode == 3){
//                String dataJsonTour = sharedPreferences.getString(tourStr, "");
                JSONObject jsonObjectTour = new JSONObject();
                try {
                    if(!dataTourCurrent.equals("")){
                        jsonObjectTour = new JSONObject(dataTourCurrent);
                        if(jsonObjectTour.has("3")){
                            JSONArray jsonArrayMode3 = jsonObjectTour.getJSONArray("3");
                            JSONObject jsonObjectMode3 = new JSONObject();
                            jsonObjectMode3.put("1", spnShowPresetTourMode1To4.getSelectedItem().toString());
                            jsonObjectMode3.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            if(currentSelectedPresetOfTourList > lvListDataTour3.getAdapter().getCount()){
                                jsonArrayMode3.put(jsonObjectMode3);
                            }
                            else{
                                jsonArrayMode3.put(currentSelectedPresetOfTourList, jsonObjectMode3);
                            }
                            jsonObjectTour.put("3", jsonArrayMode3);
                        }
                        else{
                            JSONArray jsonArrayMode3 = new JSONArray();
                            JSONObject jsonObjectMode3 = new JSONObject();
                            jsonObjectMode3.put("1",spnShowPresetTourMode1To4.getSelectedItem().toString());
                            jsonObjectMode3.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            jsonArrayMode3.put(jsonObjectMode3);
                            jsonObjectTour.put("3", jsonArrayMode3);
                            Log.i("jsonArrayData", jsonObjectTour.toString());
                        }
//                        editor.putString(tourStr, jsonObjectTour.toString());
                        dataTourCurrent = jsonObjectTour.toString();
                    }
                    else{
                        JSONArray jsonArrayMode3 = new JSONArray();
                        JSONObject jsonObjectMode3 = new JSONObject();
                        jsonObjectMode3.put("1",spnShowPresetTourMode1To4.getSelectedItem().toString());
                        jsonObjectMode3.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                        jsonArrayMode3.put(jsonObjectMode3);
                        jsonObjectTour.put("3", jsonArrayMode3);
                        Log.i("jsonArrayData", jsonObjectTour.toString());
//                        editor.putString(tourStr, jsonObjectTour.toString());
                        dataTourCurrent = jsonObjectTour.toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(currentSelectedTourMode == 4){
//                String dataJsonTour = sharedPreferences.getString(tourStr, "");
                JSONObject jsonObjectTour = new JSONObject();
                try {
                    if(!dataTourCurrent.equals("")){
                        jsonObjectTour = new JSONObject(dataTourCurrent);
                        if(jsonObjectTour.has("4")){
                            JSONArray jsonArrayMode4 = jsonObjectTour.getJSONArray("4");
                            JSONObject jsonObjectMode4 = new JSONObject();
                            jsonObjectMode4.put("1", spnShowPresetTourMode1To4.getSelectedItem().toString());
                            jsonObjectMode4.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            if(currentSelectedPresetOfTourList > lvListDataTour4.getAdapter().getCount()){
                                jsonArrayMode4.put(jsonObjectMode4);
                            }
                            else{
                                jsonArrayMode4.put(currentSelectedPresetOfTourList, jsonObjectMode4);
                            }
                            jsonObjectTour.put("4", jsonArrayMode4);
                        }
                        else{
                            JSONArray jsonArrayMode4 = new JSONArray();
                            JSONObject jsonObjectMode4 = new JSONObject();
                            jsonObjectMode4.put("1",spnShowPresetTourMode1To4.getSelectedItem().toString());
                            jsonObjectMode4.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            jsonArrayMode4.put(jsonObjectMode4);
                            jsonObjectTour.put("4", jsonArrayMode4);
                            Log.i("jsonArrayData", jsonObjectTour.toString());
                        }
//                        editor.putString(tourStr, jsonObjectTour.toString());
                        dataTourCurrent = jsonObjectTour.toString();
                    }
                    else{
                        JSONArray jsonArrayMode4 = new JSONArray();
                        JSONObject jsonObjectMode4 = new JSONObject();
                        jsonObjectMode4.put("1",spnShowPresetTourMode1To4.getSelectedItem().toString());
                        jsonObjectMode4.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                        jsonArrayMode4.put(jsonObjectMode4);
                        jsonObjectTour.put("4", jsonArrayMode4);
                        Log.i("jsonArrayData", jsonObjectTour.toString());
//                        editor.putString(tourStr, jsonObjectTour.toString());
                        dataTourCurrent = jsonObjectTour.toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            editor.commit();
            loadDataTour(dataTourCurrent);
        }
    };

    View.OnClickListener onClickImgDeleteAddTour = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddTour.setVisibility(View.INVISIBLE);
            enableLayout(rlShowDataSettingMode);
//            String dataJson = sharedPreferences.getString(tourStr, "");
            try {
                JSONObject jsonObjectTour = new JSONObject(dataTourCurrent);
                if(currentSelectedTourMode == 1){
                    JSONArray jsonArrayMode1 = jsonObjectTour.getJSONArray("1");
                    jsonArrayMode1.remove(currentSelectedPresetOfTourList);
                    jsonObjectTour.put("1", jsonArrayMode1);
                }
                else if(currentSelectedTourMode == 2){
                    JSONArray jsonArrayMode2 = jsonObjectTour.getJSONArray("2");
                    jsonArrayMode2.remove(currentSelectedPresetOfTourList);
                    jsonObjectTour.put("2", jsonArrayMode2);
                }
                else if(currentSelectedTourMode == 3){
                    JSONArray jsonArrayMode3 = jsonObjectTour.getJSONArray("3");
                    jsonArrayMode3.remove(currentSelectedPresetOfTourList);
                    jsonObjectTour.put("3", jsonArrayMode3);
                }
                else if(currentSelectedTourMode == 4){
                    JSONArray jsonArrayMode4 = jsonObjectTour.getJSONArray("4");
                    jsonArrayMode4.remove(currentSelectedPresetOfTourList);
                    jsonObjectTour.put("4", jsonArrayMode4);
                }
                dataTourCurrent = jsonObjectTour.toString();
                loadDataTour(dataTourCurrent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener onClickImgCancelAddTour = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddTour.setVisibility(View.INVISIBLE);
            enableLayout(rlShowDataSettingMode);
        }
    };

    AdapterView.OnItemClickListener onClickLvListDataTour1 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.VISIBLE);
            disableLayout(rlShowDataSettingMode);
            String nameClick = (String) lvListDataTour1.getItemAtPosition(i);
            for(int j = 0; j < spnShowPresetTourMode1To4.getCount(); j++){
                String data = (String) spnShowPresetTourMode1To4.getItemAtPosition(j);
//                Log.i("jsonArrayData", nameClick + " - " + data);
                if(nameClick.contains("Name: " + data + "\n")){
                    spnShowPresetTourMode1To4.setSelection(j);
                    break;
                }
            }
            currentSelectedTourMode = 1;
            currentSelectedPresetOfTourList = i;
        }
    };

    ListView.OnTouchListener onTouchLvListDataTour1 =  new ListView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        }
    };

    AdapterView.OnItemClickListener onClickLvListDataTour2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.VISIBLE);
            disableLayout(rlShowDataSettingMode);
            String nameClick = (String) lvListDataTour2.getItemAtPosition(i);
            for(int j = 0; j < spnShowPresetTourMode1To4.getCount(); j++){
                String data = (String) spnShowPresetTourMode1To4.getItemAtPosition(j);
//                Log.i("jsonArrayData", nameClick + " - " + data);
                if(nameClick.contains("Name: " + data + "\n")){
                    spnShowPresetTourMode1To4.setSelection(j);
                    break;
                }
            }
            currentSelectedTourMode = 2;
            currentSelectedPresetOfTourList = i;
        }
    };

    ListView.OnTouchListener onTouchLvListDataTour2 =  new ListView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        }
    };

    AdapterView.OnItemClickListener onClickLvListDataTour3 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.VISIBLE);
            disableLayout(rlShowDataSettingMode);
            String nameClick = (String) lvListDataTour3.getItemAtPosition(i);
            for(int j = 0; j < spnShowPresetTourMode1To4.getCount(); j++){
                String data = (String) spnShowPresetTourMode1To4.getItemAtPosition(j);
//                Log.i("jsonArrayData", nameClick + " - " + data);
                if(nameClick.contains("Name: " + data + "\n")){
                    spnShowPresetTourMode1To4.setSelection(j);
                    break;
                }
            }
            currentSelectedTourMode = 3;
            currentSelectedPresetOfTourList = i;
        }
    };

    ListView.OnTouchListener onTouchLvListDataTour3 =  new ListView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        }
    };

    AdapterView.OnItemClickListener onClickLvListDataTour4 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.VISIBLE);
            disableLayout(rlShowDataSettingMode);
            String nameClick = (String) lvListDataTour4.getItemAtPosition(i);
            for(int j = 0; j < spnShowPresetTourMode1To4.getCount(); j++){
                String data = (String) spnShowPresetTourMode1To4.getItemAtPosition(j);
//                Log.i("jsonArrayData", nameClick + " - " + data);
                if(nameClick.contains("Name: " + data + "\n")){
                    spnShowPresetTourMode1To4.setSelection(j);
                    break;
                }
            }
            currentSelectedTourMode = 4;
            currentSelectedPresetOfTourList = i;
        }
    };

    ListView.OnTouchListener onTouchLvListDataTour4 =  new ListView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        }
    };

    private static void disableLayout(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disableLayout((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    private static void enableLayout(ViewGroup layout) {
        layout.setEnabled(true);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                enableLayout((ViewGroup) child);
            } else {
                child.setEnabled(true);
            }
        }
    }

    public void saveTextFileToDownloadFolder(String content){
        File fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),file_folder_name_save_setting);
        if (!fileDir.exists())
        {
            fileDir.mkdirs();
        }
        File fileSaveTextDownload;
        String file_name = sharedPreferences.getString(file_name_download_save_setting, "");
        if(file_name.equals("")){
            int count = 0;
            while(true){
                fileSaveTextDownload = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        + "/" + file_folder_name_save_setting,file_folder_name_save_setting + String.valueOf(count)+".txt");
                if(!fileSaveTextDownload.exists()){
                    editor.putString(file_name_download_save_setting, file_folder_name_save_setting + String.valueOf(count)+".txt");
                    break;
                }
                if(fileSaveTextDownload.exists()){
                    if(fileSaveTextDownload.delete()){
                        editor.putString(file_name_download_save_setting, file_folder_name_save_setting + String.valueOf(count)+".txt");
                        break;
                    }
                    Log.i("jsonObject put", fileSaveTextDownload.getAbsolutePath());
                }
                count++;
            }
        }
        else{
            fileSaveTextDownload = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/" + file_folder_name_save_setting,file_name);
        }

        if(!fileSaveTextDownload.exists()){
            try {
                fileSaveTextDownload.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(fileSaveTextDownload);
            try {
                fos.write(content.getBytes());
                fos.close();
                Toast.makeText(MainActivity.this, "save!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "error save!", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "file not found!", Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler = new Handler();
    Runnable runnable = null;
    @Override
    protected void onResume() {
        //start handler as activity become visible
        int delay = 3000; //One second = 1000 milliseconds.
        final long[] count = {0};
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                //do something
                if (mmDevice != null && isConnected(mmDevice) && mConnectedThread != null) {
                    count[0]++;
                    if(!isSyncPresetWithDevice){
                        if(count[0]%2 == 0){
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("type", "sync_preset");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            String data = "{\"type\":\"sync_preset\"}";
                            String data = jsonObject.toString();
                            byte[] bytes = data.getBytes(Charset.defaultCharset());
                            mConnectedThread.write(bytes);
                        }
                    }
                    if(!isSyncTourWithDevice){
                        if(count[0]%2 == 1){
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("type", "sync_tour");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            String data = "{\"type\":\"sync_tour\"}";
                            String data = jsonObject.toString();
                            byte[] bytes = data.getBytes(Charset.defaultCharset());
                            mConnectedThread.write(bytes);
                        }
                    }
                }
                if(isSyncPresetWithDevice && isSyncTourWithDevice){
                    prbSyncDataWithDevice.setVisibility(View.INVISIBLE);
                }
                if(mmDevice != null){
                    if(!isConnected(mmDevice) && isSyncPresetWithDevice && isSyncTourWithDevice){
                        setLayoutDisconnectBluetooth();
                    }
                }

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    public void setLayoutDisconnectBluetooth(){
        loadDataPreset("");
        loadDataTour("");
        isSyncPresetWithDevice = false;
        isSyncTourWithDevice = false;
        prbSyncDataWithDevice.setVisibility(View.INVISIBLE);
        imgBluetoothConnection.setBackgroundResource(R.mipmap.ic_bluetooth_disconnected);
        txtNameBluetoothConnection.setText("No Connected");
        txtCurrentModeRunDevice.setText("Mode Running: None");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_FILE_SETTING && resultCode == RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                byte[] bytes = getBytesFromUri(getApplicationContext(), uri);
                String dataFile = new String(bytes);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(dataFile);
                    dataPresetCurrent = jsonObject.getString("preset");
                    dataTourCurrent = jsonObject.getString("tour");
//                    editor.putString(presetStr, dataPreset);
//                    editor.putString(tourStr, dataTour);
//                    editor.commit();
                    loadDataPreset(dataPresetCurrent);
                    loadDataTour(dataTourCurrent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    byte[] getBytesFromUri(Context context, Uri uri){
        InputStream iStream = null;
        try {
            iStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ( (len = iStream.read(buffer)) != -1){
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            //deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.d(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + MY_UUID_INSECURE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
                }
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
                }
                mmSocket.connect();

            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }

            //will talk about this in the 3rd video
            connected(mmSocket);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }
    private void connected(BluetoothSocket mmSocket) {
        Log.d(TAG, "connected: Starting.");
//        pgbRefeshListdevice.setVisibility(View.INVISIBLE);

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                layoutListDevice.setVisibility(View.INVISIBLE);
                pgbRefreshListDevice.setVisibility(View.INVISIBLE);
                imgBluetoothConnection.setBackgroundResource(R.mipmap.ic_bluetooth_connected);
                txtNameBluetoothConnection.setText(deviceName);
                prbSyncDataWithDevice.setVisibility(View.VISIBLE);
            }
        });
    }
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes = 0; // bytes returned from read()
            String incomingMessage = "";
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    incomingMessage += new String(buffer, 0, bytes);
                    //Sync Preset
                    if(incomingMessage.contains("#")){
                        incomingMessage = incomingMessage.replace("#","");
                        Log.d("incomingMessage", "InputStream: " + incomingMessage);
                        dataPresetCurrent = incomingMessage;
                        incomingMessage = "";
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                //-------
                                if(loadDataPreset(dataPresetCurrent)){
                                    isSyncPresetWithDevice = true;
                                }
                            }
                        });

                    }
                    //Sync Tour
                    if(incomingMessage.contains("%")){
                        incomingMessage = incomingMessage.replace("%","");
                        Log.d("incomingMessage", "InputStream: " + incomingMessage);
                        dataTourCurrent = incomingMessage;
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                //-------
                                loadDataTour(dataTourCurrent);
                                isSyncTourWithDevice = true;
                            }
                        });
                        incomingMessage = "";
                    }
                    //Sync Mode Run
                    if(incomingMessage.contains("!")){
                        incomingMessage = incomingMessage.replace("!","");
                        Log.d("incomingMessage", "InputStream: " + incomingMessage);
                        JSONObject reader = new JSONObject(incomingMessage);
                        String finalIncomingMessage = incomingMessage;
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                //-------
//                                loadDataTour(finalIncomingMessage);
                            }
                        });
                        incomingMessage = "";
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    if(Objects.equals(e.getMessage(), "socket closed")){
                        break;
                    }
//                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}