package com.example.alek.shoppinglist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ItemActivity extends AppCompatActivity {

    private EditText itemName;
    private EditText itemQuantity;
    private Realm realm;

    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        if(getIntent().hasExtra("TITLE")){
            setTitle(getIntent().getStringExtra("TITLE"));
        }

        itemName = (EditText)findViewById(R.id.input_name);
        itemQuantity =  (EditText)findViewById(R.id.input_quantity);

        realm = Realm.getDefaultInstance();

        if(getIntent().hasExtra("ITEM_NAME")) {
            itemName.setText(getIntent().getStringExtra("ITEM_NAME"));
            itemQuantity.setText(getIntent().getStringExtra("ITEM_QUANTITY"));
            editMode = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.save_action  && !editMode){
            ShoppingItem shoppingItem = new ShoppingItem();
            shoppingItem.setName(itemName.getText().toString());
            shoppingItem.setQuantity(itemQuantity.getText().toString());
            shoppingItem.setState(false);
            shoppingItem.setId(UUID.randomUUID().toString());
            shoppingItem.setTimestamp(System.currentTimeMillis());
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(shoppingItem);
            realm.commitTransaction();

            setResult(RESULT_OK);
            finish();
        }

        if(item.getItemId()==R.id.save_action && editMode) {
            realm.beginTransaction();
            ShoppingItem shoppingItem = realm.where(ShoppingItem.class).equalTo("id", getIntent().getStringExtra("ITEM_ID")).findFirst();
            shoppingItem.setName(itemName.getText().toString());
            shoppingItem.setQuantity(itemQuantity.getText().toString());
            shoppingItem.setTimestamp(System.currentTimeMillis());
            realm.commitTransaction();

            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
