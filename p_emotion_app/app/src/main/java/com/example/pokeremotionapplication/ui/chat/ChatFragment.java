package com.example.pokeremotionapplication.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pokeremotionapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatFragment extends Fragment {

    BottomNavigationView bottomNavigationView;
    boolean isHorn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    public void init(View view){
        // 返回键
        ImageView back = view.findViewById(R.id.chat_back_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView = getActivity().findViewById(R.id.Bottom_menu_view);
                bottomNavigationView.setSelectedItemId(R.id.menu_home);
            }
        });

        // 音量键
        ImageView born = view.findViewById(R.id.chat_horn);

        born.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isHorn){
                    born.setImageResource(R.drawable.horn_close);
                    isHorn = true;
                }else {
                    born.setImageResource(R.drawable.horn_open);
                    isHorn = false;
                }
            }
        });
    }
}