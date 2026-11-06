package com.example.clonewhatsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.models.Contacto;
import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {
    private List<Contacto> contactos;
    private OnContactoClickListener listener;

    public interface OnContactoClickListener {
        void onContactoClick(Contacto contacto);
    }

    public ContactoAdapter(List<Contacto> contactos, OnContactoClickListener listener) {
        this.contactos = contactos;
        this.listener = listener;
    }

    public void actualizarLista(List<Contacto> nuevaLista) {
        this.contactos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = contactos.get(position);

        holder.tvNombre.setText(contacto.getNombre() != null && !contacto.getNombre().isEmpty() ? contacto.getNombre() : "Sin nombre");
        holder.tvEmail.setText(contacto.getEmail());

        if (contacto.getFotoUrl() != null && !contacto.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(contacto.getFotoUrl()).into(holder.ivFoto);
        } else {
            holder.ivFoto.setImageResource(android.R.drawable.ic_menu_myplaces); // Imagen por defecto
        }

        holder.itemView.setOnClickListener(v -> listener.onContactoClick(contacto));
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    static class ContactoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNombre, tvEmail;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivFotoContacto);
            tvNombre = itemView.findViewById(R.id.tvNombreContacto);
            tvEmail = itemView.findViewById(R.id.tvEmailContacto);
        }
    }
}