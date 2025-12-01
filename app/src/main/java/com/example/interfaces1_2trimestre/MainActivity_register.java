package com.example.interfaces1_2trimestre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity_register extends AppCompatActivity {
//AQUÍ VA LA INSTANCIA DE LA AUTENTICACIÓN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(
                config,
                getBaseContext().getResources().getDisplayMetrics()
        );

        setContentView(R.layout.activity_main_register);

        // 1) Activa persistencia via el singleton
        // ESTO NO SE PUEDE BORRAR DEL MAIN
        DataBase_Manager dbm = DataBase_Manager.getInstance();

        // 2) Test de conexión
        // ESTO NO SE PUEDE BORRAR DEL MAIN
        FirebaseDatabase.getInstance()
                .getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snap) {
                        Log.d("TEST_DB", "Root snapshot: " + snap.getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError err) {
                        Log.w("TEST_DB", "Error leyendo root", err.toException());
                    }
                });





        //***************** ESCRITURA DE LA BASE ********************************:
        /*// 3.1) Agregar un piso
        String pisoId12 = dbm.generarKey("pisos");
        Database_Piso pisoIntroducito_Jenri = new Database_Piso (pisoId12,
                "Calle prueba rama Jenri",
                "123456",
                "Pisito del jenri",
                1200.0);
        dbm.agregarPiso(pisoIntroducito_Jenri);
*/
        // 3.2) Agregar un usuario
       // String userId12 = dbm.generarKey("usuarios");
     //   DataBase_Usuario user = new DataBase_Usuario(userId12, "Jenri Mendez", null);
    //    dbm.agregarUsuario(user);

      /*  // 3.3) Agregar una puntuación y actualizar
        dbm.agregarPuntuacionYActualizarUsuario(userId12, pisoId12, 3);
*/



        /*
 // ******************************** BORRADO DE LA BASE ************************************************
       // 4) Borrado de un piso por ID (manualmente)
        String idPisoBorrar1 = "-OO3ACtp4sNIFIGapt9U"; // Reemplaza con el ID real a eliminar
        dbm.borrarPiso(idPisoBorrar1);
        Log.d("MAIN_MANUAL", "Solicitado borrado de piso ID: " + idPisoBorrar1);
        System.out.println("SE HA BORADO EL PISO1");

        String idPisoBorrar2 = "-OOgkchM6x1TaAbQQim5"; // Reemplaza con el ID real a eliminar
        dbm.borrarPiso(idPisoBorrar2);
        Log.d("MAIN_MANUAL", "Solicitado borrado de piso ID: " + idPisoBorrar2);
        System.out.println("SE HA BORADO EL PISO2");

        // 5) Borrado de un usuario por ID (manualmente)
        String idUsuarioBorrar1 = "-OO3ACu0r5cB3SkyVnUE";
        dbm.borrarUsuario(idUsuarioBorrar1);
        Log.d("MAIN_MANUAL", "USUARIO BORRADO CON ID : " + idUsuarioBorrar1);
        System.out.println("SE HA BORADO EL USUARIO1");

        String idUsuarioBorrar2 = "-OOgkchcOFxTijnCldg7";
        dbm.borrarUsuario(idUsuarioBorrar2);
        Log.d("MAIN_MANUAL", "USUARIO BORRADO CON ID : " + idUsuarioBorrar2);
        System.out.println("SE HA BORADO  EL USUARIO1");

        // 6) Borrado de puntuacion por ID (manualmente)
        String idPuntuacionBorrar1 = "-OO3ACu39A98gW9VSX4_";
        dbm.borrarPuntuacion(idPuntuacionBorrar1);
        Log.d("MAIN_MANUAL", "PUNTUACION BORRADA CON ID : " +  idPuntuacionBorrar1);
        System.out.println(" SE HA BORRADO LA PUNTUACION1 ");



        String idPuntuacionBorrar2 = "-OOgkchid7ScQqj5DFfZ";
        dbm.borrarPuntuacion(idPuntuacionBorrar2);
        Log.d("MAIN_MANUAL", "PUNTUACION BORRADA CON ID : " +  idPuntuacionBorrar2);
        System.out.println("SE HA BORRADO LA PUNTUACION 2a");     */



// ******************************** LECCURA DE LA BASE ************************************************
        // 1) Leer un piso por ID (ahora va directo al servidor)
        String pisoId = "-OPzAR-O4RIO5jIv0-bI";
        dbm.leerPiso(pisoId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                Database_Piso piso = ds.getValue(Database_Piso.class);
                System.out.println("Piso leído → " + piso);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError err) {
                System.err.println("Error en leerPiso: " + err.toException());
            }
        });

        // 2) Leer todos los pisos
        dbm.leerTodosPisos(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                System.out.println("---- leerTodosPisos ----");
                for (DataSnapshot ds : snap.getChildren()) {
                    Database_Piso p = ds.getValue(Database_Piso.class);
                    if (p != null) {
                        System.out.println(p.toString());
                        System.out.println(); // salto de línea entre pisos
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError err) {
                System.err.println("Error en leerTodosPisos: " + err.toException());
            }
        });

        // 3) Leer un usuario por ID
        String usuarioId = "-OPzAR-iGlfd8DBQz-2w";
        dbm.leerUsuario(usuarioId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                DataBase_Usuario u = ds.getValue(DataBase_Usuario.class);
                System.out.println("Usuario leído → " + u);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError err) {
                System.err.println("Error en leerUsuario: " + err.toException());
            }
        });

        // 4) Leer todos los usuarios
        dbm.leerTodosUsuarios(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                System.out.println("---- leerTodosUsuarios ----");
                for (DataSnapshot ds : snap.getChildren()) {
                    DataBase_Usuario u = ds.getValue(DataBase_Usuario.class);
                    if (u != null) {
                        System.out.println(u.toString());
                        System.out.println(); // salto de línea entre usuarios
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError err) {
                System.err.println("Error en leerTodosUsuarios: " + err.toException());
            }
        });

        // 5) Leer una puntuación por ID
        String puntuacionId = "-OPzAR-nMwiQoUB4PrQe";
        dbm.leerPuntuacion(puntuacionId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                DataBase_Puntuacion pt = ds.getValue(DataBase_Puntuacion.class);
                System.out.println("Puntuación leída → " + pt);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError err) {
                System.err.println("Error en leerPuntuacion: " + err.toException());
            }
        });
        // Usuario_static.setUserId(); meter autentifiación

    }
}
