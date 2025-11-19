package com.example.aplicacionorganizadora;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    
    public static void guardarTareaEnFirebase(String titulo, String descripcion, String fecha, String hora) {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> tarea = new HashMap<>();
            tarea.put("titulo", titulo);
            tarea.put("descripcion", descripcion);
            tarea.put("fecha", FieldValue.serverTimestamp());
            tarea.put("fechaTarea", fecha);
            tarea.put("horaTarea", hora);
            tarea.put("llave", "miclave123");
            
            db.collection("tareas").add(tarea);
            System.out.println("✅ Tarea enviada a la web: " + titulo);
        } catch (Exception e) {
            System.out.println("❌ Error Firebase: " + e.getMessage());
        }
    }
}
