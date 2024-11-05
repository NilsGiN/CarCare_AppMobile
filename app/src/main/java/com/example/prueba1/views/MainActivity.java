package com.example.prueba1.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    private RecordatorioAdapter recordatorioAdapter;
    private Button ir_mosaico;
    private Button ir_registro_car;
    RecyclerView cRecycler;
    CarAdapter cAdapter;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializar Firestore y RecyclerView
        mFirestore = FirebaseFirestore.getInstance();

        // Inicializar MantenimientoReminderHelper y verificar mantenimientos
        recordatorioAdapter = new RecordatorioAdapter();
        recordatorioAdapter.verificarMantenimientosDeHoy(this);

        cRecycler = findViewById(R.id.Recyclerview2);
        cRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // Configurar consulta de Firestore
        Query query = mFirestore.collection("Vehiculo");
        FirestoreRecyclerOptions<Car> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Car>().setQuery(query, Car.class).build();

        // Inicializar adaptador
        cAdapter = new CarAdapter(firestoreRecyclerOptions);
        cAdapter.notifyDataSetChanged();
        cRecycler.setAdapter(cAdapter);

        cAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MosaicoActivity.class);
                startActivity(intent);
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