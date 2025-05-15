package com.example.app_practica.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//public class DbHelper extends SQLiteOpenHelper {
//
//    private static final int DATABASE_VERSION = 1;
//    private static final String DATABASE_NAME = "productos.db";
//    public static final String TABLE_PRODUCTOS = "t_productos";
//
//    public DbHelper(@Nullable Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PRODUCTOS + " (" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "nombre TEXT, " +
//                "precioActual REAL, " +
//                "rating REAL, " +
//                "numResenias INTEGER, " +
//                "enStock INTEGER, " +
//                "descripcion TEXT, " +
//                "especificacionesTecnicas TEXT" +
//                ");");
//    }
//
//
//}
