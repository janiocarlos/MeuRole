package com.app.meurole.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.meurole.R;
import com.app.meurole.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EventCreateActivity extends AppCompatActivity {

    private EditText editTextNome, editTextData, editTextLocal, editTextValor;
    private Spinner SpinnerTipo;
    private TextView LabelThumb;
    private Button buttonCadastrarEvento,BotaoThumb;

    // Referência ao nó "eventos" no Firebase
    private DatabaseReference eventsRef;
    private StorageReference eventsStorage;
    private Uri UriThumb;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    UriThumb = result.getData().getData();
                    //ImageView imgPreview = findViewById(R.id.imgFotoPreview);
                    //imgPreview.setImageURI(fotoUri); // Show the selected image in the ImageView
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }});

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
        BotaoThumb = findViewById(R.id.BotaoThumb);
        SpinnerTipo = findViewById(R.id.SpinnerTipo);

        buttonCadastrarEvento = findViewById(R.id.buttonCadastrarEvento);

        // Inicializa referência do Firebase (nó "eventos")
        eventsRef = FirebaseDatabase.getInstance().getReference("eventos");
        eventsStorage = FirebaseStorage.getInstance().getReference();

        // Ação do botão de cadastrar

        BotaoThumb.setOnClickListener(v -> abrirGaleria());

        buttonCadastrarEvento.setOnClickListener(v -> {
            if (UriThumb != null) {
                saveThumbAndEvent();
            } else {
                Toast.makeText(this, "Selecione uma foto!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveThumbAndEvent(){
        StorageReference thumbRef = eventsStorage.child("thumbsEvents/"+System.currentTimeMillis()+".jpg");

        thumbRef.putFile(UriThumb).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    thumbRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String ThumbUrl = uri.toString();
                        cadastrarEvento(ThumbUrl);
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao salvar foto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirGaleria() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
    }

    private void cadastrarEvento(String UriThumb) {
        // Obter os valores digitados
        String nomeEvento = editTextNome.getText().toString().trim();
        String dataEvento = editTextData.getText().toString().trim();
        String localEvento = editTextLocal.getText().toString().trim();
        String valorStr = editTextValor.getText().toString().trim();
        String tipoEvento = SpinnerTipo.getSelectedItem().toString();
        String UrlThumbEvento = UriThumb;
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
                tipoEvento,
                valorInscricao,
                UrlThumbEvento
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
                        String erro;
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            System.out.println("Erro: "+e.toString());
                        }

                       // Toast.makeText(
                               // EventCreateActivity.this,
                                //erro,
                               // Toast.LENGTH_SHORT
                        //).show();
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