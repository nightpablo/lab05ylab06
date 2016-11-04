package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaTareaActivity extends AppCompatActivity {

    private EditText descripcionTarea;
    private EditText horaEstimada;
    private SeekBar barraPrioridad;
    private Spinner responsable;
    private Button guardar;
    private Button cancelar;
    private TextView prioridad;
    private ProyectoDAO registro;
    private List<Prioridad> listaPrioridad;
    private Tarea tarea;
    private boolean esEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);

        registro = new ProyectoDAO(AltaTareaActivity.this);

        descripcionTarea = (EditText) findViewById(R.id.editText);
        horaEstimada = (EditText) findViewById(R.id.editText2);
        barraPrioridad = (SeekBar) findViewById(R.id.seekBar);
        prioridad = (TextView) findViewById(R.id.textView4);
        responsable = (Spinner) findViewById(R.id.spinner);
        guardar = (Button) findViewById(R.id.btnGuardar);
        cancelar = (Button) findViewById(R.id.btnCanelar);


        barraPrioridad.setMax(3);
        ArrayAdapter<Usuario> adaptador = new ArrayAdapter<Usuario>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,registro.listarUsuarios());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        responsable.setAdapter(adaptador);

        //if(!(getIntent().getSerializableExtra("ID_TAREA") instanceof Integer)) {
        tarea = new Tarea();
        barraPrioridad.setProgress(0);
        esEditable=false;
        if(getIntent().getExtras().getInt("ID_TAREA")!=0){
            tarea = registro.buscarTarea(getIntent().getExtras().getInt("ID_TAREA"));
            barraPrioridad.setProgress(tarea.getPrioridad().getId());
            responsable.setSelection(tarea.getResponsable().getId());
            descripcionTarea.setText(tarea.getDescripcion());
            horaEstimada.setText(tarea.getHorasEstimadas());
            esEditable=true;
        }



        listaPrioridad = registro.listarPrioridades();
        prioridad.setText("Prioridad: "+listaPrioridad.get(0).toString());

        barraPrioridad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prioridad.setText("Prioridad: "+listaPrioridad.get(barraPrioridad.getProgress()).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });









        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descripcionTarea.getText().toString().equals(""))
                    Toast.makeText(AltaTareaActivity.this,"Debe ingresar una descripción de la tarea",Toast.LENGTH_SHORT).show();
                else if(horaEstimada.getText().toString().equals(""))
                    Toast.makeText(AltaTareaActivity.this,"Debe ingresar una hora estimada de la tarea",Toast.LENGTH_SHORT).show();
                else if(responsable.getSelectedItemPosition()==-1)
                    Toast.makeText(AltaTareaActivity.this,"Debe seleccionar un responsable de la tarea",Toast.LENGTH_SHORT).show();
                else{
                    tarea.setDescripcion(descripcionTarea.getText().toString());
                    tarea.setHorasEstimadas(Integer.parseInt(horaEstimada.getText().toString()));
                    tarea.setMinutosTrabajados(0);
                    tarea.setFinalizada(false);
                    tarea.setProyecto(registro.buscarProyecto(1)); // Proyecto 1 temporal
                    tarea.setPrioridad(listaPrioridad.get(barraPrioridad.getProgress()));

                    tarea.setResponsable((Usuario) responsable.getSelectedItem());
                    if(esEditable) {
                        registro.actualizarTarea(tarea);
                        Toast.makeText(AltaTareaActivity.this,"Se editó la tarea",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        registro.nuevaTarea(tarea);
                        Toast.makeText(AltaTareaActivity.this,"Se creó una nueva tarea",Toast.LENGTH_SHORT).show();
                    }
                    Intent actividad = new Intent();
                    setResult(RESULT_OK,actividad);

                    finish();
                }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividad = new Intent();
                setResult(RESULT_CANCELED,actividad);
                finish();
            }
        });




    }
}
