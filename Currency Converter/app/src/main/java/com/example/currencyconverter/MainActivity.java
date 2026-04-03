package com.example.currencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final Map<String, Double> rates = new HashMap<String, Double>() {{
        put("INR", 1.0);
        put("USD", 0.012);
        put("JPY", 1.79);
        put("EUR", 0.011);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Array of currency names to fill the two spinners (dropdowns)
        String[] currencies = {"INR", "USD", "JPY", "EUR"};

        Spinner  spinnerFrom = findViewById(R.id.From);
        Spinner  spinnerTo   = findViewById(R.id.To);
        EditText etAmount    = findViewById(R.id.Amount);
        TextView tvResult    = findViewById(R.id.Result);
        Button   btnConvert  = findViewById(R.id.Convert);

        // ArrayAdapter connects the string array to the Spinner widget
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        spinnerFrom.setSelection(0); // Default FROM = INR
        spinnerTo.setSelection(1);   // Default TO   = USD

        btnConvert.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }
            double amount       = Double.parseDouble(amountStr);
            String fromCurrency = spinnerFrom.getSelectedItem().toString();
            String toCurrency   = spinnerTo.getSelectedItem().toString();

            // Formula: Amount → INR first (divide), then → target (multiply)
            double amountInINR = amount / rates.get(fromCurrency);
            double result      = amountInINR * rates.get(toCurrency);

            tvResult.setText(String.format("%.4f %s", result, toCurrency));
        });
    }

    // Inflates the gear icon in the top-right toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    // Opens SettingsActivity when the gear icon is tapped
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}