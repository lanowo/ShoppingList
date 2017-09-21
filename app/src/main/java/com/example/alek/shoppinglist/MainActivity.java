package com.example.alek.shoppinglist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private RecyclerView shoppingItems;

    private Realm realm;

    private List<ShoppingItem> dataSet;

    private RecyclerView.Adapter shoppingItemsAdapter = new RecyclerView.Adapter() {

        private final int active_view = 0;
        private final int previous_view = 1;
        private final int classifier_view = 2;


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == active_view) {
                View v = getLayoutInflater().inflate(R.layout.active_item, parent, false);
                return new ActiveItemViewHolder(v,
                        (CheckBox) v.findViewById(R.id.item_state),
                        (TextView) v.findViewById(R.id.item_name),
                        (TextView)v.findViewById(R.id.item_quantity),
                        (ImageView) v.findViewById(R.id.item_action),
                        (ImageView) v.findViewById(R.id.item_delete)
                );
            } else if(viewType == previous_view) {
                View v = getLayoutInflater().inflate(R.layout.previous_item, parent, false);
                return new PreviousItemViewHolder(v,
                        (CheckBox)v.findViewById(R.id.item_state),
                        (TextView)v.findViewById(R.id.item_name),
                        (ImageView)v.findViewById(R.id.item_action),
                        (ImageView) v.findViewById(R.id.item_delete)
                );
            } else {
                View v = getLayoutInflater().inflate(R.layout.classifier, parent, false);
                return new ClassifierViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ShoppingItem currentItem = dataSet.get(position);
            if(currentItem.getTimestamp()==-1) return;
            if(currentItem.isState()) {
                PreviousItemViewHolder h = (PreviousItemViewHolder)holder;
                h.itemName.setText(currentItem.getName());
                h.itemName.setPaintFlags(h.itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                h.itemAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        realm.beginTransaction();
                        currentItem.setState(false);
                        currentItem.setTimestamp(System.currentTimeMillis());
                        realm.commitTransaction();
                        initializeDataSet();
                        shoppingItemsAdapter.notifyDataSetChanged();
                    }
                });
                h.itemDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        realm.beginTransaction();
                        currentItem.deleteFromRealm();
                        realm.commitTransaction();
                        initializeDataSet();
                        shoppingItemsAdapter.notifyDataSetChanged();
                    }
                });
            }else {
                ActiveItemViewHolder h = (ActiveItemViewHolder)holder;
                h.itemName.setText(currentItem.getName());
                h.itemQuantity.setText(currentItem.getQuantity());
                h.itemState.setChecked(false);
                h.itemState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (checked) {
                            realm.beginTransaction();
                            currentItem.setState(true);
                            currentItem.setTimestamp(System.currentTimeMillis());
                            realm.commitTransaction();
                            initializeDataSet();
                            shoppingItemsAdapter.notifyDataSetChanged();
                        }
                    }
                });
                h.itemAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(MainActivity.this, ItemActivity.class);
                        i.putExtra("TITLE", "Edytuj produkt");
                        i.putExtra("ITEM_NAME", currentItem.getName());
                        i.putExtra("ITEM_QUANTITY", currentItem.getQuantity());
                        i.putExtra("ITEM_ID", currentItem.getId());
                        startActivityForResult(i, 1);
                    }
                });
                h.itemDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        realm.beginTransaction();
                        currentItem.deleteFromRealm();
                        realm.commitTransaction();
                        initializeDataSet();
                        shoppingItemsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        @Override
        public int getItemViewType(int position) {
            ShoppingItem currentItem = dataSet.get(position);
            if(currentItem.getTimestamp()==-1) return classifier_view;
            if(currentItem.isState()) return previous_view;
            return active_view;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Realm.init(getApplicationContext());
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
        realm = Realm.getDefaultInstance();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ItemActivity.class);
                i.putExtra("TITLE", "Dodaj produkt");
                startActivityForResult(i, 1);
            }
        });

        shoppingItems = (RecyclerView)findViewById(R.id.shopping_items);
        shoppingItems.setLayoutManager(new LinearLayoutManager(this));

        initializeDataSet();
        shoppingItems.setAdapter(shoppingItemsAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_action) {
            getFragmentManager().beginTransaction().replace(R.id.list, new MyPreferenceFragment()).addToBackStack("settings").commit();
            getFragmentManager().executePendingTransactions();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeDataSet() {
        dataSet = new ArrayList<>();
        RealmResults<ShoppingItem>  activeItemResults = realm.where(ShoppingItem.class).equalTo("state", false).findAllSorted("timestamp", Sort.DESCENDING);
        RealmResults<ShoppingItem> previousItemResults = realm.where(ShoppingItem.class).equalTo("state", true).findAllSorted("timestamp", Sort.DESCENDING);

        ShoppingItem classifier = new ShoppingItem();
        classifier.setTimestamp(-1);

        for(ShoppingItem item:activeItemResults) dataSet.add(item);
        dataSet.add(classifier);
        for(ShoppingItem item:previousItemResults) dataSet.add(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            initializeDataSet();
            shoppingItemsAdapter.notifyDataSetChanged();
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        private Realm realm;
        private static Preference button;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            addPreferencesFromResource(R.xml.preferences);
            button = (Preference)getPreferenceManager().findPreference(getString(R.string.list_reset));
            realm = Realm.getDefaultInstance();
            if (button != null) {
                button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        realm.beginTransaction();
                        realm.deleteAll();
                        realm.commitTransaction();
                        getFragmentManager().popBackStack();
                        return true;
                    }
                });
            }
        }

        @Override
        public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
            menu.clear();
            inflater.inflate(R.menu.menu_settings_frag, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }


        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.setBackgroundColor(Color.WHITE);
        }

        @Override
        public void onStop(){
            super.onStop();
            getActivity().recreate();
        }

    }

}
