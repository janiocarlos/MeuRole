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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerViewEventos;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();
    private DatabaseReference eventsRef;
    private FloatingActionButton fabAddEvent;

    public EventListFragment() {
        // Construtor vazio
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        recyclerViewEventos = view.findViewById(R.id.recyclerViewEventos);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Adapter, etc.
        eventAdapter = new EventAdapter(requireContext(), eventList, eventId -> {
            abrirDetalheDoEvento(eventId);
        });
        recyclerViewEventos.setAdapter(eventAdapter);

        // FAB
        fabAddEvent = view.findViewById(R.id.fabAddEvent);
        // Clique do FAB (caso o usuário esteja logado; mas vamos controlar a visibilidade em onResume)
        fabAddEvent.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new EventCreateFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Carregar eventos do Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference("eventos");
        carregarEventos();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sempre que o fragment voltar ao primeiro plano, checamos login
        checkUserLoginAndUpdateFab();
    }

    private void checkUserLoginAndUpdateFab() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Usuário não logado -> oculta FAB
            fabAddEvent.setVisibility(View.GONE);
        } else {
            // Usuário logado -> exibe FAB
            fabAddEvent.setVisibility(View.VISIBLE);
        }
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
        eventsRef.addValueEventListener(new ValueEventListener() {
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
            }
        });
    }
}
