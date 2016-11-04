package dam.isi.frsf.utn.edu.ar.lab05.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDAO {

    private static final String _SQL_TAREAS_X_PROYECTO = "SELECT "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata._ID+" as "+ProyectoDBMetadata.TablaTareasMetadata._ID+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.TAREA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD +
            ", "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD +" as "+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE +
            ", "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" as "+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS+
            " FROM "+ProyectoDBMetadata.TABLA_PROYECTO + " "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+", "+
            ProyectoDBMetadata.TABLA_USUARIOS + " "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+", "+
            ProyectoDBMetadata.TABLA_PRIORIDAD + " "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+", "+
            ProyectoDBMetadata.TABLA_TAREAS + " "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+
            " WHERE "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+"."+ProyectoDBMetadata.TablaProyectoMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE+" = "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD+" = "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = ?";

    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;

    public ProyectoDAO(Context c){
        this.dbHelper = new ProyectoOpenHelper(c);
    }

    public void open(){
        this.open(false);
    }

    public void open(Boolean toWrite){
        if(toWrite) {
            db = dbHelper.getWritableDatabase();
        }
        else{
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close(){
        db = dbHelper.getReadableDatabase();
    }

    public Cursor listaTareas(Integer idProyecto){
        Cursor cursorPry = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata._ID+ " FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        Integer idPry= 0;
        if(cursorPry.moveToFirst()){
            idPry=cursorPry.getInt(0);
        }
        cursorPry.close();
        Cursor cursor = null;
        Log.d("LAB05-MAIN","PROYECTO : _"+idPry.toString()+" - "+ _SQL_TAREAS_X_PROYECTO);
        cursor = db.rawQuery(_SQL_TAREAS_X_PROYECTO,new String[]{idPry.toString()});
        return cursor;
    }

    public void nuevaTarea(Tarea t){
        ContentValues nuevosValores = new ContentValues();
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,t.getMinutosTrabajados());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,t.getFinalizada());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO,t.getProyecto().getId());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,t.getResponsable().getId());
        db.insert(ProyectoDBMetadata.TABLA_TAREAS,ProyectoDBMetadata.TABLA_TAREAS,nuevosValores);
    }


    public void actualizarTarea(Tarea t){
        open(true);
        ContentValues nuevosValores = new ContentValues();
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,t.getMinutosTrabajados());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,t.getFinalizada());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO,t.getProyecto().getId());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,t.getResponsable().getId());
        db.update(ProyectoDBMetadata.TABLA_TAREAS,nuevosValores,"_ID="+t.getId(),null);
    }

    public void borrarTarea(Tarea t){

    }

    public List<Prioridad> listarPrioridades(){
        open(false);
        Cursor cursorListarDB = db.rawQuery("SELECT * FROM "+ProyectoDBMetadata.TABLA_PRIORIDAD,null);
        List<Prioridad> listaPrioridad = new ArrayList<Prioridad>();
        if(cursorListarDB.moveToFirst())
            do{
                Prioridad nuevo = new Prioridad();
                nuevo.setId(Integer.parseInt(cursorListarDB.getString(0)));
                nuevo.setPrioridad(cursorListarDB.getString(1));

                listaPrioridad.add(nuevo);
            }while(cursorListarDB.moveToNext());

        cursorListarDB.close();

        return listaPrioridad;
    }

        public List<Usuario> listarUsuarios()
    {
        open(false);
        Cursor cursorListarDB = db.rawQuery("SELECT * FROM "+ProyectoDBMetadata.TABLA_USUARIOS,null);
        List<Usuario> listaUsuario = new ArrayList<Usuario>();
        if(cursorListarDB.moveToFirst())
            do{
                Usuario nuevo = new Usuario();
                nuevo.setId(Integer.parseInt(cursorListarDB.getString(0)));
                nuevo.setNombre(cursorListarDB.getString(1));
                nuevo.setCorreoElectronico(cursorListarDB.getString(2));
                listaUsuario.add(nuevo);
            }while(cursorListarDB.moveToNext());

        cursorListarDB.close();

        return listaUsuario;
    }

    public List<Proyecto> listarProyecto()
    {
        open(false);
        Cursor cursorListarDB = db.rawQuery("SELECT * FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        List<Proyecto> listaProyecto = new ArrayList<Proyecto>();
        if(cursorListarDB.moveToFirst())
            do{
                Proyecto nuevo = new Proyecto();
                nuevo.setId(Integer.parseInt(cursorListarDB.getString(0)));
                nuevo.setNombre(cursorListarDB.getString(1));

                listaProyecto.add(nuevo);
            }while(cursorListarDB.moveToNext());

        cursorListarDB.close();

        return listaProyecto;
    }

    public List<Tarea> listarTareas(Integer idProyecto)
    {
        open(false);
        Cursor cursorListarDB = listaTareas(idProyecto);
        List<Tarea> listaTarea = new ArrayList<Tarea>();
        if(cursorListarDB.moveToFirst())
            do{
                /*
                Log.i("Prueba",cursorListarDB.getString(0)); //IDENTIFICADOR
                Log.i("Prueba",cursorListarDB.getString(1)); //DESCRIPCION
                Log.i("Prueba",cursorListarDB.getString(2)); //HORAS_PLANIFICADAS
                Log.i("Prueba",cursorListarDB.getString(3)); //MINUTOS_TRABAJDOS
                Log.i("Prueba",cursorListarDB.getString(4)); //FINALIZADA
                Log.i("Prueba",cursorListarDB.getString(5)); //ID_RESPONSABLE
                Log.i("Prueba",cursorListarDB.getString(6)); //ID_PRIORIDAD
                Log.i("Prueba",cursorListarDB.getString(7)); //ID_Proyecto*/
                /*Tarea nuevo = new Tarea(
                        Integer.parseInt(cursorListarDB.getString(0)),
                        Integer.parseInt(cursorListarDB.getString(2)),
                        Integer.parseInt(cursorListarDB.getString(3)),
                        Boolean.parseBoolean(cursorListarDB.getString(7)),
                        buscarProyecto(Integer.parseInt(cursorListarDB.getString(6))),
                        buscarPrioridad(Integer.parseInt(cursorListarDB.getString(4))),
                        buscarUsuario(Integer.parseInt(cursorListarDB.getString(5))));
                nuevo.setDescripcion(cursorListarDB.getString(1));*/
                Tarea nuevo = new Tarea();
                nuevo.setId(Integer.parseInt(cursorListarDB.getString(0)));
                nuevo.setDescripcion(cursorListarDB.getString(1));
                nuevo.setHorasEstimadas(Integer.parseInt(cursorListarDB.getString(2)));
                nuevo.setMinutosTrabajados(Integer.parseInt(cursorListarDB.getString(3)));
                nuevo.setPrioridad(buscarPrioridad(Integer.parseInt(cursorListarDB.getString(4))));
                nuevo.setResponsable(buscarUsuario(Integer.parseInt(cursorListarDB.getString(5))));
                switch(cursorListarDB.getString(6)){
                    case "Urgente":
                        nuevo.setProyecto(buscarProyecto(1));
                        break;
                    case "Alta":
                        nuevo.setProyecto(buscarProyecto(2));
                        break;
                    case "Media":
                        nuevo.setProyecto(buscarProyecto(3));
                        break;
                    case "Baja":
                        nuevo.setProyecto(buscarProyecto(4));
                        break;
                }

                //nuevo.setProyecto(buscarProyecto(Integer.parseInt(cursorListarDB.getString(6))));
                nuevo.setFinalizada(Boolean.parseBoolean(cursorListarDB.getString(7)));

                //listaTarea.add(nuevo);
            }while(cursorListarDB.moveToNext());

        cursorListarDB.close();

        return listaTarea;
    }

    public void finalizar(Integer idTarea){
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        mydb.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_id=?", new String[]{idTarea.toString()});
    }

    public List<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer desvioMaximoMinutos){
        // retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto)
        // que el tiempo planificado.
        // si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.
        return null;
    }

    public Prioridad buscarPrioridad(int ID){
        List<Prioridad> listaPrioridad = listarPrioridades();
        for(Prioridad i: listaPrioridad)
            if(i.getId()==ID)
                return i;
        return null;
    }

    public Proyecto buscarProyecto(int ID){
        List<Proyecto> listaProyecto = listarProyecto();
        for(Proyecto i: listaProyecto)
            if(i.getId()==ID)
                return i;
        return null;
    }

    public Tarea buscarTarea(int ID){
        List<Tarea> listaTarea = listarTareas(1);
        for(Tarea i: listaTarea)
            if(i.getId()==ID)
                return i;
        return null;
    }

    public Usuario buscarUsuario(int ID){
        List<Usuario> listaUsuario = listarUsuarios();
        for(Usuario i:listaUsuario)
            if(i.getId()==ID)
                return i;
        return null;
    }


}
