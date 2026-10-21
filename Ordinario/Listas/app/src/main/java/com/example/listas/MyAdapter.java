package com.example.listas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// 1. Define el Adapter que hereda de RecyclerView.Adapter
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ItemModel> itemList;

    public MyAdapter(List<ItemModel> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout (item_layout.xml) de cada elemento de la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Asigna los datos de la lista al Holder
        ItemModel item = itemList.get(position);
        holder.tvTitulo.setText(item.getTitulo());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // 2. Define el Holder (ViewHolder)
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vincula el componente visual del layout
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
        }
    }
}