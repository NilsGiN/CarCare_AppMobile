package com.example.prueba1.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prueba1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroCarActivity extends AppCompatActivity {
    private ImageButton atras_main;
    private Button btnRegistrar;

    private EditText inputmarca;
    private EditText inputmodelo;
    private EditText inputplaca;
    private EditText inputanio;
    private EditText inputsistema;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    //FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    String userId = firebaseAuth.getCurrentUser().getUid();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_car);

        // Inicializacion de boton para volver a pantalla anterior
        atras_main = findViewById(R.id.Atras_main);
        atras_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistroCarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Para BD
        firestore = FirebaseFirestore.getInstance();

        inputmarca = findViewById(R.id.Marca);
        inputmodelo = findViewById(R.id.Modelo);
        inputplaca = findViewById(R.id.Placa);
        inputanio = findViewById(R.id.Anio);
        inputsistema = findViewById(R.id.Sistema);

        inputplaca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Convertimos el texto a mayúsculas
                String upperCaseText = s.toString().toUpperCase();

                // Evitamos que el TextWatcher se vuelva a disparar si el texto ya está en mayúsculas
                if (!upperCaseText.equals(s.toString())) {
                    inputplaca.setText(upperCaseText);
                    inputplaca.setSelection(upperCaseText.length());  // Colocamos el cursor al final del texto
                }
            }
        });

        btnRegistrar = findViewById(R.id.BtnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String marca = inputmarca.getText().toString();
                String modelo = inputmodelo.getText().toString();
                String placa = inputplaca.getText().toString();
                String anio = inputanio.getText().toString();
                String sistema = inputsistema.getText().toString();
                if(marca.isEmpty() || modelo.isEmpty() || placa.isEmpty() || anio.isEmpty() || sistema.isEmpty()){
                    Toast.makeText(RegistroCarActivity.this, "Todos los campos son obligatorios",Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,Object> CarMap = new HashMap<>();
                    CarMap.put("marca",marca);
                    CarMap.put("modelo",modelo);
                    CarMap.put("placa",placa);
                    CarMap.put("anio",anio);
                    CarMap.put("sistema",sistema);
                    CarMap.put("userId",userId);
                    /* taskMap.put("due",dueDate);
                    taskMap.put("time",time);
                    taskMap.put("status",0); */

                    firestore.collection("Vehiculo").add(CarMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegistroCarActivity.this,"Registro de auto guardado", Toast.LENGTH_SHORT).show();
//                                finish(); // Cierra la actividad después de guardar
                                startActivity(new Intent(RegistroCarActivity.this, MainActivity.class));
                            }else{
                                //Toast.makeText(RegistroMantenimientoActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                                Toast.makeText(RegistroCarActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistroCarActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }
}