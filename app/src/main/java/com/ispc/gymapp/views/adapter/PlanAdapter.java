package com.ispc.gymapp.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ispc.gymapp.R;
import com.ispc.gymapp.views.activities.Ecommerce;
import com.ispc.gymapp.views.activities.Plan;
import com.ispc.gymapp.views.activities.Carrito;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {
    private List<Plan> planes;
    private Context context;
    private Ecommerce ecommerceActivity;

    public PlanAdapter(List<Plan> planes, Context context) {
        this.planes = planes;
        this.context = context;
        if (context instanceof Ecommerce) {
            this.ecommerceActivity = (Ecommerce) context;
        }
    }
    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        Plan plan = planes.get(position);
        holder.nombre.setText(plan.getNombre());
        holder.descripcion.setText(plan.getDescripcion());
        holder.precio.setText(plan.getPrecioString());
        holder.imagenImageView.setImageResource(plan.getImagen());

        if (Carrito.getInstance().getPlanes().contains(plan)) {
            holder.btnContratar.setVisibility(View.GONE);
        } else {
            holder.btnContratar.setVisibility(View.VISIBLE);
            holder.btnContratar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = holder.getAdapterPosition();
                    Carrito.getInstance().agregarPlan(plan);
                    Toast.makeText(context, "Plan agregado al carrito", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(clickedPosition);
                    if (ecommerceActivity != null) {
                        ecommerceActivity.updateCartBadge();
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return planes.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, descripcion, precio;
        public Button btnContratar;
        public ImageView imagenImageView;

        public PlanViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.plan_nombre);
            descripcion = itemView.findViewById(R.id.plan_descripcion);
            precio = itemView.findViewById(R.id.plan_precio);
            btnContratar = itemView.findViewById(R.id.btn_contratar);
            imagenImageView = itemView.findViewById(R.id.plan_img);

        }
    }
}
