package com.example.aplicacion_organizadora.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.aplicacion_organizadora.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Referencias a los nuevos TextViews de las tarjetas
        TextView recordatorioTitulo = root.findViewById(R.id.home_recordatorio_titulo);
        TextView recordatorioFecha = root.findViewById(R.id.home_recordatorio_fecha);
        TextView tareaTitulo = root.findViewById(R.id.home_tarea_titulo);
        TextView tareaPrioridad = root.findViewById(R.id.home_tarea_prioridad);
        TextView notaTitulo = root.findViewById(R.id.home_nota_titulo);
        TextView notaCuerpo = root.findViewById(R.id.home_nota_cuerpo);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observar y actualizar la tarjeta de recordatorio
        homeViewModel.getNextRecordatorio().observe(getViewLifecycleOwner(), recordatorio -> {
            if (recordatorio != null) {
                recordatorioTitulo.setText(recordatorio.titulo);
                recordatorioFecha.setText("Sonará: " + formatoFecha(recordatorio.fechaHoraAlarma));
                recordatorioFecha.setVisibility(View.VISIBLE);
            } else {
                recordatorioTitulo.setText("Sin recordatorios próximos");
                recordatorioFecha.setVisibility(View.GONE);
            }
        });

        // Observar y actualizar la tarjeta de tarea
        homeViewModel.getTopTask().observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                tareaTitulo.setText(task.title);
                tareaPrioridad.setText("Prioridad: " + task.priority);
                tareaPrioridad.setVisibility(View.VISIBLE);
            } else {
                tareaTitulo.setText("No hay tareas pendientes");
                tareaPrioridad.setVisibility(View.GONE);
            }
        });

        // Observar y actualizar la tarjeta de nota
        homeViewModel.getLatestNote().observe(getViewLifecycleOwner(), note -> {
            if (note != null) {
                notaTitulo.setText(note.title);
                notaCuerpo.setText(note.body);
                notaCuerpo.setVisibility(View.VISIBLE);
            } else {
                notaTitulo.setText("No hay notas recientes");
                notaCuerpo.setVisibility(View.GONE);
            }
        });

        return root;
    }

    private String formatoFecha(long millis) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return df.format(new Date(millis));
    }
}
