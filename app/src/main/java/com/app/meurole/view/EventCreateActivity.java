package com.app.meurole.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.meurole.R;
import com.app.meurole.model.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventCreateActivity extends AppCompatActivity {

    private EditText editTextNome, editTextData, editTextLocal, editTextValor;
    private Button buttonCadastrarEvento;

    // Referência ao nó "eventos" no Firebase
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_create);

        // Inicializa as views
        editTextNome  = findViewById(R.id.editTextNome);
        editTextData  = findViewById(R.id.editTextData);
        editTextLocal = findViewById(R.id.editTextLocal);
        editTextValor = findViewById(R.id.editTextValor);

        buttonCadastrarEvento = findViewById(R.id.buttonCadastrarEvento);

        // Inicializa referência do Firebase (nó "eventos")
        eventsRef = FirebaseDatabase.getInstance().getReference("eventos");

        // Ação do botão de cadastrar
        buttonCadastrarEvento.setOnClickListener(v -> cadastrarEvento());
    }
    private void cadastrarEvento() {
        // Obter os valores digitados
        String nomeEvento = editTextNome.getText().toString().trim();
        String dataEvento = editTextData.getText().toString().trim();
        String localEvento = editTextLocal.getText().toString().trim();
        String valorStr = editTextValor.getText().toString().trim();

        // Validar campos obrigatórios
        if (TextUtils.isEmpty(nomeEvento)) {
            editTextNome.setError("O nome é obrigatório");
            editTextNome.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dataEvento)) {
            editTextData.setError("A data é obrigatória");
            editTextData.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(localEvento)) {
            editTextLocal.setError("O local é obrigatório");
            editTextLocal.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(valorStr)) {
            editTextValor.setError("O valor é obrigatório (pode ser 0)");
            editTextValor.requestFocus();
            return;
        }

        double valorInscricao;
        try {
            valorInscricao = Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            editTextValor.setError("Digite um valor válido (Ex: 10.50)");
            editTextValor.requestFocus();
            return;
        }

        // Cria um objeto do tipo Event
        // O "eventId" será gerado automaticamente pelo push() ou setValue()
        // mas você pode criar manualmente se desejar.
        String generatedKey = eventsRef.push().getKey();
        if (generatedKey == null) {
            Toast.makeText(this, "Erro ao gerar ID para o evento", Toast.LENGTH_SHORT).show();
            return;
        }

        Event newEvent = new Event(
                generatedKey,
                nomeEvento,
                dataEvento,
                localEvento,
                valorInscricao
        );

        // Salva no Firebase (nó "eventos/eventId")
        eventsRef.child(generatedKey)
                .setValue(newEvent)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                EventCreateActivity.this,
                                "Evento cadastrado com sucesso!",
                                Toast.LENGTH_SHORT
                        ).show();

                        // Limpa os campos ou fecha a Activity, se preferir
                        limparCampos();
                    } else {
                        Toast.makeText(
                                EventCreateActivity.this,
                                "Falha ao cadastrar evento",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void limparCampos() {
        editTextNome.setText("");
        editTextData.setText("");
        editTextLocal.setText("");
        editTextValor.setText("");
    }
}