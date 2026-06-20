package com.example.dclassicsbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.dclassicsbooks.data.AppData;
import com.example.dclassicsbooks.model.Store;
import com.google.android.material.navigation.NavigationView;

public class StoresActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton btnMenu;
    private LinearLayout storesList;
    private NavigationView navView;
    private TextView tvGreetingName;

    private final Store[] stores = AppData.getStores();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        bindViews();
        setupHeader();
        setupDrawer();
        renderStores();
    }

    private void bindViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenu = findViewById(R.id.btnMenu);
        storesList = findViewById(R.id.storesList);
        navView = findViewById(R.id.navView);
        tvGreetingName = findViewById(R.id.tvGreetingName);
    }

    private void setupHeader() {
        SharedPreferences sharedPreferences = getSharedPreferences("GlobalVars", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "Book Lover");
        tvGreetingName.setText(username);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupDrawer() {
        View headerView = navView.getHeaderView(0);
        ImageButton closeButton = headerView.findViewById(R.id.btnCloseDrawer);
        closeButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(StoresActivity.this, HomeActivity.class));
                finish();
            } else if (id == R.id.nav_items) {
                startActivity(new Intent(StoresActivity.this, BooksActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                getSharedPreferences("GlobalVars", MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(StoresActivity.this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void renderStores() {
        storesList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Store store : stores) {
            View item = inflater.inflate(R.layout.item_store_card, storesList, false);
            bindStoreCard(item, store);
            storesList.addView(item);
        }
    }

    private void bindStoreCard(View item, Store store) {
        ImageView image = item.findViewById(R.id.imgStore);
        TextView title = item.findViewById(R.id.tvStoreName);
        TextView address = item.findViewById(R.id.tvStoreAddress);
        TextView status = item.findViewById(R.id.tvStoreStatus);
        TextView hours = item.findViewById(R.id.tvStoreHours);

        image.setImageResource(store.imageRes);
        title.setText(store.name);
        address.setText(store.address);
        status.setText(store.status);
        hours.setText(store.weekdayHours + "  |  " + store.weekendHours);
    }
}
