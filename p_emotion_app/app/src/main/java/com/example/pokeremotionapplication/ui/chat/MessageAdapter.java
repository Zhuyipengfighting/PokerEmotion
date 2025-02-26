package com.example.pokeremotionapplication.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokeremotionapplication.R;
import com.example.pokeremotionapplication.data.pojo.Message;

import java.util.List;

// 处理聊天消息
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 聊天的文本数据
    private List<Message> messageList;

    // 消息类型
    private static final int TYPE_SHORT_TEXT_LEFT = 1;
    private static final int TYPE_SHORT_TEXT_RIGHT = 2;
    private static final int TYPE_LONG_TEXT_LEFT = 3;
    private static final int TYPE_LONG_TEXT_RIGHT = 4;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    // 获取消息类型
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        boolean isSent = message.isSent();
        boolean isLong = message.isLong();

        if (isLong) {
            return isSent ? TYPE_LONG_TEXT_RIGHT : TYPE_LONG_TEXT_LEFT;
        } else {
            return isSent ? TYPE_SHORT_TEXT_RIGHT : TYPE_SHORT_TEXT_LEFT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType) {
            case TYPE_SHORT_TEXT_LEFT:
                view = inflater.inflate(R.layout.chat_short_text_left_item, parent, false);
                return new ShortTextLeftViewHolder(view);
            case TYPE_SHORT_TEXT_RIGHT:
                view = inflater.inflate(R.layout.chat_short_text_right_item, parent, false);
                return new ShortTextRightViewHolder(view);
            case TYPE_LONG_TEXT_LEFT:
                view = inflater.inflate(R.layout.chat_long_text_left_item, parent, false);
                return new LongTextLeftViewHolder(view);
            case TYPE_LONG_TEXT_RIGHT:
                view = inflater.inflate(R.layout.chat_long_text_right_item, parent, false);
                return new LongTextRightViewHolder(view);
            default:
                throw new IllegalArgumentException("不知道的消息类型");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_SHORT_TEXT_LEFT:
                ((ShortTextLeftViewHolder) holder).bind(message);
                break;
            case TYPE_SHORT_TEXT_RIGHT:
                ((ShortTextRightViewHolder) holder).bind(message);
                break;
            case TYPE_LONG_TEXT_LEFT:
                ((LongTextLeftViewHolder) holder).bind(message);
                break;
            case TYPE_LONG_TEXT_RIGHT:
                ((LongTextRightViewHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // 非发送者的短文本
    static class ShortTextLeftViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        // TextView userNameText;
        TextView timestampText;

        ShortTextLeftViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.chat_text);
            // userNameText = itemView.findViewById(R.id.user_name);
            timestampText = itemView.findViewById(R.id.chat_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            // userNameText.setText(message.getUserName());
            timestampText.setText(message.getTimestamp());
        }
    }

    // 发送者的短文本
    static class ShortTextRightViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        // TextView userNameText;
        TextView timestampText;

        ShortTextRightViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.chat_text);
            // userNameText = itemView.findViewById(R.id.user_name);
            timestampText = itemView.findViewById(R.id.chat_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            // userNameText.setText(message.getUserName());
            timestampText.setText(message.getTimestamp());
        }
    }

    // 非发送者的长文本
    static class LongTextLeftViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        // TextView userNameText;
        TextView timestampText;

        LongTextLeftViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.chat_text);
            // userNameText = itemView.findViewById(R.id.user_name);
            timestampText = itemView.findViewById(R.id.chat_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            // userNameText.setText(message.getUserName());
            timestampText.setText(message.getTimestamp());
        }
    }

    // 发送者的长文本
    static class LongTextRightViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        // TextView userNameText;
        TextView timestampText;

        LongTextRightViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.chat_text);
            // userNameText = itemView.findViewById(R.id.user_name);
            timestampText = itemView.findViewById(R.id.chat_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            // userNameText.setText(message.getUserName());
            timestampText.setText(message.getTimestamp());
        }
    }
}
