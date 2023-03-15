package com.example.control_20_servo;

import static android.R.layout.simple_list_item_1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
    public ListView lvListDataPreset;
    public RelativeLayout rlShowDataSettingMode;
    public static ImageView imgAddPreset, imgSendDataPresetToDevice, imgSaveDataToLocalFile, imgGetDataFromLocalFile, imgSyncDataFromDevice;
    public static ImageView imgOkAddPreset, imgDeleteAddPreset, imgCancelAddPreset;

    EditText edtSetNamePreset;
    EditText[] edtAngleServo = new EditText[20];


    public static RelativeLayout rlBackgroundFragmentAddPreset;

    String dataJsonSetupBegin;
    int currentSelectedPreset = 0;

    public static int REQUEST_BLUETOOTH = 1;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


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
        loadDataBegin();

        imgMenuListDevice.setOnClickListener(onClickImgMenuListDevice);
        txtBackListDevice.setOnClickListener(onClickTxtBackListDevice);
        lvListDevice.setOnItemClickListener(onClickLvListDevice);
        imgAddPreset.setOnClickListener(onClickImgAddPreset);
        imgOkAddPreset.setOnClickListener(onClickImgOkAddPreset);
        imgDeleteAddPreset.setOnClickListener(onClickImgDeleteAddPreset);
        imgCancelAddPreset.setOnClickListener(onClickImgCancelAddPreset);
        lvListDataPreset.setOnItemClickListener(onClickLvListDataPreset);

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

        rlBackgroundFragmentAddPreset = findViewById(R.id.rlBackgroundFragmentAddPreset);

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

    public void loadDataBegin(){
        listDataPreset = new ArrayList<>();
        DataPresetAdapter adapter = new DataPresetAdapter(this, R.layout.list_view_data_preset, listDataPreset);
        lvListDataPreset.setAdapter(adapter);
        JSONArray jsonArrayData = new JSONArray();
        for(int i = 0; i < 3; i++){
            JSONObject jsonObjectData = new JSONObject();
            JSONArray jsonArrayAngle = new JSONArray();
            try {
                jsonObjectData.put("1", "test-" + i);
                for(int j = 0; j < 20; j++){
                    jsonArrayAngle.put(190+j+i);
                }
                jsonObjectData.put("2",jsonArrayAngle);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArrayData.put(jsonObjectData);
        }
        Log.i("jsonArrayData", jsonArrayData.toString());
        dataJsonSetupBegin = jsonArrayData.toString();

        //-------------------
        try {
            JSONArray jsonArrayDataReceive = new JSONArray(dataJsonSetupBegin);
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
            lvListDataPreset.setEnabled(false);
            edtSetNamePreset.getText().clear();
            for(int i = 0; i < 20; i++){
                edtAngleServo[i].getText().clear();
            }
            currentSelectedPreset = listDataPreset.size() + 1;
        }
    };

    View.OnClickListener onClickImgOkAddPreset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(edtSetNamePreset.getText().toString().equals("")){
                Toast.makeText(MainActivity.this, "Enter Name!", Toast.LENGTH_SHORT).show();
                return;
            }
            rlBackgroundFragmentAddPreset.setVisibility(View.INVISIBLE);
            lvListDataPreset.setEnabled(true);

            JSONArray jsonArrayData = null;
            try {
                JSONObject jsonObjectData = new JSONObject();
                JSONArray jsonArrayAngle = new JSONArray();
                jsonArrayData = new JSONArray(dataJsonSetupBegin);
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
                if(currentSelectedPreset > listDataPreset.size()){
                    jsonArrayData.put(jsonObjectData);
                }
                else{
                    jsonArrayData.put(currentSelectedPreset, jsonObjectData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("jsonArrayData", jsonArrayData.toString());
            dataJsonSetupBegin = jsonArrayData.toString();

            //-------------------
            try {
                JSONArray jsonArrayDataReceive = new JSONArray(dataJsonSetupBegin);
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
        }
    };

    View.OnClickListener onClickImgDeleteAddPreset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddPreset.setVisibility(View.INVISIBLE);
            lvListDataPreset.setEnabled(true);
            try {
                JSONArray jsonArrayData = new JSONArray(dataJsonSetupBegin);
                jsonArrayData.remove(currentSelectedPreset);
                dataJsonSetupBegin = jsonArrayData.toString();
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
        }
    };

    View.OnClickListener onClickImgCancelAddPreset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            rlBackgroundFragmentAddPreset.setVisibility(View.INVISIBLE);
            lvListDataPreset.setEnabled(true);
        }
    };

    AdapterView.OnItemClickListener onClickLvListDataPreset = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            rlBackgroundFragmentAddPreset.setVisibility(View.VISIBLE);
            imgDeleteAddPreset.setVisibility(View.VISIBLE);
            lvListDataPreset.setEnabled(false);
            DataPreset dataPreset = listDataPreset.get(i);
            int[] angle = dataPreset.getAngle();
            for(int j = 0; j < angle.length; j++){
                edtSetNamePreset.setText(dataPreset.getName());
                edtAngleServo[j].setText(String.valueOf(angle[j]));
            }
            currentSelectedPreset = i;
        }
    };

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