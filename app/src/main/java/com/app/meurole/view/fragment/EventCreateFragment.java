package com.app.meurole.view.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.meurole.R;
import com.app.meurole.model.Event;
import com.app.meurole.view.UserCreateActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class EventCreateFragment extends Fragment {

    private EditText evento_nome, evento_data, evento_local, evento_valor;
    private Spinner evento_tipo;
    private Button evento_botao_cadastrar, evento_botao_thumb;

    // Referências de Firebase
    private DatabaseReference eventsRef;
    private StorageReference eventsStorage;

    // URI da imagem escolhida
    private Uri uriThumb;

    // Launcher para abrir a galeria
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            uriThumb = result.getData().getData();
                        } else {
                            Toast.makeText(requireContext(),
                                    "Nenhuma imagem selecionada",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    public EventCreateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_create, container, false);

        evento_nome  = view.findViewById(R.id.evento_nome);
        evento_data  = view.findViewById(R.id.evento_data);
        evento_local = view.findViewById(R.id.evento_local);
        evento_valor = view.findViewById(R.id.evento_valor);

        evento_tipo = view.findViewById(R.id.evento_tipo);

        evento_botao_thumb = view.findViewById(R.id.evento_botao_thumb);
        evento_botao_cadastrar = view.findViewById(R.id.evento_botao_cadastrar);

        eventsRef = FirebaseDatabase.getInstance().getReference("eventos");
        eventsStorage = FirebaseStorage.getInstance().getReference();

        evento_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obter a data atual

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Criar o DatePickerDialog
                DatePickerDialog datePicker = new DatePickerDialog(
                        requireContext(),
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Formatar a data no estilo "dd/MM/yyyy"
                            String formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                            evento_data.setText(formattedDate);
                        },
                        year, month, day
                );

                // Exibir o DatePickerDialog
                datePicker.show();
            }
        });

        evento_botao_thumb.setOnClickListener(v -> abrirGaleria());
        evento_botao_cadastrar.setOnClickListener(v -> {
            if (uriThumb != null) {
                saveThumbAndEvent();
            } else {
                Toast.makeText(requireContext(),
                        "Selecione uma foto!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveThumbAndEvent() {
        StorageReference thumbRef = eventsStorage.child("thumbsEvents/" + System.currentTimeMillis() + ".jpg");

        thumbRef.putFile(uriThumb)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            thumbRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String thumbUrl = uri.toString();
                                cadastrarEvento(thumbUrl);
                            });
                        } else {
                            Toast.makeText(requireContext(),
                                    "Erro ao salvar a foto",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void cadastrarEvento(String uriThumb) {

        String nomeEvento = evento_nome.getText().toString().trim();
        String dataEvento = evento_data.getText().toString().trim();
        String localEvento = evento_local.getText().toString().trim();
        String valorStr    = evento_valor.getText().toString().trim();
        String tipoEvento  = evento_tipo.getSelectedItem().toString();

        if (TextUtils.isEmpty(nomeEvento)) {
            evento_nome.setError("O nome é obrigatório");
            evento_nome.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dataEvento)) {
            evento_data.setError("A data é obrigatória");
            evento_data.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(localEvento)) {
            evento_local.setError("O local é obrigatório");
            evento_local.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(valorStr)) {
            evento_valor.setError("O valor é obrigatório (pode ser 0)");
            evento_valor.requestFocus();
            return;
        }

        double valorInscricao;
        try {
            valorInscricao = Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            evento_valor.setError("Digite um valor válido (Ex: 10.50)");
            evento_valor.requestFocus();
            return;
        }

        // Gera a chave (eventId)
        String generatedKey = eventsRef.push().getKey();
        if (generatedKey == null) {
            Toast.makeText(requireContext(),
                    "Erro ao gerar ID para o evento",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Event newEvent = new Event(
                generatedKey,
                nomeEvento,
                dataEvento,
                localEvento,
                tipoEvento,
                valorInscricao,
                uriThumb  // URL da imagem
        );

        eventsRef.child(generatedKey)
                .setValue(newEvent)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                requireContext(),
                                "Evento cadastrado com sucesso!",
                                Toast.LENGTH_SHORT
                        ).show();

                        limparCampos();
                    } else {
                        Toast.makeText(
                                requireContext(),
                                "Erro ao cadastrar evento.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void limparCampos() {
        evento_nome.setText("");
        evento_data.setText("");
        evento_local.setText("");
        evento_valor.setText("");
        uriThumb = null;
    }
}
