package com.example.alek.shoppinglist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Damian on 2017-09-21.
 */

public class PreviousItemViewHolder extends RecyclerView.ViewHolder {

    CheckBox itemState;
    TextView itemName;
    ImageView itemAction;
    ImageView itemDelete;

    public PreviousItemViewHolder(View itemView, CheckBox itemState, TextView itemName, ImageView itemAction, ImageView itemDelete) {
        super(itemView);
        this.itemState = itemState;
        this.itemName = itemName;
        this.itemAction = itemAction;
        this.itemDelete = itemDelete;
    }

}
