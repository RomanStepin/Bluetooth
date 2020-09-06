package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PresenterSearchActivity
{
    BluetoothAdapter bluetoothAdapter;
    // да, коды одинаковые, потомучто один на включение блютуза, другой на запрос разрешения, разные кэллбэки
    public final static int BT_ENABLE_REQUEST_CODE = 1;
    public final static int LOCATION_PERMISSION_REQUEST_CODE = 1;
    // Фиксируем флаг, включен ли блютуз
    boolean bluetoothEnabled;

    IView view;
    Context context;
    BroadcastReceiver receiverAdapterState;
    BroadcastReceiver receiverDeviceFound;
    ArrayList<String> arrayListDevices;

    IntentFilter intentFilterDeviceFound;
    IntentFilter intentFilterAdapterState;



    public PresenterSearchActivity(IView view, Context context)
    {
        this.view = view;
        this.context = context;
        bluetoothEnabled = false;
        arrayListDevices = new ArrayList<>();

        // фильтры для ресиверов, содержащие экшны, которые те должны принимать (нахождение нового устройства и изменение состояния блютуза)
        intentFilterDeviceFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilterAdapterState = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        // вернет null, если блютуз не поддерживается
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    // Если блютуз не поддерживается - говорим VIEW - закрыться, если отключен - сделать запрос на включение, если включен - запросить разрешение локации (без этого разрешения не ищутся устройства)
    public void checkBluetooth()
    {
        if (bluetoothAdapter == null) {
            view.closeAndToast("Bluetooth на вашем устройстве не поддерживается");
        } else {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothEnabled = true;
                view.requestLocation();
            }
            else {
                view.requestBluetooth();
            }
        }
    }

    // начинаем поиск устройств. при нахождении устройства будет сообщено ресиверам, ловящим BluetoothDevice.ACTION_FOUND
    public void bluetoothStartDiscovery()
    {
        bluetoothAdapter.startDiscovery();
    }


    //  ресиверы, которые будут отлавливать включение/отключение bluetooth и нахождение новых устройств для спаривания
    void registerReceives()
    {
        receiverAdapterState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_ON)
                {
                        bluetoothEnabled = true;
                        if (view.checkLocationPermission())
                            bluetoothAdapter.startDiscovery();
                }
                else
                    bluetoothEnabled = false;
            }
        };

        receiverDeviceFound = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                Log.d("LOG", device.getAddress());
                if(!arrayListDevices.contains(device.getAddress())) {
                    arrayListDevices = new ArrayList<>(arrayListDevices);

                    if (device.getName() != null)
                        arrayListDevices.add(device.getAddress() + " " + device.getName());
                    else
                        arrayListDevices.add(device.getAddress());

                }

                view.updateData(arrayListDevices);

            }
        };
        context.registerReceiver(receiverDeviceFound, intentFilterDeviceFound);
        context.registerReceiver(receiverAdapterState, intentFilterAdapterState);
    }

    // закрываем ресиверы и заканчиваем поиск устройств
    void unregisterReceivers()
    {
        bluetoothAdapter.cancelDiscovery();
        context.unregisterReceiver(receiverAdapterState);
        context.unregisterReceiver(receiverDeviceFound);
    }

    public interface IView
    {
        public void updateData(List<String> arrayListMAC);
        public void closeAndToast(String text);
        public void requestLocation();
        public void requestBluetooth();
        public Boolean checkLocationPermission();
    }
}
