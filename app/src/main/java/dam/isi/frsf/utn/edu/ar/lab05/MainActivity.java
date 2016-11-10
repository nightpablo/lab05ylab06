package dam.isi.frsf.utn.edu.ar.lab05;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;


import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class MainActivity extends AppCompatActivity {

    private ListView lvTareas;
    private ProyectoDAO proyectoDAO;
    private Cursor cursor;
    private TareaCursorAdapter tca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intActAlta= new Intent(MainActivity.this,AltaTareaActivity.class);
                intActAlta.putExtra("ID_TAREA", 0);


                startActivityForResult(intActAlta,1234);
            }
        });
        lvTareas = (ListView) findViewById(R.id.listaTareas);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LAB05-MAIN","en resume");
        proyectoDAO = new ProyectoDAO(MainActivity.this);
        proyectoDAO.open();
        cursor = proyectoDAO.listaTareas(1);
        Log.d("LAB05-MAIN","mediol "+cursor.getCount());

        tca = new TareaCursorAdapter(MainActivity.this,cursor,proyectoDAO);

        lvTareas.setAdapter(tca);

        Log.d("LAB05-MAIN","fin resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LAB05-MAIN","on pausa");

        if(cursor!=null) cursor.close();
        if(proyectoDAO!=null) proyectoDAO.close();
        Log.d("LAB05-MAIN","fin on pausa");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.buscar_tarea){
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.content_busqueda_tarea, null);
            final EditText editText_MinutosDesvio = (EditText) dialoglayout.findViewById(R.id.editText_Tarea_MinutosDesvio);
            final CheckBox checkBox_TareaTerminada = (CheckBox) dialoglayout.findViewById(R.id.checkBox_Tarea_Finalizada);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(dialoglayout);

            builder.setView(dialoglayout)
                    .setPositiveButton("Buscar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(MainActivity.this,BusquedaTarea.class);
                                    i.putExtra("Finalizada",checkBox_TareaTerminada.isChecked());
                                    i.putExtra("Minutos",editText_MinutosDesvio.getText().toString());
                                    startActivity(i);


                                }
                            })
                    .setNegativeButton("Cancelar",
                            null
                    );

            builder.create().show();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if (requestCode==1234 && resultCode==RESULT_OK) {
            onResume();

        }
    }
}
