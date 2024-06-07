package com.ispc.gymapp.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import com.ispc.gymapp.R;
import com.ispc.gymapp.model.Plan;
import com.ispc.gymapp.views.activities.Ecommerce;
import com.ispc.gymapp.views.activities.Carrito;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    public interface OnPlanItemClickListener {
        void onPlanItemClick(Plan plan);
    }

    public interface OnPlanButtonClickListener {
        void onPlanButtonClick(Plan plan);
    }
    private Context context;
    private ArrayList<Plan> plans;
    private int selectedItem = RecyclerView.NO_POSITION;
    private OnPlanItemClickListener itemClickListener;
    private OnPlanButtonClickListener buttonClickListener;


    public int getSelectedPosition() {
        return selectedItem;
    }
    public void setSelectedItem(int position) {
        int previousItem = selectedItem;
        selectedItem = position;
        notifyItemChanged(previousItem);
        notifyItemChanged(selectedItem);
    }


    public PlanAdapter(Context context, ArrayList<Plan> plans, OnPlanItemClickListener itemClickListener, OnPlanButtonClickListener buttonClickListener) {
        this.context = context;
        this.plans = plans;
        this.itemClickListener = itemClickListener;
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        Plan plan = plans.get(position);
        holder.nombreTextView.setText(plan.getNombre());
        holder.descripcionTextView.setText(plan.getDescripcion());
        holder.precioTextView.setText(String.valueOf(plan.getPrecio()));
        Picasso.get().load(plan.getImagen()).into(holder.imagenImageView);

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onPlanItemClick(plan);
            }
        });


        holder.btnContratar.setOnClickListener(v -> {
            Carrito.getInstance().agregarPlan(plan);
            Toast.makeText(context, "Plan agregado al carrito", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
            if (buttonClickListener != null) {
                buttonClickListener.onPlanButtonClick(plan);
            }
        });

        int selectedColor = ContextCompat.getColor(context, R.color.secondary);
        int defaultColor = ContextCompat.getColor(context, R.color.backgroundColor);
        holder.itemView.setBackgroundColor(position == selectedItem ? selectedColor : defaultColor);

    }

    @Override
    public int getItemCount() {
        return plans.size();
    }




    public  class PlanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PlanAdapter adapter;
        public TextView nombreTextView, descripcionTextView, precioTextView;
        public Button btnContratar;
        public ImageView imagenImageView;

        public PlanViewHolder(@NonNull View itemView, PlanAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            nombreTextView = itemView.findViewById(R.id.plan_nombre);
            descripcionTextView = itemView.findViewById(R.id.plan_descripcion);
            precioTextView = itemView.findViewById(R.id.plan_precio);
            btnContratar = itemView.findViewById(R.id.btn_contratar);
            imagenImageView = itemView.findViewById(R.id.plan_img);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                setSelectedItem(position);
            }
        }
    }


}
