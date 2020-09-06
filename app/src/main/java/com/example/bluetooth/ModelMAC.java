package com.example.bluetooth;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


// MODEL. содержит методы для загрузки данных о сохраненных МАС адресах, и добавлении МАС адреса. Для простоты использую SharedPreferences
public class ModelMAC
{
    public static List<String> getArrayMAC(Context context)
    {
        SharedPreferences sharedPreferencesMAC = context.getSharedPreferences("sharedPreferencesMAC", MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) sharedPreferencesMAC.getAll();
        Set<String> set = map.keySet();
        return new ArrayList<>(set);
    }

    public static void saveMAC(String s, Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferencesMAC", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(s, "");
        editor.apply();
    }
}
