package com.example.alek.shoppinglist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Damian on 2017-09-20.
 */

public class ActiveItemViewHolder extends RecyclerView.ViewHolder {

    CheckBox itemState;
    TextView itemName;
    TextView itemQuantity;
    ImageView itemAction;
    ImageView itemDelete;

    public ActiveItemViewHolder(View itemView, CheckBox itemState, TextView itemName, TextView itemQuantity, ImageView itemAction, ImageView itemDelete) {
        super(itemView);
        this.itemState = itemState;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemAction = itemAction;
        this.itemDelete = itemDelete;
    }

}
