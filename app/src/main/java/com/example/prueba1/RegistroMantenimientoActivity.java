package com.example.prueba1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroMantenimientoActivity extends AppCompatActivity {
    private ImageButton atras_mosaico;
    private EditText inputid_tipo;
    private EditText fecha_mantenimiento;
    private EditText fecha_prox_mantenimiento;
    private String dueDate="";
    private String nextdueDate="";
    private EditText inputkm_actual;
    private EditText inputkm_prox;
    private EditText inputnotas;
    private Button btnRegistrar;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_mantenimiento);
        firestore = FirebaseFirestore.getInstance();

        inputid_tipo = findViewById(R.id.Tipo);
        inputkm_actual = findViewById(R.id.Kim_actual);
        inputkm_prox = findViewById(R.id.Kim_prox);
        inputnotas = findViewById(R.id.Notas);

        // Inicializacion de boton para volver a pantalla anterior
        atras_mosaico = findViewById(R.id.Atras_mosaico);
        atras_mosaico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistroMantenimientoActivity.this, MosaicoActivity.class);
                startActivity(intent);
            }
        });

        btnRegistrar = findViewById(R.id.BtnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id_tipo = inputid_tipo.getText().toString();
                String km_actual = inputkm_actual.getText().toString();
                String km_prox = inputkm_prox.getText().toString();
                String notas = inputnotas.getText().toString();
                if(id_tipo.isEmpty() || dueDate.isEmpty() || nextdueDate.isEmpty() || km_actual.isEmpty() || km_prox.isEmpty() || notas.isEmpty()){
                    Toast.makeText(RegistroMantenimientoActivity.this, "Todos los campos son obligatorios",Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,Object> MantenimientoMap = new HashMap<>();
                    MantenimientoMap.put("id_tipo",id_tipo);
                    MantenimientoMap.put("fecha",dueDate);
                    MantenimientoMap.put("km_actual",km_actual);
                    MantenimientoMap.put("km_prox",km_prox);
                    MantenimientoMap.put("fecha_prox",nextdueDate);
                    MantenimientoMap.put("notas",notas);
                    /* taskMap.put("due",dueDate);
                    taskMap.put("time",time);
                    taskMap.put("status",0); */

                    firestore.collection("Mantenimiento").add(MantenimientoMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegistroMantenimientoActivity.this,"Mantenimiento guardado", Toast.LENGTH_SHORT).show();
                                finish(); // Cierra la actividad después de guardar
                            }else{
                                //Toast.makeText(RegistroMantenimientoActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                                Toast.makeText(RegistroMantenimientoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistroMantenimientoActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuración del TextView como un calendario
        fecha_mantenimiento = findViewById(R.id.Fecha_mantenimiento);
        fecha_mantenimiento.setFocusable(false);
        fecha_mantenimiento.setClickable(true);
        fecha_mantenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDatePicker(fecha_mantenimiento, true);
            }
        });

        fecha_prox_mantenimiento = findViewById(R.id.Fecha_prox_mantenimiento);
        fecha_mantenimiento.setFocusable(false);
        fecha_mantenimiento.setClickable(true);
        fecha_prox_mantenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDatePicker(fecha_prox_mantenimiento, false);
            }
        });
    }

    // Método reutilizable para mostrar el DatePickerDialog
    private void mostrarDatePicker(final EditText editText, final boolean isFirstDate) {
        Calendar calendar = Calendar.getInstance();

        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(RegistroMantenimientoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1; // Los meses empiezan desde 0
                String selectedDate = dayOfMonth + "/" + month + "/" + year;
                editText.setText(selectedDate); // Establece la fecha en el TextView

                if (isFirstDate) {
                    dueDate = selectedDate; // Almacena la primera fecha
                } else {
                    nextdueDate = selectedDate; // Almacena la segunda fecha
                }
            }
        }, year, month, day);

        datePickerDialog.show();
    }
}