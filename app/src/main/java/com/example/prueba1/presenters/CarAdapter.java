package com.example.prueba1.presenters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prueba1.R;
import com.example.prueba1.model.Car;
import com.example.prueba1.views.MainActivity;
import com.example.prueba1.views.MosaicoActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class CarAdapter extends FirestoreRecyclerAdapter<Car, CarAdapter.ViewHolder>
        implements View.OnClickListener {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private View.OnClickListener listener;

    private Context context;
    public CarAdapter(@NonNull FirestoreRecyclerOptions<Car> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CarAdapter.ViewHolder viewHolder, int i, @NonNull Car car) {
        viewHolder.placa.setText(car.getPlaca());
        viewHolder.marca.setText(car.getMarca());
        viewHolder.modelo.setText(car.getModelo());
        viewHolder.sistema.setText(car.getSistema());

        // Obtener el ID del documento
        String documentId = getSnapshots().getSnapshot(i).getId();

        // Configurar el click listener para cada card
        viewHolder.itemView.setOnClickListener(view -> {
            // Crear un Intent para abrir la nueva actividad
            Intent intent = new Intent(context, MosaicoActivity.class);
            // Pasar el ID del documento a la nueva actividad
            intent.putExtra("carId", documentId);
            context.startActivity(intent);
        });

        // Configurar el long click listener para opciones de editar/eliminar
        viewHolder.itemView.setOnLongClickListener(view -> {
            // Crear un diálogo con opciones
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Opciones")
                    .setItems(new CharSequence[]{"Eliminar"}, (dialog, which) -> {
                        switch (which) {
//                            case 0: // Editar
//                                editarCar(documentId, car);
//                                break;
                            case 0: // Eliminar
                                eliminarCar(documentId);
                                break;
                        }
                    })
                    .show();
            return true; // Retornar true para indicar que el evento fue manejado
        });
    }

    // Método para eliminar un vehículo
    private void eliminarCar(String documentId) {
        FirebaseFirestore.getInstance().collection("Vehiculo") // Cambia "cars" por el nombre de tu colección en Firestore
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("CarAdapter", "Vehículo eliminado con éxito");
                })
                .addOnFailureListener(e -> {
                    Log.e("CarAdapter", "Error al eliminar el vehículo", e);
                });
    }

    @NonNull
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_car_single, parent, false);

        v.setOnClickListener(this);

        return new CarAdapter.ViewHolder(v);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView placa, marca, modelo, sistema;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            placa = itemView.findViewById(R.id.placa);
            marca = itemView.findViewById(R.id.marca);
            modelo = itemView.findViewById(R.id.modelo);
            sistema = itemView.findViewById(R.id.sistema);
        }
    }
}