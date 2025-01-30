package com.app.meurole.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meurole.R;
import com.app.meurole.adapter.EventAdapter;
import com.app.meurole.model.Event;
import com.app.meurole.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerViewEventos;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    private DatabaseReference eventsRef;

    public EventListFragment() {
        // Construtor vazio exigido pelo Fragment
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // "Inflando" o layout do fragment (fragment_event_list.xml)
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);

        // Configura o RecyclerView (similar ao que você fazia no onCreate da Activity)
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(requireContext()));
        eventAdapter = new EventAdapter(requireContext(), eventList, eventId -> {
            // Aqui chamamos um método para trocar para o EventDetailFragment
            abrirDetalheDoEvento(eventId);
        });

        recyclerViewEventos.setAdapter(eventAdapter);

        // Inicializar referência do Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference("eventos");

        // Carregar lista de eventos
        carregarEventos();


        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddEvent = view.findViewById(R.id.fabAddEvent);
        fabAddEvent.setOnClickListener(v -> {
            // Navegar para o EventCreateFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new EventCreateFragment())
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void abrirDetalheDoEvento(String eventId) {

        EventDetailFragment fragment = EventDetailFragment.newInstance(eventId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void carregarEventos() {
        eventsRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        event.setEventId(snapshot.getKey());
                        eventList.add(event);
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(),
                        "Erro ao carregar eventos: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("EventListFragment", "onCancelled: " + databaseError.getMessage());
            }
        });
    }
}