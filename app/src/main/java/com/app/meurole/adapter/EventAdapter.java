package com.app.meurole.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meurole.R;
import com.app.meurole.model.Event;
import com.app.meurole.view.EventDetailActivity;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.textViewNome.setText(event.getNome());
        holder.textViewLocal.setText(event.getLocal());
        holder.textViewData.setText(event.getData());

        // Clique em cada item da lista
        holder.itemView.setOnClickListener(v -> {
            // Pode colocar um Toast de teste
            Toast.makeText(context, "Evento selecionado: " + event.getNome(), Toast.LENGTH_SHORT).show();

            // Iniciando a Activity de detalhes e passando o ID do evento via Intent
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("EVENT_ID", event.getEventId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNome, textViewLocal, textViewData;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNome  = itemView.findViewById(R.id.textViewNomeEvento);
            textViewLocal = itemView.findViewById(R.id.textViewLocalEvento);
            textViewData  = itemView.findViewById(R.id.textViewDataEvento);
        }
    }

}
