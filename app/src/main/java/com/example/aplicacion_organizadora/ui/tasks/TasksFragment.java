package com.example.aplicacion_organizadora.ui.tasks;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion_organizadora.R;

import java.util.Arrays;

public class TasksFragment extends Fragment {

    private TaskViewModel viewModel;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new TaskAdapter(
                task -> viewModel.delete(task),
                task -> mostrarDialogoEditarTarea(task),
                task -> viewModel.update(task)
        );
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        viewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            adapter.setTasks(tasks);
        });

        View btnAddTask = view.findViewById(R.id.btnAddTask);
        btnAddTask.setOnClickListener(v -> mostrarDialogoNuevaTarea());
    }

    private void mostrarDialogoNuevaTarea() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_task, null);

        EditText editTitle = dialogView.findViewById(R.id.editTitle);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        RadioGroup radioPriority = dialogView.findViewById(R.id.radioPriority);


        editTitle.addTextChangedListener(new TextWatcher() {
            private boolean bloqueando = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (bloqueando) return;
                String texto = s.toString().trim();
                String[] palabras = texto.split("\\s+");
                int cantidadPalabras = texto.isEmpty() ? 0 : palabras.length;
                if (cantidadPalabras > 20) {
                    bloqueando = true;
                    Toast.makeText(requireContext(),
                            "Máximo 20 palabras en el título", Toast.LENGTH_SHORT).show();
                    String recortado = String.join(" ", Arrays.copyOf(palabras, 20));
                    editTitle.setText(recortado);
                    editTitle.setSelection(recortado.length());
                    bloqueando = false;
                }
            }
        });

        new AlertDialog.Builder(requireContext())
                .setTitle("Nueva tarea")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    int selectedId = radioPriority.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(requireContext(),
                                "Selecciona una prioridad", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String title = editTitle.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();
                    String priority = ((RadioButton) dialogView.findViewById(selectedId))
                            .getText().toString();

                    if (title.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long ahora = System.currentTimeMillis();
                    Task nuevaTarea = new Task(title, description, priority, ahora);
                    nuevaTarea.isDone = false;
                    viewModel.insert(nuevaTarea);

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarTarea(Task tarea) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_task, null);

        EditText editTitle = dialogView.findViewById(R.id.editTitle);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        RadioGroup radioPriority = dialogView.findViewById(R.id.radioPriority);

        editTitle.setText(tarea.title);
        editDescription.setText(tarea.description);
        switch (tarea.priority) {
            case "Alta":
                radioPriority.check(R.id.radioHigh);
                break;
            case "Media":
                radioPriority.check(R.id.radioMedium);
                break;
            case "Baja":
                radioPriority.check(R.id.radioLow);
                break;
        }

        editTitle.addTextChangedListener(new TextWatcher() {
            private boolean bloqueando = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (bloqueando) return;
                String texto = s.toString().trim();
                String[] palabras = texto.split("\\s+");
                int cantidadPalabras = texto.isEmpty() ? 0 : palabras.length;
                if (cantidadPalabras > 20) {
                    bloqueando = true;
                    Toast.makeText(requireContext(),
                            "Máximo 20 palabras en el título", Toast.LENGTH_SHORT).show();
                    String recortado = String.join(" ", Arrays.copyOf(palabras, 20));
                    editTitle.setText(recortado);
                    editTitle.setSelection(recortado.length());
                    bloqueando = false;
                }
            }
        });

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar tarea")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    int selectedId = radioPriority.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(requireContext(),
                                "Selecciona una prioridad", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String nuevaPrioridad = ((RadioButton) dialogView.findViewById(selectedId)).getText().toString();
                    tarea.title = editTitle.getText().toString().trim();
                    tarea.description = editDescription.getText().toString().trim();
                    tarea.priority = nuevaPrioridad;

                    viewModel.update(tarea);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
