package com.example.bluetooth;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class PresenterMainActivity         // Класс презентера в модели MVP
{
    IView view;
    Context context;
    List<String> arrayListMAC;

    public static final int SHOW_DATA_REQUEST_CODE = 1;   // Константы, определяющие, для чего VIEW нужны данные
    public static final int UPDATE_DATA_REQUEST_CODE = 2;

    public PresenterMainActivity(IView view, Context context)
    {
        this.view = view;
        this.context = context;
    }

    public void getData(int request_code)
    {
        arrayListMAC = ModelMAC.getArrayMAC(context);
        switch (request_code)
        {
            case SHOW_DATA_REQUEST_CODE:
                    view.showData(arrayListMAC);
                break;
            case UPDATE_DATA_REQUEST_CODE:
                    view.updateData(arrayListMAC);
                break;
        }
    }

    public interface IView
    {
        public void showData(List<String> arrayListMAC);
        public void updateData(List<String> arrayListMAC);
        public void showToast(String text, int flag);
    }
}
