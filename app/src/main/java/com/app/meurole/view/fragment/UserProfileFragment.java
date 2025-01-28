package com.app.meurole.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.meurole.R;
import com.app.meurole.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserProfileFragment extends Fragment {

    private TextView textViewNome, textViewCPF, textViewDataNasc, textViewEmail;

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
        textViewEmail= view.findViewById(R.id.textViewPerfilEmail);

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
}