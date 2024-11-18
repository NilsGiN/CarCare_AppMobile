package com.example.prueba1.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prueba1.R;
import com.example.prueba1.model.Car;
import com.example.prueba1.presenters.CarAdapter;
import com.example.prueba1.presenters.RecordatorioAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    private RecordatorioAdapter recordatorioAdapter;
    private Button ir_registro_car;
    private ImageButton buttonLogout; // Botón de cierre de sesión
    RecyclerView cRecycler;
    CarAdapter cAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser  currentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Verificar si el usuario está autenticado
        currentUser  = firebaseAuth.getCurrentUser ();
        if (currentUser  == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        // Inicializar Firestore y RecyclerView
        mFirestore = FirebaseFirestore.getInstance();

        // Inicializar MantenimientoReminderHelper y verificar mantenimientos
        recordatorioAdapter = new RecordatorioAdapter();
        recordatorioAdapter.verificarMantenimientosDeHoy(this);

        cRecycler = findViewById(R.id.Recyclerview2);
        cRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // Configurar consulta de Firestore
        String userId = currentUser .getUid(); // Obtener el ID del usuario actual
        Query query = mFirestore.collection("Vehiculo").whereEqualTo("userId", userId);
        FirestoreRecyclerOptions<Car> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Car>().setQuery(query, Car.class).build();

        // Inicializar adaptador
        cAdapter = new CarAdapter(firestoreRecyclerOptions, MainActivity.this);
        cAdapter.notifyDataSetChanged();
        cRecycler.setAdapter(cAdapter);

        // Configurar el botón de cierre de sesión
        buttonLogout = findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para cerrar sesión
                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            }
        });

        ir_registro_car = findViewById(R.id.Plus);
        ir_registro_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistroCarActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cAdapter.stopListening();
    }
}