package com.app.meurole.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyTextWatcher implements TextWatcher {

    private final EditText evento_valor;

    public MoneyTextWatcher(EditText evento_valor) {
        this.evento_valor = evento_valor;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            String cleanString = s.toString().replaceAll("[R$,.]", "").trim();
            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(parsed / 100);
            evento_valor.setText(formatted);
            evento_valor.setSelection(formatted.length()); // Coloca o cursor no final
        }

    }
}
