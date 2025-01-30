package com.app.meurole.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.meurole.R;
import com.app.meurole.adapter.UserEventAdapter;
import com.app.meurole.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserRegistrationEventFragment extends Fragment {

    private RecyclerView recyclerViewEventosInscritos;
    private UserEventAdapter userEventAdapter;
    private List<Event> eventosInscritosList = new ArrayList<>();
    private DatabaseReference inscricoesRef;
    private FirebaseUser currentUser;

    public UserRegistrationEventFragment() {
        // Construtor vazio necessário para o Fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_registration_event, container, false);

        recyclerViewEventosInscritos = view.findViewById(R.id.recyclerViewEventosInscritos);
        recyclerViewEventosInscritos.setLayoutManager(new LinearLayoutManager(requireContext()));

        userEventAdapter = new UserEventAdapter(requireContext(), eventosInscritosList, eventId -> {
            abrirDetalheDoEvento(eventId);
        });

        recyclerViewEventosInscritos.setAdapter(userEventAdapter);

        // Verifica se o usuário está logado
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Usuário não está logado!", Toast.LENGTH_SHORT).show();
        } else {
            carregarEventosInscritos(currentUser.getUid());
        }

        return view;
    }

    private void carregarEventosInscritos(String userUid) {
        inscricoesRef = FirebaseDatabase.getInstance().getReference("inscricoes").child(userUid);

        inscricoesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventosInscritosList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String eventId = snapshot.getKey();

                        // Buscar detalhes do evento
                        carregarDetalhesDoEvento(eventId);
                    }
                } else {
                    Toast.makeText(requireContext(), "Nenhum evento encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Erro ao carregar eventos inscritos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDetalhesDoEvento(String eventId) {
        DatabaseReference eventoRef = FirebaseDatabase.getInstance().getReference("eventos").child(eventId);

        eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    eventosInscritosList.add(event);
                    userEventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Erro ao buscar detalhes do evento", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDetalheDoEvento(String eventId) {
        EventDetailFragment fragment = EventDetailFragment.newInstance(eventId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}