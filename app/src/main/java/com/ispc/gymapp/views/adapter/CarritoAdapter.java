package com.ispc.gymapp.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Plan;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<Plan> plans;

    public CarritoAdapter(List<Plan> planes) {
        this.plans = planes;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Plan plan = plans.get(position);
        holder.nombre.setText(plan.getNombre());
        holder.descripcion.setText(plan.getDescripcion());
        holder.precio.setText(String.valueOf(plan.getPrecio()));
        Picasso.get().load(plan.getImagen()).into(holder.imagenImageView);


        View btnContratar = holder.itemView.findViewById(R.id.btn_contratar);
        if (btnContratar != null) {
            btnContratar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, descripcion, precio;
        ImageView imagenImageView;

        public CarritoViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.plan_nombre);
            descripcion = itemView.findViewById(R.id.plan_descripcion);
            precio = itemView.findViewById(R.id.plan_precio);
            imagenImageView = itemView.findViewById(R.id.plan_img);
        }
    }
}
