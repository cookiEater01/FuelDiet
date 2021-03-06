package com.fueldiet.fueldiet.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.fueldiet.fueldiet.R;
import com.fueldiet.fueldiet.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class CalculatorFragment extends Fragment {

    private static final String TAG = "CalculatorFragment";

    EditText km;
    EditText litre;
    EditText price;
    EditText litrePrice;
    EditText consumption;

    MaterialButton convert;
    ConsumptionUnits selectedConsumptionUnit;

    Locale locale;
    SharedPreferences pref;

    enum ConsumptionUnits {
        KM_L, L_100_KM, MPG_IMPERIAL, MPG_US;

        static
        public final ConsumptionUnits[] values = values();

        public ConsumptionUnits next() {
            return values[(ordinal() + 1) % values.length];
        }
    }

    TextView consumptionUnit;
    TextView distanceUnit;
    TextView litresUnit;

    /*
    String recent = "";

    TextWatcher forKm;
    TextWatcher forLitre;
    TextWatcher forPrice;
    TextWatcher forLitrePrice;
    TextWatcher forCons;

     */

    public static CalculatorFragment newInstance() {
        return new CalculatorFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        km = view.findViewById(R.id.calc_dist_input);
        litre = view.findViewById(R.id.calc_fuel_input);
        price = view.findViewById(R.id.calc_price_input);
        litrePrice = view.findViewById(R.id.calc_price_l_input);
        consumption = view.findViewById(R.id.calc_cons_input);
        convert = view.findViewById(R.id.calc_button);
        consumptionUnit = view.findViewById(R.id.calc_cons_unit);
        distanceUnit = view.findViewById(R.id.calc_dist_unit);
        litresUnit = view.findViewById(R.id.calc_fuel_unit);

        consumptionUnit.setOnClickListener(v -> toggleConsumptionUnit());
        distanceUnit.setOnClickListener(v -> toggleConsumptionUnit());
        litresUnit.setOnClickListener(v -> toggleConsumptionUnit());

        convert.setOnClickListener(v -> {
            Log.d(TAG, "onClick: button clicked");
            calculate();
        });

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        selectedConsumptionUnit = ConsumptionUnits.valueOf(pref.getString("default_calc_mode", String.valueOf(ConsumptionUnits.L_100_KM)));
        changeDisplayedUnit(true);

        Configuration configuration = getContext().getResources().getConfiguration();
        locale = configuration.getLocales().get(0);

        //createTextWatchers();

        return view;
    }

    private void toggleConsumptionUnit() {
        Log.d(TAG, "toggleConsumptionUnit");
        selectedConsumptionUnit = selectedConsumptionUnit.next();
        changeDisplayedUnit(false);
        Snackbar.make(requireView(), "Unit changed!", Snackbar.LENGTH_SHORT).show();
    }

    private void changeDisplayedUnit(boolean firstRun) {
        Log.d(TAG, "changeDisplayedUnit");

        pref.edit().putString("default_calc_mode", String.valueOf(selectedConsumptionUnit)).apply();

        switch (selectedConsumptionUnit) {
            case KM_L: consumptionUnit.setText(getString(R.string.km_l));
                distanceUnit.setText(getString(R.string.kilometres));
                litresUnit.setText(getString(R.string.litres));
                break;
            case L_100_KM: consumptionUnit.setText(getString(R.string.l_100_km));
                distanceUnit.setText(getString(R.string.kilometres));
                litresUnit.setText(getString(R.string.litres));
                break;
            case MPG_IMPERIAL: consumptionUnit.setText(getString(R.string.mpg_uk));
                distanceUnit.setText(getString(R.string.miles));
                litresUnit.setText(getString(R.string.gallons_uk));
                break;
            case MPG_US: consumptionUnit.setText(getString(R.string.mpg_us));
                distanceUnit.setText(getString(R.string.miles));
                litresUnit.setText(getString(R.string.gallons));
                break;
        }
        if (!firstRun)
            calculate();
    }

    private void calculate() {
        Log.d(TAG, "calculate: started calculating consumption");

        Log.d(TAG, "calculate: hide keyboard");
        //https://stackoverflow.com/questions/3400028/close-virtual-keyboard-on-button-press
        InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        Double calculatedCons = null;
        if (!km.getText().toString().isEmpty() && !litre.getText().toString().isEmpty()) {

            if (Double.parseDouble(km.getText().toString()) <= 0 || Double.parseDouble(litre.getText().toString()) <= 0) {
                Toast.makeText(getContext(), getString(R.string.positive_values_only), Toast.LENGTH_SHORT).show();
            } else {
                switch (selectedConsumptionUnit) {
                    case KM_L:
                    case MPG_US:
                    case MPG_IMPERIAL:
                        calculatedCons = Utils.calculateConsumptionKmPL(Integer.parseInt(km.getText().toString()), Double.parseDouble(litre.getText().toString()));
                        break;
                    case L_100_KM:
                        calculatedCons = Utils.calculateConsumption(Integer.parseInt(km.getText().toString()), Double.parseDouble(litre.getText().toString()));
                        break;
                }
                consumption.setText(String.valueOf(calculatedCons));

                if (!litrePrice.getText().toString().isEmpty() && Double.parseDouble(litrePrice.getText().toString()) > 0.0) {
                    price.setText(String.valueOf(Utils.calculateFullPrice(Double.parseDouble(litrePrice.getText().toString()), Double.parseDouble(litre.getText().toString()))));
                }
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.fill_text_cost), Toast.LENGTH_SHORT).show();
        }
    }
/*
    private void createTextWatchers() {
        forKm = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!litre.getEditText().getText().toString().equals("") && !recent.equals("litre") && s.length() > 0) {
                    Double cons = Utils.calculateConsumption(Integer.parseInt(km.getEditText().getText().toString()), Double.parseDouble(litre.getEditText().getText().toString()));
                    removeTextWatcherConsumption();
                    consumption.getEditText().setText(cons+"");
                    recent = "consumption";
                    addTextWatcherConsumption();
                } else if (!consumption.getEditText().getText().toString().equals("") && !recent.equals("consumption") && s.length() > 0) {
                    BigDecimal cons = new BigDecimal(consumption.getEditText().getText().toString());
                    cons = cons.divide(BigDecimal.valueOf(100));
                    cons = cons.multiply(new BigDecimal(s.toString()));
                    cons = cons.setScale(2, RoundingMode.HALF_UP);
                    removeTextWatcherLitre();
                    litre.getEditText().setText(cons.toString());
                    recent = "litre";
                    addTextWatcherLitre();

                    if (!litrePrice.getEditText().getText().toString().equals("")) {
                        removeTextWatcherPrice();
                        Double fullPrice = Utils.calculateFullPrice(Double.parseDouble(litrePrice.getEditText().getText().toString()), Double.parseDouble(cons.toString()));
                        price.getEditText().setText(fullPrice+"");
                        addTextWatcherPrice();
                    } else if (!price.getEditText().getText().toString().equals("")) {
                        removeTextWatcherLitrePrice();
                        Double lPrice = Utils.calculateLitrePrice(Double.parseDouble(price.getEditText().getText().toString()), Double.parseDouble(cons.toString()));
                        litrePrice.getEditText().setText(lPrice+"");
                        addTextWatcherLitrePrice();
                    }
                }
            }
        };
        forCons = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!km.getEditText().getText().toString().equals("") && !recent.equals("km") && s.length() > 0) {
                    BigDecimal cons = new BigDecimal(consumption.getEditText().getText().toString());
                    cons = cons.divide(BigDecimal.valueOf(100));
                    cons = cons.multiply(new BigDecimal(km.getEditText().getText().toString()));
                    cons = cons.setScale(2, RoundingMode.HALF_UP);
                    removeTextWatcherLitre();
                    litre.getEditText().setText(cons.toString());
                    recent = "litre";
                    addTextWatcherLitre();
                    if (!litrePrice.getEditText().getText().toString().equals("")) {
                        removeTextWatcherPrice();
                        Double fullPrice = Utils.calculateFullPrice(Double.parseDouble(litrePrice.getEditText().getText().toString()), Double.parseDouble(cons.toString()));
                        price.getEditText().setText(fullPrice+"");
                        addTextWatcherPrice();
                    } else if (!price.getEditText().getText().toString().equals("")) {
                        removeTextWatcherLitrePrice();
                        Double lPrice = Utils.calculateLitrePrice(Double.parseDouble(price.getEditText().getText().toString()), Double.parseDouble(cons.toString()));
                        litrePrice.getEditText().setText(lPrice+"");
                        addTextWatcherLitrePrice();
                    }
                } else if (!litre.getEditText().getText().toString().equals("") && !recent.equals("litre") && s.length() > 0) {
                    BigDecimal dist = new BigDecimal(litre.getEditText().getText().toString());
                    String cons = consumption.getEditText().getText().toString();
                    dist = dist.divide(new BigDecimal(cons), RoundingMode.HALF_UP);
                    dist = dist.multiply(BigDecimal.valueOf(100));
                    dist = dist.setScale(2, RoundingMode.HALF_UP);

                    removeTextWatcherKm();
                    km.getEditText().setText(dist.toString());
                    recent = "km";
                    addTextWatcherKm();
                }
            }
        };

        forLitre = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!km.getEditText().getText().toString().equals("") && !recent.equals("km") && s.length() > 0) {
                    Double cons = Utils.calculateConsumption(Integer.parseInt(km.getEditText().getText().toString()), Double.parseDouble(litre.getEditText().getText().toString()));
                    removeTextWatcherConsumption();
                    consumption.getEditText().setText(cons+"");
                    recent = "consumption";
                    addTextWatcherConsumption();
                } else if (!consumption.getEditText().getText().toString().equals("") && !recent.equals("consumption") && s.length() > 0) {
                    BigDecimal dist = new BigDecimal(litre.getEditText().getText().toString());
                    dist = dist.divide(new BigDecimal(consumption.getEditText().getText().toString()), RoundingMode.HALF_UP);
                    dist = dist.multiply(BigDecimal.valueOf(100));
                    dist = dist.setScale(2, RoundingMode.HALF_UP);
                    removeTextWatcherKm();
                    km.getEditText().setText(dist.toString());
                    recent = "km";
                    addTextWatcherKm();
                }
                if (!litrePrice.getEditText().getText().toString().equals("")) {
                    removeTextWatcherPrice();
                    Double fullPrice = Utils.calculateFullPrice(Double.parseDouble(litrePrice.getEditText().getText().toString()), Double.parseDouble(litre.getEditText().getText().toString()));
                    price.getEditText().setText(fullPrice+"");
                    addTextWatcherPrice();
                } else if (!price.getEditText().getText().toString().equals("")) {
                    removeTextWatcherLitrePrice();
                    Double lPrice = Utils.calculateLitrePrice(Double.parseDouble(price.getEditText().getText().toString()), Double.parseDouble(litre.getEditText().getText().toString()));
                    litrePrice.getEditText().setText(lPrice+"");
                    addTextWatcherLitrePrice();
                }
            }
        };

        addTextWatcherKm();
        addTextWatcherConsumption();
        addTextWatcherLitre();
    }

    private void removeTextWatcherConsumption() {
        consumption.getEditText().removeTextChangedListener(forCons);
    }

    private void addTextWatcherConsumption() {
        consumption.getEditText().addTextChangedListener(forCons);
    }
    
    private void removeTextWatcherKm() {
        km.getEditText().removeTextChangedListener(forKm);
    }

    private void addTextWatcherKm() {
        km.getEditText().addTextChangedListener(forKm);
    }

    private void removeTextWatcherLitre() {
        litre.getEditText().removeTextChangedListener(forLitre);
    }

    private void addTextWatcherLitre() {
        litre.getEditText().addTextChangedListener(forLitre);
    }

    private void removeTextWatcherLitrePrice() {
        litrePrice.getEditText().removeTextChangedListener(forLitrePrice);
    }

    private void addTextWatcherLitrePrice() {
        litrePrice.getEditText().addTextChangedListener(forLitrePrice);
    }
    private void removeTextWatcherPrice() {
        price.getEditText().removeTextChangedListener(forPrice);
    }

    private void addTextWatcherPrice() {
        price.getEditText().addTextChangedListener(forPrice);
    }

 */
}
