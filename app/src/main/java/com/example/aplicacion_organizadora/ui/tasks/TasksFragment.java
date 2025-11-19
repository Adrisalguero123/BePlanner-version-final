private void sincronizarConFirebase(String titulo, String descripcion, String prioridad) {
    // 🆕 AGREGA ESTE TOAST PARA CONFIRMAR VISUALMENTE
    Toast.makeText(requireContext(), "🔥 Firebase iniciado: " + titulo, Toast.LENGTH_SHORT).show();
    
    try {
        Log.d("FIREBASE_DEBUG", "🎯 MÉTODO LLAMADO - Titulo: " + titulo);
        
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("FIREBASE_DEBUG", "🔧 Firebase instanciado");
        
        Map<String, Object> tarea = new HashMap<>();
        tarea.put("titulo", titulo);
        tarea.put("descripcion", descripcion);
        tarea.put("prioridad", prioridad);
        tarea.put("fecha", FieldValue.serverTimestamp());
        tarea.put("llave", "miclave123");
        
        Log.d("FIREBASE_DEBUG", "📤 Enviando a Firestore...");
        
        db.collection("tareas").add(tarea)
            .addOnSuccessListener(documentReference -> {
                Log.d("FIREBASE_DEBUG", "✅✅✅ ÉXITO - Tarea guardada ID: " + documentReference.getId());
                // 🆕 TOAST DE ÉXITO
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getActivity(), "✅ Sincronizado con web!", Toast.LENGTH_LONG).show()
                    );
                }
            })
            .addOnFailureListener(e -> {
                Log.d("FIREBASE_DEBUG", "❌❌❌ ERROR: " + e.getMessage());
                // 🆕 TOAST DE ERROR
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getActivity(), "❌ Error sincronizando", Toast.LENGTH_LONG).show()
                    );
                }
            });
            
    } catch (Exception e) {
        Log.d("FIREBASE_DEBUG", "💥 EXCEPCIÓN: " + e.getMessage());
    }
}
