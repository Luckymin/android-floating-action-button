package com.getbase.floatingactionbutton.sample;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * @author luckymin
 * @date 16/3/1.
 */
public class TestAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
//            ...
//            layoutParams.setFullSpan(true);
        } else {

//            ...
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }
}
