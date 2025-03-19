package com.example.pokeremotionapplication.ui.chat;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;

    public ItemSpacingDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // 为每个 item 设置间隔
        outRect.bottom = spacing;

        // 为第一个 item 设置顶部间隔
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = spacing;
        }
    }
}