package com.example.bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PresenterMainActivity.IView {  // MainActivity в качестве VIEW в модели MVP реализует интерфейс IView, определенный в PresenterMainActivity

    // кнопка и Intent для открытия SearchActivity
    Button buttonForSearch;
    Intent intentForSearch;

    RecyclerView recyclerView;
    MyRecyclerViewAdapterForMain myRecyclerViewAdapterForMain;

    // Константа, отправляемая в качестве request_code, в методе startActivityForResult
    public final int SEARCH_ACTIVITY_START_REQUEST_CODE = 1;

    // ПРЕЗЕНТЕР. Dagger не использую, создаю прямо так
    PresenterMainActivity presenterMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация
        presenterMainActivity = new PresenterMainActivity(this, this);
        buttonForSearch = findViewById(R.id.buttonForSearch);
        buttonForSearch.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Просим у Presenter данные. Флаг SHOW_DATA_REQUEST_CODE, говорит о том, что данные запрашиваются в первый раз после создания MainActivity
        presenterMainActivity.getData(PresenterMainActivity.SHOW_DATA_REQUEST_CODE);
    }

    // Открываем SearchActivity поиска
    @Override
    public void onClick(View v) {
        intentForSearch = new Intent(this, SearchActivity.class);
        startActivityForResult(intentForSearch, SEARCH_ACTIVITY_START_REQUEST_CODE);
    }

    // кэллбэк, вызываемый при возвращении с SearchActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_START_REQUEST_CODE)
        {
            // Просим у Presenter данные. Флаг UPDATE_DATA_REQUEST_CODE, говорит о том, что данные запрашиваются для ОБНОВЛЕНИЯ адаптера
            presenterMainActivity.getData(PresenterMainActivity.UPDATE_DATA_REQUEST_CODE);
        }
    }



    // Методы интерфейса IView. Вызываются из Презентера. Отображение данных на RecyclerView

    //  Первоначальная зугрузка данных в адаптер
    @Override
    public void showData(List<String> arrayListMAC) {
        myRecyclerViewAdapterForMain = new MyRecyclerViewAdapterForMain((ArrayList<String>) arrayListMAC);
        recyclerView.setAdapter(myRecyclerViewAdapterForMain);
    }

    // Обновление данных в адаптере
    @Override
    public void updateData(List<String> arrayListMAC) {
        myRecyclerViewAdapterForMain.setData((ArrayList<String>) arrayListMAC);
        myRecyclerViewAdapterForMain.notifyDataSetChanged();
    }

    @Override
    public void showToast(String text, int flag) {          // Показ текстовых подсказок, при необходимости
        Toast.makeText(this, text, flag).show();
    }
}

