package com.app.meurole.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meurole.R;
import com.app.meurole.adapter.EventAdapter;
import com.app.meurole.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEventos;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    // Referência para o nó "eventos" no Firebase
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_list);
        setContentView(R.layout.activity_event_list);

        recyclerViewEventos = findViewById(R.id.recyclerViewEventos);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();

        // Inicializando o adapter com lista vazia
        eventAdapter = new EventAdapter(this, eventList);
        recyclerViewEventos.setAdapter(eventAdapter);

        // Inicializando referência do Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference("eventos");

        // Carregar lista de eventos
        carregarEventos();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void carregarEventos() {
        // Lê todos os filhos do nó "eventos"
        eventsRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear(); // Limpa a lista para evitar duplicações

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);

                    // Se o objeto foi deserializado com sucesso
                    if (event != null) {
                        // Armazenamos o 'eventId' a partir do 'snapshot.getKey()' ou do JSON
                        event.setEventId(snapshot.getKey());

                        eventList.add(event);
                    }
                }
                // Atualiza o RecyclerView
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Em caso de erro
                Toast.makeText(EventListActivity.this,
                        "Erro ao carregar eventos: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("EventListActivity", "onCancelled: " + databaseError.getMessage());
            }
        });
    }
}