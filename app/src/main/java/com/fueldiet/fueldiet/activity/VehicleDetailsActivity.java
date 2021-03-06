package com.fueldiet.fueldiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.fueldiet.fueldiet.R;
import com.fueldiet.fueldiet.db.FuelDietDBHelper;
import com.fueldiet.fueldiet.fragment.VehicleConsumptionFragment;
import com.fueldiet.fueldiet.fragment.VehicleCostsFragment;
import com.fueldiet.fueldiet.fragment.VehicleReminderFragment;
import com.fueldiet.fueldiet.object.VehicleObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VehicleDetailsActivity extends BaseActivity {

    private static final String TAG = "VehicleDetailsActivity";

    public long vehicle_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        Intent intent = getIntent();
        int maybeReminderID = intent.getIntExtra("reminder_id", -2);
        if (maybeReminderID != -2) {
            NotificationManagerCompat.from(getApplicationContext()).cancel(maybeReminderID);
        }
        vehicle_id = intent.getLongExtra("vehicle_id", 1);
        setTitle();
        int frag = intent.getIntExtra("frag", -1);
        Log.d(TAG, "onCreate: frag " + frag);

        BottomNavigationView bottomNav = findViewById(R.id.vehicle_details_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFrag;

            switch (item.getItemId()) {
                case R.id.vehicle_details_consumption:
                    selectedFrag = VehicleConsumptionFragment.newInstance(vehicle_id);
                    break;
                case R.id.vehicle_details_costs:
                    selectedFrag = VehicleCostsFragment.newInstance(vehicle_id);
                    break;
                default:
                    //is reminders
                    selectedFrag = VehicleReminderFragment.newInstance(vehicle_id);
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.view_pager, selectedFrag).commit();
            return true;
        });

        switch (frag) {
            case -1:
            case 0:
                bottomNav.setSelectedItemId(R.id.vehicle_details_consumption);
                break;
            case 1:
                bottomNav.setSelectedItemId(R.id.vehicle_details_costs);
                break;
            default:
                bottomNav.setSelectedItemId(R.id.vehicle_details_reminders);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setTitle() {
        FuelDietDBHelper dbHelper = FuelDietDBHelper.getInstance(this);
        VehicleObject vo = dbHelper.getVehicle(vehicle_id);
        getSupportActionBar().setTitle(String.format("%s %s", vo.getMake(), vo.getModel()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vehicle_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.vehicle_details_chart_img) {
            Intent intent1 = new Intent(VehicleDetailsActivity.this, ChartsActivity.class);
            intent1.putExtra("vehicle_id", vehicle_id);
            startActivity(intent1);
        } else if (item.getItemId() == R.id.vehicle_details_info_img) {
            Intent intent1 = new Intent(VehicleDetailsActivity.this, VehicleInfoActivity.class);
            intent1.putExtra("vehicle_id", vehicle_id);
            startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }
}