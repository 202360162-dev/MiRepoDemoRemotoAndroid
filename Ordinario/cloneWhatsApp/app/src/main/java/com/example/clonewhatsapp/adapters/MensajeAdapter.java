package com.example.clonewhatsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.models.Mensaje;
import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TIPO_ENVIADO = 1;
    private static final int TIPO_RECIBIDO = 2;
    private List<Mensaje> mensajes;
    private String miUserId;

    public MensajeAdapter(List<Mensaje> mensajes, String miUserId) {
        this.mensajes = mensajes;
        this.miUserId = miUserId;
    }

    @Override
    public int getItemViewType(int position) {
        if (mensajes.get(position).getSenderId().equals(miUserId)) return TIPO_ENVIADO;
        return TIPO_RECIBIDO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_ENVIADO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_enviado, parent, false);
            return new EnviadoViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_recibido, parent, false);
            return new RecibidoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje msj = mensajes.get(position);
        if (holder.getItemViewType() == TIPO_ENVIADO) {
            EnviadoViewHolder evh = (EnviadoViewHolder) holder;
            evh.tvContent.setText(msj.getContent());
            // Lógica de estados (Fase 8)
            if (msj.getEstado().equals("leido")) evh.tvEstado.setText("✓✓ (Azul)");
            else if (msj.getEstado().equals("recibido")) evh.tvEstado.setText("✓✓");
            else evh.tvEstado.setText("✓");
        } else {
            ((RecibidoViewHolder) holder).tvContent.setText(msj.getContent());
        }
    }

    @Override
    public int getItemCount() { return mensajes.size(); }

    static class EnviadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvEstado;
        EnviadoViewHolder(View v) { super(v); tvContent = v.findViewById(R.id.tvMsgEnviado); tvEstado = v.findViewById(R.id.tvMsgEstado); }
    }
    static class RecibidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        RecibidoViewHolder(View v) { super(v); tvContent = v.findViewById(R.id.tvMsgRecibido); }
    }
}