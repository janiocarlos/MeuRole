package com.app.meurole.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meurole.R;
import com.app.meurole.model.Event;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(String eventId);
    }
    private OnEventClickListener listener;
    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList, OnEventClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
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

        Date dateObj = event.getData();
        if (dateObj != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dataFormatada = sdf.format(dateObj);
            holder.textViewData.setText(dataFormatada);
        } else {
            holder.textViewData.setText("Data Indefinida");
        }


        Glide.with(context)
                .load(event.getThumbUrl())
                .into(holder.imageViewThumb);

        holder.buttonVerDetalhes.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event.getEventId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumb;
        TextView textViewNome, textViewLocal, textViewData;
        Button buttonVerDetalhes;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumb = itemView.findViewById(R.id.imageViewThumb);
            textViewNome  = itemView.findViewById(R.id.textViewNomeEvento);
            textViewLocal = itemView.findViewById(R.id.textViewLocalEvento);
            textViewData  = itemView.findViewById(R.id.textViewDataEvento);
            buttonVerDetalhes   = itemView.findViewById(R.id.buttonVerDetalhes);
        }
    }

}
