package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

/**
 * Created by Pablo on 10/11/2016.
 */

public class BusquedaTarea extends AppCompatActivity {


    private ListView listView_listaTarea;
    private Button Cerrar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_tarea);

        listView_listaTarea = (ListView) findViewById(R.id.listView_lista_tarea);
        Intent intent = getIntent();
        intent.getExtras().getBoolean("Finalizada");
        List<Tarea> listaTarea = new ProyectoDAO(BusquedaTarea.this).listarDesviosPlanificacion(intent.getExtras().getBoolean("Finalizada"),Integer.parseInt(intent.getExtras().getString("Minutos")));
        ArrayAdapter<Tarea> adaptador = new ArrayAdapter<Tarea>(BusquedaTarea.this,android.R.layout.simple_list_item_1,listaTarea);

        listView_listaTarea.setAdapter(adaptador);

        Cerrar = (Button) findViewById(R.id.button_Cerrar);
        Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
