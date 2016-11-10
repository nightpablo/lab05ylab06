package dam.isi.frsf.utn.edu.ar.lab05;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

/**
 * Created by mdominguez on 06/10/16.
 */
public class TareaCursorAdapter extends CursorAdapter {
    private LayoutInflater inflador;
    private ProyectoDAO myDao;
    private Context contexto;

    public TareaCursorAdapter(Context contexto, Cursor c, ProyectoDAO dao) {
        super(contexto, c, false);
        myDao = dao;
        this.contexto = contexto;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vista = inflador.inflate(R.layout.fila_tarea, viewGroup, false);
        return vista;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        //obtener la posicion de la fila actual y asignarla a los botones y checkboxes
        final int pos = cursor.getPosition();

        // Referencias UI.
        TextView nombre = (TextView) view.findViewById(R.id.tareaTitulo);
        TextView tiempoAsignado = (TextView) view.findViewById(R.id.tareaMinutosAsignados);
        final TextView tiempoTrabajado = (TextView) view.findViewById(R.id.tareaMinutosTrabajados);
        TextView prioridad = (TextView) view.findViewById(R.id.tareaPrioridad);
        TextView responsable = (TextView) view.findViewById(R.id.tareaResponsable);
        final CheckBox finalizada = (CheckBox) view.findViewById(R.id.tareaFinalizada);

        final Button btnFinalizar = (Button) view.findViewById(R.id.tareaBtnFinalizada);
        final Button btnEditar = (Button) view.findViewById(R.id.tareaBtnEditarDatos);
        final Button btnBorrar = (Button) view.findViewById(R.id.tareaBtnBorrar);
        final ToggleButton btnEstado = (ToggleButton) view.findViewById(R.id.tareaBtnTrabajando);

        btnEstado.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEstado.setOnClickListener(new View.OnClickListener() {
            private long TiempoInicial=0;
            private long TiempoFinal=0;
            @Override
            public void onClick(View v) {
                if(finalizada.isChecked()){
                    Toast.makeText(context, "No se puede trabajar, la tarea está finalizada", Toast.LENGTH_SHORT).show();
                    btnEstado.setChecked(false);
                    return;
                }
                boolean isChecked = btnEstado.isChecked();

                if(isChecked) {
                    TiempoInicial = System.currentTimeMillis();
                }
                else {
                    if(TiempoInicial!=0){


                        TiempoFinal = System.currentTimeMillis();

                        int tiempoTrabajadoCalculado = ((int) (long) (TiempoFinal-TiempoInicial)/1000);
                        if(tiempoTrabajadoCalculado-5<0)
                            return;

                        tiempoTrabajadoCalculado = (int) tiempoTrabajadoCalculado/5;


                        ProyectoDAO registro = new ProyectoDAO(context);

                        Tarea miTarea = registro.buscarTarea((Integer) v.getTag());
                        miTarea.setMinutosTrabajados(miTarea.getMinutosTrabajados()+tiempoTrabajadoCalculado);
                        registro.actualizarTarea(miTarea);

                        TiempoInicial=0;
                        TiempoFinal=0;
                        handlerRefresh.sendEmptyMessage(1);


                    }




                }
            }
        });

        nombre.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)));
        Integer horasAsigandas = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS));
        tiempoAsignado.setText(horasAsigandas * 60 + " minutos ");

        Integer minutosAsigandos = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS));
        tiempoTrabajado.setText(minutosAsigandos + " minutos ");
        String p = cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS));
        prioridad.setText(p);
        responsable.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS))+" ");
        finalizada.setChecked(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA)) == 1);
        //finalizada.setTextIsSelectable(false); No es este, es setClickable
        finalizada.setClickable(false);

        btnEditar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalizada.isChecked()){
                    Toast.makeText(context, "No se puede editar, la tarea está finalizada", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(btnEstado.isChecked()){
                    Toast.makeText(context, "No se puede editar, la tarea está trabajando", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Integer idTarea = (Integer) view.getTag();
                Intent intEditarAct = new Intent(contexto, AltaTareaActivity.class);
                intEditarAct.putExtra("ID_TAREA", idTarea);
                context.startActivity(intEditarAct);

            }
        });

        btnFinalizar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea = (Integer) view.getTag();
                Thread backGroundUpdate = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalizada.isChecked()){
                            Toast.makeText(context, "La tarea ya está finalizada", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(btnEstado.isChecked()){
                            Toast.makeText(context, "No se puede finalizar, la tarea está trabajando", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("LAB05-MAIN", "finalizar tarea : --- " + idTarea);
                        myDao.finalizar(idTarea);
                        handlerRefresh.sendEmptyMessage(1);
                    }
                });
                backGroundUpdate.start();
            }
        });

        btnBorrar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnEstado.isChecked()){
                    Toast.makeText(context, "No se puede borrar, la tarea está trabajando", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Acá preguntamos si deseamos borrar o no!
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final Integer idTarea = (Integer) v.getTag();
                builder.setTitle("Borrar Tarea")
                        .setMessage("¿Está seguro que desea borrar esta tarea?")
                        .setPositiveButton("Si",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ProyectoDAO registro = new ProyectoDAO(context);
                                        registro.borrarTarea(registro.buscarTarea(idTarea));
                                        handlerRefresh.sendEmptyMessage(1);
                                    }
                                })
                        .setNegativeButton("No",
                                null);

                builder.create().show();


            }
        });
    }

    Handler handlerRefresh = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            TareaCursorAdapter.this.changeCursor(myDao.listaTareas(1));
        }
    };
}