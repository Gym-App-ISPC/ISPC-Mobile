package com.ispc.gymapp.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.gymapp.R;
import com.ispc.gymapp.views.activities.Plan;

import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<Plan> planes;

    public CarritoAdapter(List<Plan> planes) {
        this.planes = planes;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Plan plan = planes.get(position);
        holder.nombre.setText(plan.getNombre());
        holder.descripcion.setText(plan.getDescripcion());
        holder.imagenImageView.setImageResource(plan.getImagen());
    }

    @Override
    public int getItemCount() {
        return planes.size();
    }

    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, descripcion;
        ImageView imagenImageView;

        public CarritoViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.plan_nombre);
            descripcion = itemView.findViewById(R.id.plan_descripcion);
            imagenImageView = itemView.findViewById(R.id.plan_img);
        }
    }
}
