package com.nct.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Calendar;

public class CouperDateTextWatcher implements TextWatcher {
    EditText mditText;
    private String current = "";
//    private String ddmmyyyy = "DDMMYYYY";
    private String yyyymmdd = "YYYYMMDD";
    private Calendar cal;

    public CouperDateTextWatcher(EditText edt) {
        this.mditText = edt;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        cal = Calendar.getInstance();
        if (!s.toString().equals(current)) {
            String clean = s.toString().replaceAll("[^\\d.]", "");
            String cleanC = current.replaceAll("[^\\d.]", "");

            int cl = clean.length();
            int sel = cl;
            for (int i = 4; i <= cl && i < 8; i += 2) {
                sel++;
            }
            if (clean.equals(cleanC)) sel--;

            if (clean.length() < 8) {
                clean = clean + yyyymmdd.substring(clean.length());
            } else {
                int year = Integer.parseInt(clean.substring(0, 4));
                int mon = Integer.parseInt(clean.substring(4, 6));
                int day = Integer.parseInt(clean.substring(6, 8));

                if (mon > 12) {
                    mon = 12;
                }
                cal.set(Calendar.MONTH, mon - 1);
                day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                clean = String.format("%02d%02d%02d", year, mon, day);
            }

            clean = String.format("%s-%s-%s", clean.substring(0, 4), clean.substring(4, 6), clean.substring(6, 8));
            current = clean;

            mditText.setText(current);
            if (current.length() > 0 && sel > 0) {
                mditText.setSelection(sel < current.length() ? sel : current.length());
            }
        }
    }
}
