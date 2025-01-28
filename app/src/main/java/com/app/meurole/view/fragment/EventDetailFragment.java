package com.app.meurole.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.meurole.R;
import com.app.meurole.model.Event;
import com.app.meurole.view.UserLoginActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment {


    private static final String ARG_EVENT_ID = "EVENT_ID";
    private TextView textViewNome, textViewData, textViewLocal, textViewValor, textViewTipo;
    private ImageView imageViewEventThumb;
    private Button buttonConfirmarInscricao;
    private ActivityResultLauncher<Intent> loginActivityResultLauncher;

    private String eventId; // Recebe o ID do evento selecionado

    public EventDetailFragment() {
        // Required empty public constructor
    }

    public static EventDetailFragment newInstance(String eventId) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);


        imageViewEventThumb     = view.findViewById(R.id.imageViewEventThumb);
        textViewNome            = view.findViewById(R.id.textViewDetalheNome);
        textViewData            = view.findViewById(R.id.textViewDetalheData);
        textViewLocal           = view.findViewById(R.id.textViewDetalheLocal);
        textViewTipo            = view.findViewById(R.id.textViewDetalheTipo); // novo campo
        textViewValor           = view.findViewById(R.id.textViewDetalheValor);
        buttonConfirmarInscricao= view.findViewById(R.id.buttonConfirmarInscricao);

        buttonConfirmarInscricao = view.findViewById(R.id.buttonConfirmarInscricao);
        String eventId = getArguments() != null ? getArguments().getString("EVENT_ID") : null;

        if (!TextUtils.isEmpty(eventId)) {
            carregarDetalhesEvento(eventId);
        } else {
            Toast.makeText(requireContext(), "Evento inválido", Toast.LENGTH_SHORT).show();
        }

        buttonConfirmarInscricao.setOnClickListener(v -> {
            // Verificar se o usuário está logado
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(requireContext(),
                        "É necessário estar logado para se inscrever.",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(requireContext(), UserLoginActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                loginActivityResultLauncher.launch(intent);

            } else {
                confirmarInscricao(user.getUid(), eventId);
            }
        });

        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o launcher: o callback será chamado quando a LoginActivity retornar
        loginActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Se tudo correu bem no LoginActivity
                        Intent data = result.getData();
                        if (data != null) {
                            // Recupera o eventId que foi passado de volta
                            String eventId = data.getStringExtra("EVENT_ID");

                            // Verifica se o usuário está logado
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null && eventId != null) {
                                // Agora que está logado, podemos confirmar a inscrição
                                confirmarInscricao(user.getUid(), eventId);
                            }
                        }
                    }
                }
        );
    }

    private void carregarDetalhesEvento(String eventId) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("eventos").child(eventId);

        eventRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);

                if (event != null) {
                    textViewNome.setText(event.getNome());
                    textViewLocal.setText(event.getLocal());
                    textViewTipo.setText(event.getTipo());

                    Date dataEvento = event.getData();
                    if (dataEvento != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String dataStr = sdf.format(dataEvento);
                        textViewData.setText(dataStr);
                    } else {
                        textViewData.setText("Data não cadastrada");
                    }

                    // Formata valor
                    String valorText = String.valueOf(event.getValorInscricao());
                    textViewValor.setText("R$ " + valorText);

                    // Carrega imagem do evento (thumbUrl), se existir
                    if (event.getThumbUrl() != null && !event.getThumbUrl().isEmpty()) {
                        Glide.with(requireContext())
                                .load(event.getThumbUrl())     // se tiver um drawable de erro
                                .into(imageViewEventThumb);
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Não foi possível encontrar o evento",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(),
                        "Falha ao carregar detalhes do evento",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarInscricao(String userId, String eventId) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Montamos um Map com os caminhos e valores
        Map<String, Object> updates = new HashMap<>();
        updates.put("inscricoes/" + eventId + "/" + userId, true);
        updates.put("usuarios/" + userId + "/eventosInscritos/" + eventId, true);

        // Faz o update em todos esses caminhos de uma vez
        rootRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(),
                        "Inscrição realizada com sucesso!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Falha ao realizar inscrição no evento.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}