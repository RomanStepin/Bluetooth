package com.example.bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


// SearchActivity в качестве VIEW в модели MVP реализует интерфейс IView, определенный в PresenterSearchActivity
public class SearchActivity extends AppCompatActivity implements View.OnClickListener, PresenterSearchActivity.IView {

    //кнопка для возврата, recyclerView, RecyclerView.Adapter для recyclerView и ПРЕЗЕНТЕР
    Button buttonForMain;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    PresenterSearchActivity presenterSearchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Инициализация
        buttonForMain = findViewById(R.id.buttonForMain);
        buttonForMain.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        presenterSearchActivity = new PresenterSearchActivity(this, this);


        // в начале просим ПРЕЗЕНТЕР зарегистрировать ресиверы
        presenterSearchActivity.registerReceives();

        // если активити создается впервые
        if (savedInstanceState == null)
        {
            // просим презентер проверить состояние bluetooth на устройстве
            presenterSearchActivity.checkBluetooth();
        }

        // если активити пересоздается после поворота экрана, то данные для recyclerView и флаг включенности блютуза восстановятся в состояние "до поворота"
        if ((savedInstanceState != null))
        {
            if (savedInstanceState.getStringArrayList("arrayListMAC") != null) {
                ArrayList<String> arrayListMAC = savedInstanceState.getStringArrayList("arrayListMAC");
                myRecyclerViewAdapter = new MyRecyclerViewAdapter(arrayListMAC);
                recyclerView.setAdapter(myRecyclerViewAdapter);
                presenterSearchActivity.arrayListDevices = arrayListMAC;
            }
            presenterSearchActivity.bluetoothEnabled = savedInstanceState.getBoolean("isBluetoothEnabled", false);
            requestLocation();
        }
    }


    // этот кэллбэк срабатывает при уничтожении активити при повороте. В нем сохраняем данные для recyclerView и флаг включенности блютуза
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isBluetoothEnabled", presenterSearchActivity.bluetoothEnabled);
        if (myRecyclerViewAdapter != null)
        outState.putStringArrayList("arrayListMAC", myRecyclerViewAdapter.getData());
    }


    // закрытие. Приведет нас в метод onActivityResult в MainActivity
    @Override
    public void onClick(View v) {
        finish();
    }

    // при уничтожении активити просим ПРЕЗЕНТЕР прекратить поиск устройств и выключить ресиверы
    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenterSearchActivity.unregisterReceivers();
    }


    // вызов окошка, позволяющего включить блютуз
    @Override
    public void requestBluetooth() {
        Intent intentForEnableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intentForEnableBT, PresenterSearchActivity.BT_ENABLE_REQUEST_CODE);
    }

    /*
    кэллбэк, срабатывающий после выбора в окошке, позволяющем включить блютуз.
    /Проверяем, что это действительно был запрос на включение блютуза, если отказались - закрываемся, если согласились - проверяем разрешение на определение локации
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case PresenterSearchActivity.BT_ENABLE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_CANCELED)
                {
                    Toast.makeText(this, "Для поиска устройств Bluetooth должен быть включен", Toast.LENGTH_SHORT).show();
                    finish();
                }
                if (resultCode == Activity.RESULT_OK)
                    requestLocation();
        }
    }

    /*
        Проверяем, включено ли разрешение на определение локации.
        Если разрешение есть - проверяем, включился ли уже блютуз, и если да - просим ПРЕЗЕНТЕР начать поиск устройств
        Если разрешения нет - запрашиаем его
    */
    @Override
    public void requestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},  PresenterSearchActivity.LOCATION_PERMISSION_REQUEST_CODE);
        } else if (presenterSearchActivity.bluetoothEnabled)
        {
            presenterSearchActivity.bluetoothStartDiscovery();
        }
    }

    // кэллбэк, срабатывающий после ответа пользователя на запрос разрешения на локацию
    // если разрешения не было дано - закрываем активити
    // Если разрешение было дано - проверяем, включился ли уже блютуз, и если да - просим ПРЕЗЕНТЕР начать поиск устройств
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PresenterSearchActivity.LOCATION_PERMISSION_REQUEST_CODE) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Для поиска устройств Bluetooth должно быть разрешение на определение местоположения", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if (presenterSearchActivity.bluetoothEnabled)
            {
                presenterSearchActivity.bluetoothStartDiscovery();
            }
        }
    }

    //Проверка, есть ли уже разрешение на локацию. Используем в презентере при включении блютуза, если он включится раньше.
    @Override
    public Boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // обновляем данные в RecyclerView. Если адаптер еще не был создан - создаем и вносим данные
    // если уже был создан - обновляем данные.
    // Здесь использую DiffUtilCallback. Механизм позволяет не пересоздавать все item-ы, при изменении данных а только те, которые изменились и добавлять новые
    @Override
    public void updateData(List<String> arrayListMAC) {
        if (myRecyclerViewAdapter == null) {
            myRecyclerViewAdapter = new MyRecyclerViewAdapter((ArrayList<String>) arrayListMAC);
            recyclerView.setAdapter(myRecyclerViewAdapter);
        }
        else {
            MyDiffUtilCallback productDiffUtilCallback = new MyDiffUtilCallback(myRecyclerViewAdapter.getData(), arrayListMAC);
            DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback);
            myRecyclerViewAdapter.setData((ArrayList<String>) arrayListMAC);
            productDiffResult.dispatchUpdatesTo(myRecyclerViewAdapter);
        }
    }

    // закрытие + сообщение
    @Override
    public void closeAndToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        finish();
    }
}






