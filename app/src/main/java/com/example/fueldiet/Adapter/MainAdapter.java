package com.example.fueldiet.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fueldiet.Fragment.MainFragment;
import com.example.fueldiet.Object.CostObject;
import com.example.fueldiet.Object.DriveObject;
import com.example.fueldiet.Object.VehicleObject;
import com.example.fueldiet.R;
import com.example.fueldiet.Utils;
import com.example.fueldiet.db.FuelDietDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mListener;

    private Context mContext;
    private List<Object> objectsList;
    private FuelDietDBHelper dbHelper;
    private final int TYPE_SPINNER = 0;
    private final int TYPE_TITLE = 1;
    private final int TYPE_DATA_FUEL = 2;
    private final int TYPE_DATA_COST = 3;
    private final int TYPE_DATA_ENTRY = 4;

    public MainAdapter(Context context, List<Object> vehicles, FuelDietDBHelper dbHelper) {
        mContext = context;
        objectsList = vehicles;
        this.dbHelper = dbHelper;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemSelected(long vehicleID);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /* checks which type it is */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SPINNER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_spinner, parent, false);
            return new SpinnerViewHolder(v, mListener);
        } else if (viewType == TYPE_TITLE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_type_title, parent, false);
            return new TitleViewHolder(v, mListener);
        } else if (viewType == TYPE_DATA_FUEL){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_type_data_fuel, parent, false);
            return new DataViewHolder(v, mListener, viewType);
        } else if (viewType == TYPE_DATA_COST){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_type_data_cost, parent, false);
            return new DataViewHolder(v, mListener, viewType);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_type_data_entry, parent, false);
            return new DataViewHolder(v, mListener, viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (objectsList.get(position) instanceof SpinnerAdapter)
            return TYPE_SPINNER;
        else if (objectsList.get(position) instanceof MainFragment.TitleType)
            return TYPE_TITLE;
        else if (position == 2)
            return TYPE_DATA_FUEL;
        else if (position == 4)
            return TYPE_DATA_COST;
        else
            return TYPE_DATA_ENTRY;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SPINNER) {
            ((SpinnerViewHolder) holder).setUp(objectsList.get(position));
        } else if (getItemViewType(position) == TYPE_TITLE) {
            ((TitleViewHolder) holder).setUp(objectsList.get(position));
        } else if (getItemViewType(position) == TYPE_DATA_FUEL) {
            ((DataViewHolder) holder).setUpFuel(objectsList.get(position));
        } else if (getItemViewType(position) == TYPE_DATA_COST) {
            ((DataViewHolder) holder).setUpCost(objectsList.get(position));
        } else if (getItemViewType(position) == TYPE_DATA_ENTRY) {
            ((DataViewHolder) holder).setUpEntry(objectsList.get(position));
        }
    }

    class SpinnerViewHolder extends RecyclerView.ViewHolder {
        Spinner spinner;

        SpinnerViewHolder(final View itemView, final OnItemClickListener listener) {
            super(itemView);
            spinner = itemView.findViewById(R.id.vehicle_select);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    VehicleObject clickedItem = (VehicleObject) parent.getItemAtPosition(position);
                    mListener.onItemSelected(clickedItem.getId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        void setUp(Object object) {
            //VehicleSelectAdapter adapter = (VehicleSelectAdapter) object;
            //spinner.setAdapter(adapter);

            ArrayList<VehicleObject> vehicles = new ArrayList<>();
            List<VehicleObject> vehicleObjects = dbHelper.getAllVehicles();

            if (vehicleObjects == null || vehicleObjects.size() == 0) {
                vehicles.add(new VehicleObject("No vehicle", "added!", -1));
                VehicleSelectAdapter spinnerAdapter = new VehicleSelectAdapter(mContext, vehicles);
                spinner.setAdapter(spinnerAdapter);
            } else {
                vehicles.addAll(vehicleObjects);
                VehicleSelectAdapter spinnerAdapter = new VehicleSelectAdapter(mContext, vehicles);
                spinner.setAdapter(spinnerAdapter);

                long vehicleID = (long)objectsList.get(2);
                int pos = 0;
                if (vehicleID != -1) {
                    for (int i = 0; i < spinnerAdapter.list.size(); i++) {
                        if (spinnerAdapter.list.get(i).getId() == vehicleID) {
                            pos = i;
                            break;
                        }
                    }
                    spinner.setSelection(pos);
                }
            }


            //VehicleSelectAdapter spinnerAdapter = new VehicleSelectAdapter(mContext, vehicles);
            //spinner.setAdapter(spinnerAdapter);
        }
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        ImageView logo;
        TextView title;

        TitleViewHolder(final View itemView, final OnItemClickListener listener) {
            super(itemView);
            logo = itemView.findViewById(R.id.type_image);
            title = itemView.findViewById(R.id.type_title);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        void setUp(Object object) {
            MainFragment.TitleType type = (MainFragment.TitleType) object;
            String typeTitle = type.name();
            title.setText(typeTitle.replace("_", " "));

            switch (type) {
                case Cost:
                    logo.setImageResource(R.drawable.ic_euro_symbol_black_24dp);
                    break;
                case Fuel:
                    logo.setImageResource(R.drawable.ic_local_gas_station_black_24dp);
                    break;
                case Last_Entries:
                    logo.setImageResource(R.drawable.ic_timeline_black_24dp);
                    break;
                default:
                    logo.setImageResource(R.drawable.ic_help_outline_black_24dp);
            }
        }
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        int which;
        TextView avgCons, rcntCons, rcntPrice, date, fuelCost, otherCost, prevFuelCost, prevOtherCost;

        DataViewHolder(final View itemView, final OnItemClickListener listener, int which) {
            super(itemView);
            this.which = which;

            if (which == 2) {
                //fuel
                avgCons = itemView.findViewById(R.id.avg_fuel_cons_value);
                rcntCons = itemView.findViewById(R.id.rcnt_fuel_cons_value);
                rcntPrice = itemView.findViewById(R.id.rcnt_fuel_price_value);
                date = itemView.findViewById(R.id.date);
            } else if (which == 3) {
                //cost
                fuelCost = itemView.findViewById(R.id.fuel_cost_value);
                otherCost = itemView.findViewById(R.id.other_costs_value);
                prevFuelCost = itemView.findViewById(R.id.prev_fuel_cost_value);
                prevOtherCost = itemView.findViewById(R.id.prev_other_cost_value);
            } else {
                //entry
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        void setUpFuel(Object object) {
            long vehicleID = (long) object;
            if (vehicleID != -1) {
                DriveObject latest = dbHelper.getLastDrive(vehicleID);
                if (latest == null) {
                    rcntCons.setText("No data yet");
                    rcntPrice.setText("No data yet");
                    avgCons.setText("No data yet");
                    date.setText("");
                    itemView.findViewById(R.id.unit1).setVisibility(View.INVISIBLE);
                    itemView.findViewById(R.id.unit2).setVisibility(View.INVISIBLE);
                    itemView.findViewById(R.id.unit3).setVisibility(View.INVISIBLE);
                    return;
                }
                rcntCons.setText(Utils.calculateConsumption(latest.getTrip(), latest.getLitres())+"");
                SimpleDateFormat format = new SimpleDateFormat("dd. MM. yyyy");
                date.setText(format.format(latest.getDate().getTime()));
                rcntPrice.setText(latest.getCostPerLitre()+"");
                double avg = 0.0;
                int count = 0;
                List<DriveObject> allDrives = dbHelper.getAllDrives(vehicleID);
                for (DriveObject drive : allDrives) {
                    avg += Utils.calculateConsumption(drive.getTrip(), drive.getLitres());
                    count++;
                }
                avg = Math.round(avg / count);
                avgCons.setText(avg + "");
            }
        }

        void setUpCost(Object object) {
            long vehicleID = (long) object;
            Calendar first = Calendar.getInstance();
            Calendar last = Calendar.getInstance();
            if (vehicleID != -1) {
                first.set(Calendar.DAY_OF_MONTH, 1);
                last.set(Calendar.DAY_OF_MONTH, last.getActualMaximum(Calendar.DAY_OF_MONTH));
                List<DriveObject> current = dbHelper.getAllDrivesWhereTimeBetween(vehicleID, first.getTimeInMillis()/1000, last.getTimeInMillis()/1000);
                double price = 0.0;
                for (DriveObject drive : current) {
                    price += Utils.calculateFullPrice(drive.getCostPerLitre(), drive.getLitres());
                }
                fuelCost.setText(price+" €");

                price = 0.0;
                List<CostObject> currentCost = dbHelper.getAllCostsWhereTimeBetween(vehicleID, first.getTimeInMillis()/1000, last.getTimeInMillis()/1000);
                for (CostObject cost : currentCost) {
                    price += cost.getCost();
                }
                otherCost.setText(price+" €");
                price = 00.0;

                first.add(Calendar.MONTH, -1);
                last.set(Calendar.DAY_OF_MONTH, first.getActualMaximum(Calendar.DAY_OF_MONTH));

                current = dbHelper.getAllDrivesWhereTimeBetween(vehicleID, first.getTimeInMillis()/1000, last.getTimeInMillis()/1000);
                price = 0.0;
                for (DriveObject drive : current) {
                    price += Utils.calculateFullPrice(drive.getCostPerLitre(), drive.getLitres());
                }
                prevFuelCost.setText(price+" €");

                price = 0.0;
                currentCost = dbHelper.getAllCostsWhereTimeBetween(vehicleID, first.getTimeInMillis()/1000, last.getTimeInMillis()/1000);
                for (CostObject cost : currentCost) {
                    price += cost.getCost();
                }
                prevOtherCost.setText(price+" €");
            }
        }

        void setUpEntry(Object object) {
            long vehicleID = (long) object;
        }
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mBrand;
        public TextView mData;


        public MainViewHolder(final View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.vehicle_logo_image);
            mBrand = itemView.findViewById(R.id.vehicle_make_model_view);
            mData = itemView.findViewById(R.id.vehicle_desc_view);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        //listener.onItemClick((long)itemView.getTag());
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }


/*
    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        if (position >= getItemCount())
            return;

        VehicleObject vehicle = objectsList.get(position);

        String consUnit = PreferenceManager.getDefaultSharedPreferences(mContext).getString("language_select", "english");
        String benz;
        if (consUnit.equals("english"))
            benz = vehicle.getFuel();
        else
            benz = Utils.fromENGtoSLO(vehicle.getFuel());

        String make = vehicle.getMake();
        String model = vehicle.getModel();

        String data = vehicle.getEngine() + " " + vehicle.getHp() + "hp" + " " + benz;
        long id = vehicle.getId();

        holder.mBrand.setText(String.format("%s %s", make, model));
        holder.mData.setText(data);

        //Loads image file if exists, else predefined image
        try {
            String fileName = vehicle.getCustomImg();
            File storageDIR = mContext.getDir("Images",MODE_PRIVATE);
            if (fileName == null) {
                ManufacturerObject mo = MainActivity.manufacturers.get(toCapitalCaseWords(make));
                if (!mo.isOriginal()){
                    Utils.downloadImage(mContext.getResources(), mContext.getApplicationContext(), mo);
                }
                int idResource = mContext.getResources().getIdentifier(mo.getFileNameModNoType(), "drawable", mContext.getPackageName());
                Glide.with(mContext).load(storageDIR+"/"+mo.getFileNameMod()).error(mContext.getDrawable(idResource)).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.mImageView);
            } else {
                Glide.with(mContext).load(storageDIR+"/"+fileName).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.mImageView);
            }
        } catch (Exception e){
            Bitmap noIcon = Utils.getBitmapFromVectorDrawable(mContext, R.drawable.ic_help_outline_black_24dp);
            Glide.with(mContext).load(noIcon).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mImageView);
        }

        holder.itemView.setTag(id);
    }*/

    @Override
    public int getItemCount() {
        return objectsList.size();
    }

}