package com.example.app_practica.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

//public class dbProductos extends DbHelper {
//
//    Context context;
//    public dbProductos(@Nullable Context context) {
//        super(context);
//        this.context = context;
//    }
//
//    public long insertaProducto(String nombre, double precioActual, float rating, int numResenias, boolean enStock, String descripcion, String especificacionesTecnicas){
//
//        long id = 0;
//
//        try {
//            DbHelper dbHelper = new DbHelper(context);
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put("nombre", nombre);
//            values.put("precioActual", precioActual);
//            values.put("rating", rating);
//            values.put("numResenias", numResenias);
//            values.put("enStock", enStock ? 1 : 0);
//            values.put("descripcion", descripcion);
//            values.put("especificacionesTecnicas", especificacionesTecnicas);
//
//            id = db.insert(TABLE_PRODUCTOS, null, values);
//        } catch (Exception ex){
//            ex.toString();
//        }
//        return id;
//    }
//}
