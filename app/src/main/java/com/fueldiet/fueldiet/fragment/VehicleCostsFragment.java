package com.fueldiet.fueldiet.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fueldiet.fueldiet.R;
import com.fueldiet.fueldiet.activity.AddNewCostActivity;
import com.fueldiet.fueldiet.activity.EditCostActivity;
import com.fueldiet.fueldiet.adapter.CostAdapter;
import com.fueldiet.fueldiet.db.FuelDietDBHelper;
import com.fueldiet.fueldiet.object.CostObject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class VehicleCostsFragment extends Fragment {

    private long id_vehicle;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    CostAdapter mAdapter;
    FuelDietDBHelper dbHelper;
    View view;
    FloatingActionButton fab;

    private List<CostObject> data;

    private long costID;
    private int pos;

    public VehicleCostsFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        updateData();
        mAdapter.notifyDataSetChanged();
    }

    public static VehicleCostsFragment newInstance(long id) {
        VehicleCostsFragment fragment = new VehicleCostsFragment();
        Bundle args = new Bundle();
        args.putLong("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_vehicle = getArguments().getLong("id");
        }
        dbHelper = FuelDietDBHelper.getInstance(getContext());
        data = new ArrayList<>();
    }

    private void updateData() {
        data.clear();
        data.addAll(dbHelper.getAllCosts(id_vehicle));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_vehicle_costs, container, false);
        mRecyclerView = view.findViewById(R.id.vehicle_costs_recycler_view);
        mLayoutManager= new LinearLayoutManager(getActivity());
        updateData();
        mAdapter = new CostAdapter(getActivity(), data);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        fab = view.findViewById(R.id.vehicle_costs_add_new);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddNewCostActivity.class);
            intent.putExtra("vehicle_id", id_vehicle);
            startActivity(intent);
        });

        mAdapter.setOnItemClickListener(new CostAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position, long element_id) {
                costID = element_id;
                pos = position;
                editItem();
            }

            @Override
            public void onDeleteClick(int position, long element_id) {
                costID = element_id;
                pos = position;
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setMessage(getString(R.string.are_you_sure)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.no), dialogClickListener).show();
            }
        });
        return view;
    }

    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                removeItem();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                Toast.makeText(getContext(), getString(R.string.canceled), Toast.LENGTH_SHORT).show();
                break;
        }
    };

    private void removeItem() {
        CostObject deleted = dbHelper.getCost(costID);
        dbHelper.removeCost(costID);
        updateData();
        mAdapter.notifyItemRemoved(pos);

        Snackbar snackbar = Snackbar.make(getView(), getString(R.string.object_deleted), Snackbar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar sb) { }

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) { }
        }).setAction("UNDO", v -> {
            dbHelper.addCost(deleted);
            updateData();
            mAdapter.notifyItemInserted(pos);
            mRecyclerView.scrollToPosition(0);
            Toast.makeText(getContext(), getString(R.string.undo_pressed), Toast.LENGTH_SHORT).show();
        });
        snackbar.show();
    }

    private void editItem() {
        Intent intent = new Intent(getContext(), EditCostActivity.class);
        intent.putExtra("cost_id", costID);
        startActivity(intent);
    }
}
