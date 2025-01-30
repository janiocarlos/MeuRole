package com.app.meurole.view.fragment;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.app.meurole.R;
import com.app.meurole.adapter.EventAdapter;
import com.app.meurole.adapter.UserEventAdapter;
import com.app.meurole.model.Event;
import com.app.meurole.model.User;
import com.app.meurole.view.MainActivity;
import com.app.meurole.view.UserLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserProfileFragment extends Fragment {

    private TextView textViewNome, textViewCPF, textViewDataNasc, textViewEmail;
    private RecyclerView recyclerViewMeusEventos;
    private UserEventAdapter userEventAdapter;
    private List<Event> meusEventosList = new ArrayList<>();
    private DatabaseReference eventsRef;
    private FirebaseAuth firebaseAuth;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        textViewNome = view.findViewById(R.id.textViewPerfilNome);
        textViewCPF  = view.findViewById(R.id.textViewPerfilCPF);
        textViewDataNasc = view.findViewById(R.id.textViewPerfilDataNasc);
        textViewEmail = view.findViewById(R.id.textViewPerfilEmail);
        recyclerViewMeusEventos = view.findViewById(R.id.recyclerViewMeusEventos);

        // Configura o RecyclerView (similar ao que você fazia no onCreate da Activity)
        recyclerViewMeusEventos.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        userEventAdapter = new UserEventAdapter(requireContext(), meusEventosList, eventId -> {
            // Aqui chamamos um método para trocar para o EventDetailFragment
            abrirDetalheDoEvento(eventId);
        });

        recyclerViewMeusEventos.setAdapter(userEventAdapter);

        // Inicializar referência do Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference("eventos");

        // Configurar RecyclerView na horizontal
        firebaseAuth = FirebaseAuth.getInstance();
        Button btnSigout = view.findViewById(R.id.btnSigout);
        btnSigout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Verifica se o usuário está logado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(),
                    "Usuário não está logado!",
                    Toast.LENGTH_SHORT).show();
            // Se quiser, você pode fechar o fragment ou redirecionar
        } else {
            // Carrega as infos do DB
            carregarDadosUsuario(currentUser.getUid());
            carregarMeusEventos(currentUser.getUid());
        }

        return view;
    }

    private void carregarDadosUsuario(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uid);

        userRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Mapeia pro model User (se for compatível com seu model)
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Preenche as views
                        textViewNome.setText("Nome: " + user.getNome());
                        textViewCPF.setText("CPF: " + user.getCPF());
                        textViewEmail.setText("Email: " + user.getEmail());

                        // user.getDob() é do tipo Date (?)
                        // Precisamos formatar a data
                        Date dob = user.getDob();
                        if (dob != null) {
                            String dataFormatada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .format(dob);
                            textViewDataNasc.setText("Data de Nasc.: " + dataFormatada);
                        } else {
                            textViewDataNasc.setText("Data de Nasc.: não definida");
                        }
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Dados de usuário não encontrados",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(),
                        "Erro ao carregar dados do usuário: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarMeusEventos(String userId) {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("eventos");

        eventsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                meusEventosList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        meusEventosList.add(event);
                    }
                }
                userEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Erro ao carregar eventos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void logoutUser() {
        firebaseAuth.signOut(); // Faz logout do Firebase
        Toast.makeText(requireContext(), "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateMenuVisibility();
        }
        EventListFragment fragment = new EventListFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}