package com.example.prueba1.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.example.prueba1.model.Mantenimiento;
import com.example.prueba1.presenters.CarAdapter;
import com.example.prueba1.presenters.MantenimientoAdapter;
import com.example.prueba1.presenters.RecordatorioAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    private RecordatorioAdapter recordatorioAdapter;
    private Button ir_mosaico;
    private Button ir_registro_car;
    RecyclerView cRecycler;
    CarAdapter cAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    String userId = firebaseAuth.getCurrentUser().getUid();
//    FirebaseAuthSettings authSettings = firebaseAuth.getFirebaseAuthSettings();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  authSettings.setPersistence(FirebaseAuthSettings.Persistence.LOCAL);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        if (currentUser != null) {
//            // Usuario ya autenticado, redirige a la pantalla principal
//            Intent intent = new Intent(MainActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            // Usuario no autenticado, redirige a la pantalla de inicio de sesión
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
        // Inicializar Firestore y RecyclerView
        mFirestore = FirebaseFirestore.getInstance();

        // Inicializar MantenimientoReminderHelper y verificar mantenimientos
        recordatorioAdapter = new RecordatorioAdapter();
        recordatorioAdapter.verificarMantenimientosDeHoy(this);

        cRecycler = findViewById(R.id.Recyclerview2);
        cRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // Configurar consulta de Firestore
        Query query = mFirestore.collection("Vehiculo").whereEqualTo("userId", userId);
        FirestoreRecyclerOptions<Car> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Car>().setQuery(query, Car.class).build();

        // Inicializar adaptador
        cAdapter = new CarAdapter(firestoreRecyclerOptions, MainActivity.this);
        cAdapter.notifyDataSetChanged();
        cRecycler.setAdapter(cAdapter);

//        cAdapter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(MainActivity.this, MosaicoActivity.class);
//
//
//                startActivity(intent);
//
//            }
//        });

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
//    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
//        @Override
//        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//            FirebaseUser user = firebaseAuth.getCurrentUser();
//            if (user != null) {
//                // Usuario autenticado
//                Log.d("Auth", "Usuario autenticado: " + user.getEmail());
//            } else {
//                // Usuario no autenticado
//                Log.d("Auth", "Usuario no autenticado");
//            }
//        }
//    };

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseAuth.addAuthStateListener(authStateListener);
        cAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (authStateListener != null) {
//            firebaseAuth.removeAuthStateListener(authStateListener);
//        }
        cAdapter.stopListening();
    }
}