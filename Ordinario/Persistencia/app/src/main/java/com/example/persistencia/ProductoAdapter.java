package com.example.persistencia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {
    private List<Producto> productos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Producto producto);
        void onDelete(int id);
    }

    public ProductoAdapter(List<Producto> productos, OnItemClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = productos.get(position);
        holder.tvTitulo.setText(p.producto + " - $" + p.precio);
        holder.tvDesc.setText(p.descripcion);
        holder.btnEditar.setOnClickListener(v -> listener.onEdit(p));
        holder.btnEliminar.setOnClickListener(v -> listener.onDelete(p.id));
    }

    @Override
    public int getItemCount() { return productos.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDesc;
        Button btnEditar, btnEliminar;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}