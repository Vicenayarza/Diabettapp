package com.example.tfg.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tfg.Modelos.LanguajeItem;
import com.example.tfg.R;

import java.util.List;

public class LanguajeAdapter extends ArrayAdapter<LanguajeItem> {

    private Context context;
    private List<LanguajeItem> languages;

    public LanguajeAdapter(Context context, List<LanguajeItem> languages) {
        super(context, 0, languages);
        this.context = context;
        this.languages = languages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.language_spinner_item, parent, false);
        }

        ImageView flagImage = convertView.findViewById(R.id.flag_image);
        TextView languageName = convertView.findViewById(R.id.language_name);

        LanguajeItem currentItem = languages.get(position);

        if (currentItem != null) {
            flagImage.setImageResource(currentItem.flagResId);
            languageName.setText(currentItem.languageName);
        }

        return convertView;
    }
}