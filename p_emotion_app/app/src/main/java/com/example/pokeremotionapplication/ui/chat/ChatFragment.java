package com.example.pokeremotionapplication.ui.chat;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.pokeremotionapplication.R;
import com.example.pokeremotionapplication.data.dataBase.ChatDataBaseHelper;
import com.example.pokeremotionapplication.data.pojo.Message;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    BottomNavigationView bottomNavigationView;
    boolean isHorn = false;
    private ChatDataBaseHelper dbHelper;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private EditText inputMessage;
    private ImageButton sendButton;
    private List<Message> messageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        dbHelper = new ChatDataBaseHelper(getContext());
        recyclerView = view.findViewById(R.id.chat_recycler);
        inputMessage = view.findViewById(R.id.chat_input);
        sendButton = view.findViewById(R.id.chat_send);
        messageList = new ArrayList<>();

        // 初始化 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        // 添加间隔
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing); // 从 dimens.xml 中获取间隔值
        recyclerView.addItemDecoration(new ItemSpacingDecoration(spacingInPixels));

        // 加载初始消息
        loadMessages();

        // 发送消息
        sendButton.setOnClickListener(v -> {
            String message = inputMessage.getText().toString();
            if (!message.isEmpty()) {
                dbHelper.insertMessage("User", "123456", message);
                inputMessage.setText("");
                loadMessages();
            }
        });

        return view;
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

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getAllMessage();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String userName = cursor.getString(cursor.getColumnIndex(ChatDataBaseHelper.COLUMN_USER_NAME));
                @SuppressLint("Range") String userNumber = cursor.getString(cursor.getColumnIndex(ChatDataBaseHelper.COLUMN_USER_NUMBER));
                @SuppressLint("Range") String message = cursor.getString(cursor.getColumnIndex(ChatDataBaseHelper.COLUMN_MESSAGE));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(ChatDataBaseHelper.COLUMN_TIMESTAMP));

                // 假设所有消息都是短文本且由当前用户发送
                boolean isSent = userNumber.equals("123456"); // 假设当前用户的编号是 "123456"
                boolean isLong = message.length() > 50; // 假设消息长度超过50个字符为长文本

                messageList.add(new Message(userName, userNumber, message, timestamp, isSent, isLong));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        messageAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageList.size() - 1);
    }
}