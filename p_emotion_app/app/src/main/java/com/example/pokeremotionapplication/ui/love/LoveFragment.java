package com.example.pokeremotionapplication.ui.love;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pokeremotionapplication.R;

import java.util.Calendar;

public class LoveFragment extends Fragment {

    private TextView tvStartTime;
    private TextView tvEndTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_love, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTime(view);
    }

    // 起始时间
    private void setTime(View view){
       tvStartTime = view.findViewById(R.id.love_start_text);
       tvEndTime = view.findViewById(R.id.love_end_text);

        Button startButton = view.findViewById(R.id.love_start_button);
        Button endButton = view.findViewById(R.id.love_end_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTimePicker();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndTimePicker();
            }
        });
    }

    // 具体时间选择器
    private void showStartTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.CustomDateTimePickerDialogTheme , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                showTimePicker(year, monthOfYear, dayOfMonth, true);
            }
        }, year, month, dayOfMonth);

        dpd.show();
    }

    private void showEndTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.CustomDateTimePickerDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                showTimePicker(year, monthOfYear, dayOfMonth, false);
            }
        }, year, month, dayOfMonth);

        dpd.show();
    }

    private void showTimePicker(int year, int month, int dayOfMonth, boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        TimePickerDialog tpd = new TimePickerDialog(requireContext(),R.style.CustomDateTimePickerDialogTheme ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (isStart) {
                    tvStartTime.setText(String.format("%02d年%02d月%02d日 %02d:%02d:%02d", year, month + 1, dayOfMonth, hourOfDay, minute, second));
                    // 这里可以添加起始时间选择后的逻辑
                } else {
                    tvEndTime.setText(String.format("%02d年%02d月%02d日 %02d:%02d:%02d", year, month + 1, dayOfMonth, hourOfDay, minute, second));
                    // 这里可以添加结束时间选择后的逻辑
                }
            }
        }, hour, minute, true); // 传递时间以及是否为24小时制
        tpd.show();
    }
}