package com.example.control_20_servo;

import static android.R.layout.simple_list_item_1;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
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
    ArrayAdapter<String> arrayAdapterListDataTour1, arrayAdapterListDataTour2;
    public ListView lvListDataPreset, lvListDataTour1, lvListDataTour2;
    public RelativeLayout rlShowDataSettingMode;
    ImageView imgAddPreset, imgSendDataPresetToDevice, imgSaveDataToLocalFile, imgGetDataFromLocalFile, imgSyncDataFromDevice;
    ImageView imgOkAddPreset, imgDeleteAddPreset, imgCancelAddPreset;
    ImageView imgAddTourMode1, imgAddTourMode2;
    ImageView imgSendDataTourMode1ToDevice, imgSendDataTourMode2ToDevice, imgSendDataTourMode3ToDevice;
    ImageView imgOkAddTour, imgDeleteAddTour, imgCancelAddTour;

    Spinner spnShowPresetOpenTourMode3, spnShowPresetCloseTourMode3, spnShowPresetTourMode1And2;
    EditText edtSetTimeDelayPresetOfTour;
    EditText edtSetNamePreset;
    EditText[] edtAngleServo = new EditText[20];


    public static RelativeLayout rlBackgroundFragmentAddPreset, rlBackgroundFragmentAddTour;
    View layoutSetupPreset, layoutSetupTour;

    int currentSelectedPresetList = 0;
    int currentSelectedTourMode = 0;
    int currentSelectedPresetOfTourList = 0;

    public static int REQUEST_BLUETOOTH = 1;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //variable for save name motor
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String presetStr = "preset";
    String tourStr = "tour";


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        setContentView(R.layout.activity_main);
        checkPermissions();
        initLayout();
        initBluetooth();
        initDataLocal();
        loadDataPreset(sharedPreferences.getString(presetStr, ""));
        loadDataTour(sharedPreferences.getString(tourStr, ""));

        imgMenuListDevice.setOnClickListener(onClickImgMenuListDevice);
        txtBackListDevice.setOnClickListener(onClickTxtBackListDevice);
        lvListDevice.setOnItemClickListener(onClickLvListDevice);
        imgAddPreset.setOnClickListener(onClickImgAddPreset);
        imgSendDataPresetToDevice.setOnClickListener(onClickImgSendDataPresetToDevice);
        imgSaveDataToLocalFile.setOnClickListener(onClickImgSaveDataToLocalFile);
        imgGetDataFromLocalFile.setOnClickListener(onClickImgGetDataFromLocalFile);
        imgSyncDataFromDevice.setOnClickListener(onClickImgSyncDataFromDevice);
        imgOkAddPreset.setOnClickListener(onClickImgOkAddPreset);
        imgDeleteAddPreset.setOnClickListener(onClickImgDeleteAddPreset);
        imgCancelAddPreset.setOnClickListener(onClickImgCancelAddPreset);
        lvListDataPreset.setOnItemClickListener(onClickLvListDataPreset);
        llSelectSettingPreset.setOnClickListener(onClickLlSelectSettingPreset);
        llSelectSettingTour.setOnClickListener(onClickLlSelectSettingTour);
        imgAddTourMode1.setOnClickListener(onClickImgAddTourMode1);
        imgAddTourMode2.setOnClickListener(onClickImgAddTourMode2);
        imgOkAddTour.setOnClickListener(onClickImgOkAddTour);
        imgDeleteAddTour.setOnClickListener(onClickImgDeleteAddTour);
        imgCancelAddTour.setOnClickListener(onClickImgCancelAddTour);

        lvListDataTour1.setOnItemClickListener(onClickLvListDataTour1);
        lvListDataTour2.setOnItemClickListener(onClickLvListDataTour2);

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
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
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

        llSelectSettingPreset = findViewById(R.id.llSelectSettingPreset);
        llSelectSettingTour = findViewById(R.id.llSelectSettingTour);

        imgAddPreset = findViewById(R.id.imgAddPreset);
        imgSendDataPresetToDevice = findViewById(R.id.imgSendDataPresetToDevice);
        imgSaveDataToLocalFile = findViewById(R.id.imgSaveDataToLocalFile);
        imgGetDataFromLocalFile = findViewById(R.id.imgGetDataFromLocalFile);
        imgSyncDataFromDevice = findViewById(R.id.imgSyncDataFromDevice);

        imgOkAddPreset = findViewById(R.id.imgOkAddPreset);
        imgDeleteAddPreset = findViewById(R.id.imgDeleteAddPreset);
        imgCancelAddPreset = findViewById(R.id.imgCancelAddPreset);

        imgAddTourMode1 = findViewById(R.id.imgAddTourMode1);
        imgAddTourMode2 = findViewById(R.id.imgAddTourMode2);
        imgSendDataTourMode1ToDevice = findViewById(R.id.imgSendDataTourMode1ToDevice);
        imgSendDataTourMode2ToDevice = findViewById(R.id.imgSendDataTourMode2ToDevice);
        imgSendDataTourMode3ToDevice = findViewById(R.id.imgSendDataTourMode3ToDevice);

        imgOkAddTour = findViewById(R.id.imgOkAddTour);
        imgDeleteAddTour = findViewById(R.id.imgDeleteAddTour);
        imgCancelAddTour = findViewById(R.id.imgCancelAddTour);

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

        spnShowPresetOpenTourMode3 = findViewById(R.id.spnShowPresetOpenTourMode3);
        spnShowPresetCloseTourMode3 = findViewById(R.id.spnShowPresetCloseTourMode3);
        spnShowPresetTourMode1And2 = findViewById(R.id.spnShowPresetTourMode1And2);
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

    public void loadDataPreset(String jsonData){
        listDataPreset = new ArrayList<>();
        DataPresetAdapter adapter = new DataPresetAdapter(this, R.layout.list_view_data_preset, listDataPreset);
        lvListDataPreset.setAdapter(adapter);
        //-------------------
        if(!jsonData.equals("")){
            try {
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
            }
        }

    }

    public void loadDataTour(String dataTour){
        arrayAdapterListDataTour1 = new ArrayAdapter<String>(MainActivity.this, simple_list_item_1);
        arrayAdapterListDataTour2 = new ArrayAdapter<String>(MainActivity.this, simple_list_item_1);
//        String dataTour = sharedPreferences.getString(tourStr, "");
        JSONObject jsonObjectTour = new JSONObject();
        //data example" {"1":[{"1":"ngoc","2":2}], "2":[{"1":"tuyet","2":2}], "3":["ngoc, "tuyet"]}
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
                String openMode3 = jsonArrayMode3.getString(0);
                String closeMode3 = jsonArrayMode3.getString(1);
                for(int i = 0; i < spnShowPresetOpenTourMode3.getAdapter().getCount(); i++){
                    if(spnShowPresetOpenTourMode3.getAdapter().getItem(i).toString().equals(openMode3)){
                        spnShowPresetOpenTourMode3.setSelection(i);
                    }
                    if(spnShowPresetCloseTourMode3.getAdapter().getItem(i).toString().equals(closeMode3)){
                        spnShowPresetCloseTourMode3.setSelection(i);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        lvListDataTour1.setAdapter(arrayAdapterListDataTour1);
        lvListDataTour2.setAdapter(arrayAdapterListDataTour2);
        //update list preset to spinner
        updateListPresetToSpinner();
    }

    public void updateListPresetToSpinner(){
        List<String> listMode1and2 = new ArrayList<>();
        List<String> listOpenMode3 = new ArrayList<>();
        List<String> listCloseMode3 = new ArrayList<>();
        try {
            String dataJson = sharedPreferences.getString(presetStr, "");
            JSONArray jsonArray = new JSONArray();
            if(!dataJson.equals("")){
                jsonArray = new JSONArray(dataJson);
            }
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("1");
                listMode1and2.add(name);
                listOpenMode3.add(name);
                listCloseMode3.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter adapterMode1and2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listMode1and2);
        ArrayAdapter adapterOpenMode3 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listOpenMode3);
        ArrayAdapter adapterCloseMode3 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listCloseMode3);
        adapterMode1and2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterOpenMode3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCloseMode3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spnShowPresetTourMode1And2.setAdapter(adapterMode1and2);
        spnShowPresetOpenTourMode3.setAdapter(adapterOpenMode3);
        spnShowPresetCloseTourMode3.setAdapter(adapterCloseMode3);
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
//                Log.i(TAG, arrayAdapterListdevice.getItem(i));
            pgbRefreshListDevice.setVisibility(View.VISIBLE);
            BluetoothDevice bluetoothDeviceconnect = (BluetoothDevice) ObjectBluetooth[i];

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
            }
            deviceName = bluetoothDeviceconnect.getName();
            String deviceAdress = bluetoothDeviceconnect.getAddress();

            Log.d(TAG, "onItemClick: deviceName = " + deviceName);
            Log.d(TAG, "onItemClick: deviceAdress = " + deviceAdress);
            Log.d(TAG, "Trying to Pair with " + deviceName);
            bluetoothDeviceconnect.createBond();

            mDeviceUUIDs = bluetoothDeviceconnect.getUuids();


            Log.d(TAG, "Trying to create UUID: " + deviceName);

            for (ParcelUuid uuid : mDeviceUUIDs) {
                Log.d(TAG, "UUID: " + uuid.getUuid().toString());
            }
            ConnectThread connect = new ConnectThread(bluetoothDeviceconnect, MY_UUID_INSECURE);
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

    View.OnClickListener onClickImgSendDataPresetToDevice = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    View.OnClickListener onClickImgSaveDataToLocalFile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            JSONArray jsonArray = new JSONArray();
            if(listDataPreset.size() == 0){
                Toast.makeText(MainActivity.this, "No data to Save", Toast.LENGTH_SHORT).show();
                return;
            }
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
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            Log.i("jsonArrayData", jsonArray.toString());
            editor.putString(presetStr, jsonArray.toString());

            Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener onClickImgGetDataFromLocalFile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    View.OnClickListener onClickImgSyncDataFromDevice = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
            String dataPreset = sharedPreferences.getString(presetStr, "");
            try {
                JSONObject jsonObjectData = new JSONObject();
                JSONArray jsonArrayAngle = new JSONArray();
                if(!dataPreset.equals("")){
                    jsonArrayData = new JSONArray(dataPreset);
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
            dataPreset = jsonArrayData.toString();
            editor.putString(presetStr, dataPreset);
            editor.commit();

            //-------------------
            try {
                JSONArray jsonArrayDataReceive = new JSONArray(dataPreset);
                listDataPreset.clear();
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
                lvListDataPreset.invalidateViews();
                lvListDataPreset.refreshDrawableState();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateListPresetToSpinner();
        }
    };

    View.OnClickListener onClickImgDeleteAddPreset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddPreset.setVisibility(View.INVISIBLE);
            enableLayout(rlShowDataSettingMode);
            try {
                String dataJson = sharedPreferences.getString(presetStr, "");
                JSONArray jsonArrayData = new JSONArray(dataJson);
                jsonArrayData.remove(currentSelectedPresetList);
                dataJson = jsonArrayData.toString();
                editor.putString(presetStr, dataJson);
                editor.commit();
                listDataPreset.clear();
                for(int i = 0; i < jsonArrayData.length(); i++){
                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                    String name = jsonObjectData.getString("1");
                    JSONArray jsonArrayAngle = jsonObjectData.getJSONArray("2");
                    int[] angle = new int[20];
                    for(int j = 0; j < jsonArrayAngle.length(); j++){
                        angle[j] = jsonArrayAngle.getInt(j);
                    }
                    listDataPreset.add(new DataPreset(i, name, angle));
                }
                lvListDataPreset.invalidateViews();
                lvListDataPreset.refreshDrawableState();
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
                String dataJsonTour = sharedPreferences.getString(tourStr, "");
                JSONObject jsonObjectTour = new JSONObject();
                try {
                    if(!dataJsonTour.equals("")){
                        jsonObjectTour = new JSONObject(dataJsonTour);
                        if(jsonObjectTour.has("1")){
                            JSONArray jsonArrayMode1 = jsonObjectTour.getJSONArray("1");
                            JSONObject jsonObjectMode1 = new JSONObject();
                            jsonObjectMode1.put("1", spnShowPresetTourMode1And2.getSelectedItem().toString());
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
                            jsonObjectMode1.put("1",spnShowPresetTourMode1And2.getSelectedItem().toString());
                            jsonObjectMode1.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            jsonArrayMode1.put(jsonObjectMode1);
                            jsonObjectTour.put("1", jsonArrayMode1);
                            Log.i("jsonArrayData", jsonObjectTour.toString());
                        }
                        editor.putString(tourStr, jsonObjectTour.toString());
                    }
                    else{
                        JSONArray jsonArrayMode1 = new JSONArray();
                        JSONObject jsonObjectMode1 = new JSONObject();
                        jsonObjectMode1.put("1",spnShowPresetTourMode1And2.getSelectedItem().toString());
                        jsonObjectMode1.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                        jsonArrayMode1.put(jsonObjectMode1);
                        jsonObjectTour.put("1", jsonArrayMode1);
                        Log.i("jsonArrayData", jsonObjectTour.toString());
                        editor.putString(tourStr, jsonObjectTour.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(currentSelectedTourMode == 2){
                String dataJsonTour = sharedPreferences.getString(tourStr, "");
                JSONObject jsonObjectTour = new JSONObject();
                try {
                    if(!dataJsonTour.equals("")){
                        jsonObjectTour = new JSONObject(dataJsonTour);
                        if(jsonObjectTour.has("2")){
                            JSONArray jsonArrayMode2 = jsonObjectTour.getJSONArray("2");
                            JSONObject jsonObjectMode2 = new JSONObject();
                            jsonObjectMode2.put("1", spnShowPresetTourMode1And2.getSelectedItem().toString());
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
                            jsonObjectMode2.put("1",spnShowPresetTourMode1And2.getSelectedItem().toString());
                            jsonObjectMode2.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                            jsonArrayMode2.put(jsonObjectMode2);
                            jsonObjectTour.put("2", jsonArrayMode2);
                            Log.i("jsonArrayData", jsonObjectTour.toString());
                        }
                        editor.putString(tourStr, jsonObjectTour.toString());
                    }
                    else{
                        JSONArray jsonArrayMode2 = new JSONArray();
                        JSONObject jsonObjectMode2 = new JSONObject();
                        jsonObjectMode2.put("1",spnShowPresetTourMode1And2.getSelectedItem().toString());
                        jsonObjectMode2.put("2",Integer.valueOf(edtSetTimeDelayPresetOfTour.getText().toString()));
                        jsonArrayMode2.put(jsonObjectMode2);
                        jsonObjectTour.put("2", jsonArrayMode2);
                        Log.i("jsonArrayData", jsonObjectTour.toString());
                        editor.putString(tourStr, jsonObjectTour.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            editor.commit();
            loadDataTour(sharedPreferences.getString(tourStr, ""));
        }
    };

    View.OnClickListener onClickImgDeleteAddTour = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddTour.setVisibility(View.INVISIBLE);
            enableLayout(rlShowDataSettingMode);
            String dataJson = sharedPreferences.getString(tourStr, "");
            try {
                JSONObject jsonObjectTour = new JSONObject(dataJson);
                if(currentSelectedTourMode == 1){
                    JSONArray jsonArrayMode1 = jsonObjectTour.getJSONArray("1");
                    jsonArrayMode1.remove(currentSelectedPresetOfTourList);
                    jsonObjectTour.put("1", jsonArrayMode1);
                    editor.putString(tourStr, jsonObjectTour.toString());
                    editor.commit();
                    loadDataTour(sharedPreferences.getString(tourStr, ""));
                }
                else if(currentSelectedTourMode == 2){
                    JSONArray jsonArrayMode2 = jsonObjectTour.getJSONArray("2");
                    jsonArrayMode2.remove(currentSelectedPresetOfTourList);
                    jsonObjectTour.put("2", jsonArrayMode2);
                    editor.putString(tourStr, jsonObjectTour.toString());
                    editor.commit();
                    loadDataTour(sharedPreferences.getString(tourStr, ""));
                }
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
            currentSelectedTourMode = 1;
            currentSelectedPresetOfTourList = i;
        }
    };

    AdapterView.OnItemClickListener onClickLvListDataTour2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            rlBackgroundFragmentAddTour.setVisibility(View.VISIBLE);
            imgDeleteAddTour.setVisibility(View.VISIBLE);
            disableLayout(rlShowDataSettingMode);
            currentSelectedTourMode = 2;
            currentSelectedPresetOfTourList = i;
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

//        byte[] bytes = "abcd".getBytes(Charset.defaultCharset());
//        mConnectedThread.write(bytes);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                layoutListDevice.setVisibility(View.INVISIBLE);
                pgbRefreshListDevice.setVisibility(View.INVISIBLE);
                imgBluetoothConnection.setBackgroundResource(R.mipmap.ic_bluetooth_connected);
                txtNameBluetoothConnection.setText(deviceName);
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

            int bytes; // bytes returned from read()
            final byte delimiter = 10; //This is the ASCII code for a newline character

            byte[] readBuffer = new byte[1024];;
            int readBufferPosition = 0;
            String incomingMessage = "";
            int data[] = new int[12];
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    incomingMessage += new String(buffer, 0, bytes);
                    if(incomingMessage.contains("}")){
                        Log.d(TAG, "InputStream: " + incomingMessage);
                        JSONObject reader = new JSONObject(incomingMessage);
                        JSONArray jsonArray = reader.getJSONArray("1");
                        for(int i = 0; i < 12; i++){
                            data[i] = jsonArray.getInt(i);
                        }
                        incomingMessage = "";
                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for(int i = 0; i < 12; i++){
//                                sbControlLight[i].setProgress(data[i]);
//                            }
//                        }
//                    });


                } catch (IOException | JSONException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
//                    Toast.makeText(MainActivity.this, "Kết nối thất bại", Toast.LENGTH_SHORT).show();
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