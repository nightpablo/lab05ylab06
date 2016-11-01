package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaTareaActivity extends AppCompatActivity {

    EditText descripcionTarea;
    EditText horaEstimada;
    SeekBar barraPrioridad;
    Spinner responsable;
    Button guardar;
    Button cancelar;
    TextView prioridad;
    ProyectoDAO registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);
        registro = new ProyectoDAO(AltaTareaActivity.this);
        descripcionTarea = (EditText) findViewById(R.id.editText);
        horaEstimada = (EditText) findViewById(R.id.editText2);
        barraPrioridad = (SeekBar) findViewById(R.id.seekBar);
        barraPrioridad.setMax(4);
        barraPrioridad.setProgress(0);
        prioridad = (TextView) findViewById(R.id.textView4);
        prioridad.setText("Prioridad: 1");
        barraPrioridad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prioridad.setText("Prioridad: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        responsable = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<Usuario> adaptador = new ArrayAdapter<Usuario>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,registro.listarUsuarios());
        //Toast.makeText(AltaTareaActivity.this, adaptador.getItem(0).toString(), Toast.LENGTH_SHORT).show();
        responsable.setAdapter(adaptador);

        guardar = (Button) findViewById(R.id.btnGuardar);

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
                    Tarea tarea = new Tarea();
                    tarea.setDescripcion(descripcionTarea.getText().toString());
                    tarea.setHorasEstimadas(Integer.parseInt(horaEstimada.getText().toString()));
                    Prioridad unaprioridad = new Prioridad();
                    unaprioridad.setPrioridad(barraPrioridad.getProgress()+"");
                    tarea.setPrioridad(unaprioridad);
                    tarea.setResponsable((Usuario) responsable.getSelectedItem());

                    registro.nuevaTarea(tarea);
                    //Intent actividad = new Intent();
                    //actividad.putExtra("resultado",(Serializable) tarea);
                    //setResult(RESULT_OK,actividad);
                    Toast.makeText(AltaTareaActivity.this,"Se creó una nueva tarea",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        cancelar = (Button) findViewById(R.id.btnCanelar);
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
