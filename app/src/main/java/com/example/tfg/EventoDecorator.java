package com.example.tfg;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class EventoDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private final HashSet<CalendarDay> dates;

    public EventoDecorator(Context context, Collection<CalendarDay> dates) {
        this.drawable = new ColorDrawable(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}