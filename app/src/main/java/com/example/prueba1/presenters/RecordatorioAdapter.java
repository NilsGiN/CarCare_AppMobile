package com.example.prueba1.presenters;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prueba1.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordatorioAdapter {

    private final FirebaseFirestore mFirestore;

    public RecordatorioAdapter() {
        this.mFirestore = FirebaseFirestore.getInstance();

    }

    // Método para verificar los mantenimientos del día
    public void verificarMantenimientosDeHoy(Context context) {
        // Obtener la fecha actual en formato "dd/MM/yyyy"
        String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        // Consultar en Firestore por mantenimientos con fecha igual a hoy
        mFirestore.collection("Mantenimiento")
                .whereEqualTo("fecha_prox", fechaHoy)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            StringBuilder mensaje = new StringBuilder("Tienes mantenimientos para el día de hoy:\n");

                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                String tipoMantenimiento = doc.getString("tipo");
                                String auto = doc.getString("auto");

                                // Consulta adicional para obtener el nombre del tipo de mantenimiento
                                mFirestore.collection("Tipo_Mantenimiento")
                                        .document(tipoMantenimiento) // Busca el documento por el ID
                                        .get()
                                        .addOnCompleteListener(tipoTask -> {
                                            if (tipoTask.isSuccessful() && tipoTask.getResult() != null) {
                                                // Obtener el nombre del mantenimiento
                                                String nombreMantenimiento = tipoTask.getResult().getString("nombre");
                                                if (nombreMantenimiento != null) {
                                                    mensaje.append("• ").append(nombreMantenimiento)
                                                            .append(" - ").append(auto).append("\n");
                                                } else {
                                                    mensaje.append("• ").append(tipoMantenimiento)
                                                            .append(" (nombre no encontrado)").append(" - ")
                                                            .append(auto).append("\n");
                                                }

                                                // Mostrar el popup cuando termine de construir el mensaje
                                                if (mensaje.length() > 0) {
                                                    mostrarPopupRecordatorio(context, mensaje.toString());
                                                }
                                            } else {
                                                Toast.makeText(context, "Error al obtener el nombre del mantenimiento", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // No hay mantenimientos para hoy
                            mostrarPopupRecordatorio(context, "No hay mantenimientos pendientes para el día de hoy");
                        }
                    } else {
                        // Error al obtener los datos o no existen mantenimientos para hoy
                        mostrarPopupRecordatorio(context, "No hay mantenimientos pendientes para el día de hoy");
                    }
                });
    }

    // Método para mostrar el popup de recordatorio
    private void mostrarPopupRecordatorio(Context context, String mensaje) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_recordatorio);
        dialog.setCancelable(true);

        TextView textViewRecordatorio = dialog.findViewById(R.id.textViewRecordatorio);
        textViewRecordatorio.setText(mensaje);

        Button buttonCerrarPopup = dialog.findViewById(R.id.buttonCerrarPopup);
        buttonCerrarPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}