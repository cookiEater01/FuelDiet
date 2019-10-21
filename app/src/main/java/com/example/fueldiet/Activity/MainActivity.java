package com.example.fueldiet.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fueldiet.db.FuelDietDBHelper;
import com.example.fueldiet.Object.ManufacturerObject;
import com.example.fueldiet.R;
import com.example.fueldiet.Utils;
import com.example.fueldiet.Adapter.VehicleAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {

    public static final String LOGO_URL = "https://raw.githubusercontent.com/filippofilip95/car-logos-dataset/master/images/%s";

    private RecyclerView mRecyclerView;
    private VehicleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    FuelDietDBHelper dbHelper;
    public static Map<String, ManufacturerObject> manufacturers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String response = loadJSONFromAsset();
        List<ManufacturerObject> tmp = new Gson().fromJson(response, new TypeToken<List<ManufacturerObject>>() {}.getType());
        manufacturers = tmp.stream().collect(Collectors.toMap(ManufacturerObject::getName, manufacturerObject -> manufacturerObject));

        dbHelper = new FuelDietDBHelper(this);

        buildRecyclerView();
        //setupSharedPreferences();


        FloatingActionButton fab = findViewById(R.id.add_new);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddNewVehicleActivity.class));
            /*
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
        });
    }



    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.carlogos);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdapter.swapCursor(dbHelper.getAllVehicles());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "TODO: Settings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.reset_db:
                FuelDietDBHelper dbh = new FuelDietDBHelper(getBaseContext());
                Toast.makeText(this, "Reset is done.", Toast.LENGTH_SHORT).show();
                dbh.resetDb();
                mAdapter.swapCursor(dbHelper.getAllVehicles());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.vehicleList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new VehicleAdapter(this, dbHelper.getAllVehicles());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //direction == 4 - delete
                //direction == 8 - edit
                if (direction == 4) {
                    removeItem((long)viewHolder.itemView.getTag(), viewHolder);
                } else if (direction == 8) {
                    editItem((long)viewHolder.itemView.getTag());
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View cardView = viewHolder.itemView;
                    float height = (float) cardView.getBottom() - (float) cardView.getTop();
                    float width = height / 3;
                    Paint p = new Paint();

                    if(dX > 0){
                        p.setColor(Color.BLUE);
                        RectF background = new RectF((float) cardView.getLeft(), (float) cardView.getTop(), cardView.getLeft() + dX,(float) cardView.getBottom());
                        c.drawRect(background,p);
                        //icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_24px);
                        icon = Utils.getBitmapFromVectorDrawable(getBaseContext(), R.drawable.ic_edit_24px);
                        RectF icon_dest = new RectF((float) cardView.getLeft() + width ,(float) cardView.getTop() + width,(float) cardView.getLeft()+ 2*width,(float)cardView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.RED);
                        RectF background = new RectF((float) cardView.getRight() + dX, (float) cardView.getTop(),(float) cardView.getRight(), (float) cardView.getBottom());
                        c.drawRect(background,p);
                        icon = Utils.getBitmapFromVectorDrawable(getBaseContext(), R.drawable.ic_delete_24px);
                        RectF icon_dest = new RectF((float) cardView.getRight() - 2*width ,(float) cardView.getTop() + width,(float) cardView.getRight() - width,(float)cardView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mRecyclerView);



        mAdapter.setOnItemClickListener(element_id -> openItem(element_id));
    }

    private void removeItem(final long id, final RecyclerView.ViewHolder viewHolder) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.clayout), "Vehicle deleted!", Snackbar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
                mAdapter.swapCursor(dbHelper.getAllVehiclesExcept(id));
            }

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    dbHelper.deleteVehicle(id);
                }
            }
        }).setAction("UNDO", v -> {
            mAdapter.swapCursor(dbHelper.getAllVehicles());
            Toast.makeText(MainActivity.this, "Undo pressed", Toast.LENGTH_SHORT).show();
        });
        snackbar.show();
        //mAdapter.swapCursor(dbHelper.getAllVehicles());
    }

    public void editItem(long id) {
        Intent intent = new Intent(MainActivity.this, EditVehicleActivity.class);
        intent.putExtra("vehicle_id", id);
        startActivity(intent);
        //mAdapter.notifyDataSetChanged();
    }

    public void openItem(long id) {
        Toast.makeText(this, "Position: " + id, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, VehicleDetailsActivity.class);
        intent.putExtra("vehicle_id", id);
        startActivity(intent);
        //mAdapter.notifyItemChanged(position);
    }
}