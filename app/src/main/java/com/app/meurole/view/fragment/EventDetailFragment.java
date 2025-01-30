package com.app.meurole.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventDetailFragment extends Fragment {

    private static final String ARG_EVENT_ID = "EVENT_ID";

    // Views do layout fragment_event_detail.xml
    private ImageView imageViewEventThumb;        // Imagem do evento
    private TextView textViewNome, textViewData, textViewLocal,
            textViewTipo, textViewValor; // Detalhes
    private Button buttonConfirmarInscricao;
    private Button buttonCancelarInscricao;

    private ActivityResultLauncher<Intent> loginActivityResultLauncher;

    private String eventId; // Recebe o ID do evento selecionado

    public EventDetailFragment() {
        // Construtor vazio
    }

    public static EventDetailFragment newInstance(String eventId) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflamos o layout "mais bonito" que você criou (com imagem, card, etc.)
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        // Vincular as Views
        imageViewEventThumb     = view.findViewById(R.id.imageViewEventThumb);
        textViewNome            = view.findViewById(R.id.textViewDetalheNome);
        textViewData            = view.findViewById(R.id.textViewDetalheData);
        textViewLocal           = view.findViewById(R.id.textViewDetalheLocal);
        textViewTipo            = view.findViewById(R.id.textViewDetalheTipo);
        textViewValor           = view.findViewById(R.id.textViewDetalheValor);
        buttonConfirmarInscricao= view.findViewById(R.id.buttonConfirmarInscricao);
        buttonCancelarInscricao = view.findViewById(R.id.buttonCancelarInscricao);

        // Recuperamos o eventId via argumentos
        eventId = (getArguments() != null)
                ? getArguments().getString(ARG_EVENT_ID)
                : null;

        if (!TextUtils.isEmpty(eventId)) {
            carregarDetalhesEvento(eventId);
        } else {
            Toast.makeText(requireContext(), "Evento inválido", Toast.LENGTH_SHORT).show();
        }

        // Botão de inscrição
        buttonConfirmarInscricao.setOnClickListener(v -> {
            // Verificar se o usuário está logado
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(requireContext(),
                        "É necessário estar logado para se inscrever.",
                        Toast.LENGTH_LONG).show();
                // Inicia a UserLoginActivity e aguarda resultado
                Intent intent = new Intent(requireContext(), UserLoginActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                loginActivityResultLauncher.launch(intent);
            } else {
                confirmarInscricao(user.getUid(), eventId);
            }
        });

        // Botão de cancelar inscrição (exemplo de lógica)
        buttonCancelarInscricao.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(requireContext(),
                        "Você não está logado.",
                        Toast.LENGTH_LONG).show();
            } else {
                cancelarInscricao(user.getUid(), eventId);
            }
        });

        return view;
    }

    /**
     * Ler do Realtime Database e preencher as views (incluindo a imagem do evento).
     * Também verifica se o usuário já está inscrito, para exibir/ocultar o botão de cancelar.
     */
    private void carregarDetalhesEvento(String eventId) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference("eventos")
                .child(eventId);

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);

                if (event != null) {
                    // Preenche as textViews
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

                    String valorText = String.valueOf(event.getValorInscricao());
                    textViewValor.setText("R$ " + valorText);

                    // Carrega imagem do evento (thumbUrl), se existir
                    if (event.getThumbUrl() != null && !event.getThumbUrl().isEmpty()) {
                        Glide.with(requireContext())
                                .load(event.getThumbUrl())
                                .into(imageViewEventThumb);
                    }

                    // Verifica se o usuário está inscrito
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        DatabaseReference inscricoesRef = FirebaseDatabase.getInstance().getReference("inscricoes");
                        inscricoesRef.child(eventId).child(currentUser.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Boolean estaInscrito = snapshot.getValue(Boolean.class);
                                        if (estaInscrito != null && estaInscrito) {
                                            // usuário já inscrito => exibe botão CANCELAR
                                            buttonCancelarInscricao.setVisibility(View.VISIBLE);
                                            buttonConfirmarInscricao.setVisibility(View.GONE);
                                        } else {
                                            // não está inscrito => exibe botão CONFIRMAR
                                            buttonCancelarInscricao.setVisibility(View.GONE);
                                            buttonConfirmarInscricao.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // erro
                                    }
                                });
                    } else {
                        // sem login => só mostra botão confirmar (que fará login)
                        buttonCancelarInscricao.setVisibility(View.GONE);
                        buttonConfirmarInscricao.setVisibility(View.VISIBLE);
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
        Map<String, Object> updates = new HashMap<>();
        updates.put("inscricoes/" + eventId + "/" + userId, true);

        rootRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(),
                        "Inscrição realizada com sucesso!",
                        Toast.LENGTH_SHORT).show();
                // Atualiza a tela para exibir o botão de cancelar
                carregarDetalhesEvento(eventId);
            } else {
                Toast.makeText(requireContext(),
                        "Falha ao realizar inscrição.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelarInscricao(String userId, String eventId) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updates = new HashMap<>();
        updates.put("inscricoes/" + eventId + "/" + userId, null);

        rootRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(),
                        "Inscrição cancelada.",
                        Toast.LENGTH_SHORT).show();
                // Atualiza a tela para exibir o botão de confirmar
                carregarDetalhesEvento(eventId);
            } else {
                Toast.makeText(requireContext(),
                        "Falha ao cancelar inscrição.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
