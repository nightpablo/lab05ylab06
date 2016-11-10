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
        open(true); //Abrimos la bd para escritura
        ContentValues nuevosValores = new ContentValues();
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,t.getMinutosTrabajados());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,t.getFinalizada());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO,t.getProyecto().getId());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,t.getResponsable().getId());
        db.insert(ProyectoDBMetadata.TABLA_TAREAS,ProyectoDBMetadata.TABLA_TAREAS,nuevosValores);
        close(); //Cerramos
    }


    public void actualizarTarea(Tarea t){
        open(true); //Abrimos la bd para escritura
        ContentValues nuevosValores = new ContentValues();
        // Para el botón Editar
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD,t.getPrioridad().getId());
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE,t.getResponsable().getId());

        //Para el botón Trabajar
        nuevosValores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,t.getMinutosTrabajados());

        db.update(ProyectoDBMetadata.TABLA_TAREAS,nuevosValores,"_ID="+t.getId(),null);

        close(); //Cerramos
    }

    public void borrarTarea(Tarea t){
        open(true); //Abrimos la bd para escritura
        db.delete(ProyectoDBMetadata.TABLA_TAREAS,"_ID="+t.getId(),null);
        close(); //Cerramos
    }

    public List<Prioridad> listarPrioridades(){
        open(false); //Abrimos la bd para lectura
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
        open(false); //Abrimos la bd para lectura
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
        open(false); //Abrimos la bd para lectura
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
        open(false); //Abrimos la bd para lectura
        Cursor cursorListarDB = listaTareas(idProyecto);
        List<Tarea> listaTarea = new ArrayList<Tarea>();
        if(cursorListarDB.moveToFirst())
            do{

                Tarea nuevo = new Tarea();
                nuevo.setId(Integer.parseInt(cursorListarDB.getString(0)));
                nuevo.setDescripcion(cursorListarDB.getString(1));
                nuevo.setHorasEstimadas(Integer.parseInt(cursorListarDB.getString(2)));
                nuevo.setMinutosTrabajados(Integer.parseInt(cursorListarDB.getString(3)));
                nuevo.setResponsable(buscarUsuario(Integer.parseInt(cursorListarDB.getString(7))));

                switch(cursorListarDB.getString(6)){
                    case "Urgente":
                        nuevo.setPrioridad(buscarPrioridad(1));
                        break;
                    case "Alta":
                        nuevo.setPrioridad(buscarPrioridad(2));
                        break;
                    case "Media":
                        nuevo.setPrioridad(buscarPrioridad(3));
                        break;
                    case "Baja":
                        nuevo.setPrioridad(buscarPrioridad(4));
                        break;
                }
                nuevo.setProyecto(buscarProyecto(1));
                nuevo.setFinalizada(Integer.parseInt(cursorListarDB.getString(4))== 1);

                listaTarea.add(nuevo);
            }while(cursorListarDB.moveToNext());

        cursorListarDB.close();

        return listaTarea;
    }

    public void finalizar(Integer idTarea){
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        //SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        open(true); //Abrimos la bd para escritura
        db.update(ProyectoDBMetadata.TABLA_TAREAS,valores,"_ID="+idTarea,null);
        close(); //Cerramos
    }

    public List<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer desvioMaximoMinutos){
        // retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto)
        // que el tiempo planificado.
        // si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.
        List<Tarea> listatarea = listarTareas(1);
        List<Tarea> listaretorno = new ArrayList<Tarea>();
        for(Tarea i:listatarea){
            if(!soloTerminadas) {
                if (Math.abs(i.getMinutosTrabajados() - i.getHorasEstimadas() * 60) < desvioMaximoMinutos)
                    listaretorno.add(i);
            }
            else{
                if(i.getFinalizada())
                    if (Math.abs(i.getMinutosTrabajados() - i.getHorasEstimadas() * 60) < desvioMaximoMinutos)
                        listaretorno.add(i);
            }
        }
        Log.i("Result","Entró");
        return listaretorno;
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
            if(i.getId()==ID) {

                /*Log.i("Retorno","ID:"+i.getId()+
                        ". Descripcion:"+i.getDescripcion()+
                        ". Finalizada:"+i.getFinalizada()+
                ". Horas Estimadas:"+i.getHorasEstimadas()+
                        ". Minutos Trabajados:"+i.getMinutosTrabajados()+
                ". ID Prioridad:"+i.getPrioridad().getId()+
                        ". ID Responsable:"+i.getResponsable().getId());*/
                return i;
            }
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
