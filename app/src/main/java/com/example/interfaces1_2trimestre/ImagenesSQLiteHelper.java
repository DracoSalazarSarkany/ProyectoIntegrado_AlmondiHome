package com.example.interfaces1_2trimestre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ImagenesSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "imagenes.db";
    private static final int DATABASE_VERSION = 2; // Cambié versión para actualizar BD

    private static final String TABLE_IMAGENES = "imagenes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PISO_ID = "piso_id";
    private static final String COLUMN_RUTA = "ruta";

    // Constantes para tabla usuarios recordados
    private static final String TABLE_USUARIOS_RECORDADOS = "usuarios_recordados";
    private static final String COLUMN_USR_ID = "id";        // uid Firebase
    private static final String COLUMN_USR_EMAIL = "email";
    private static final String COLUMN_USR_PASSWORD = "password";

    public ImagenesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGENES_TABLE = "CREATE TABLE " + TABLE_IMAGENES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PISO_ID + " TEXT,"
                + COLUMN_RUTA + " TEXT"
                + ")";
        db.execSQL(CREATE_IMAGENES_TABLE);

        String CREATE_USUARIOS_RECORDADOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS_RECORDADOS + "("
                + COLUMN_USR_ID + " TEXT PRIMARY KEY,"
                + COLUMN_USR_EMAIL + " TEXT,"
                + COLUMN_USR_PASSWORD + " TEXT"
                + ")";
        db.execSQL(CREATE_USUARIOS_RECORDADOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Actualizar esquema para usuarios recordados si sube versión
        if (oldVersion < 2) {
            String CREATE_USUARIOS_RECORDADOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS_RECORDADOS + "("
                    + COLUMN_USR_ID + " TEXT PRIMARY KEY,"
                    + COLUMN_USR_EMAIL + " TEXT,"
                    + COLUMN_USR_PASSWORD + " TEXT"
                    + ")";
            db.execSQL(CREATE_USUARIOS_RECORDADOS_TABLE);
        }
        // No borres tabla imagenes para no perder datos
    }

    // Métodos tabla imagenes ya existentes

    public void insertarImagen(String pisoId, String rutaImagen) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertSQL = "INSERT INTO " + TABLE_IMAGENES + "(" + COLUMN_PISO_ID + "," + COLUMN_RUTA + ") VALUES (?, ?)";
        db.execSQL(insertSQL, new Object[]{pisoId, rutaImagen});
        db.close();
    }

    public List<String> obtenerImagenesPorPiso(String pisoId) {
        List<String> rutas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_IMAGENES,
                new String[]{COLUMN_RUTA},
                COLUMN_PISO_ID + " = ?",
                new String[]{pisoId},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String ruta = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RUTA));
                rutas.add(ruta);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return rutas;
    }

    public void borrarTodasLasImagenes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGENES, null, null);
        db.close();
    }

    public void borrarImagenesPorPiso(String pisoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGENES, COLUMN_PISO_ID + " = ?", new String[]{pisoId});
        db.close();
    }

    // NUEVOS MÉTODOS PARA USUARIOS RECORDADOS

    public void guardarUsuarioRecordado(String id, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USR_ID, id);
        values.put(COLUMN_USR_EMAIL, email);
        values.put(COLUMN_USR_PASSWORD, password);

        // Actualizar si existe, si no insertar
        int updated = db.update(TABLE_USUARIOS_RECORDADOS, values, COLUMN_USR_ID + " = ?", new String[]{id});
        if (updated == 0) {
            db.insert(TABLE_USUARIOS_RECORDADOS, null, values);
        }
        db.close();
    }

    public UsuarioRecordado obtenerUsuarioRecordado() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS_RECORDADOS,
                new String[]{COLUMN_USR_ID, COLUMN_USR_EMAIL, COLUMN_USR_PASSWORD},
                null, null, null, null, null);

        UsuarioRecordado usuario = null;
        if (cursor != null && cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USR_ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USR_EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USR_PASSWORD));
            usuario = new UsuarioRecordado(id, email, password);
            cursor.close();
        }
        db.close();
        return usuario;
    }

    public void borrarUsuarioRecordado() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USUARIOS_RECORDADOS, null, null);
        db.close();
    }

    // Clase auxiliar para usuario recordado
    public static class UsuarioRecordado {
        public final String id;
        public final String email;
        public final String password;

        public UsuarioRecordado(String id, String email, String password) {
            this.id = id;
            this.email = email;
            this.password = password;
        }
    }
}
