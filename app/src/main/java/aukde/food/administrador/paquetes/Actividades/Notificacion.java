package aukde.food.administrador.paquetes.Actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Modelos.FCMBody;
import aukde.food.administrador.paquetes.Modelos.FCMResponse;
import aukde.food.administrador.paquetes.Providers.NotificationProvider;
import aukde.food.administrador.paquetes.Providers.TokenProvider;
import aukde.food.administrador.paquetes.Servicios.ForegroundServiceCronometro;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notificacion extends AppCompatActivity {

    private TextView ntfNumPedido,ntfNombre , ntfTelefono , ntfDirecion,ntfHora,ntfFecha , ntfGanancia , ntfAukdeliver;
    private Button btnListaPedido , btnCerrar;
    private String mExtraNumPedido;
    private String mExtraNombre;
    private String mExtraTelefono;
    private String mExtraDireccion;
    private String mExtraHora;
    private String mExtraFecha;
    private String mExtraGanancia;
    private String mExtraRepartidor;
    private MediaPlayer mediaPlayer;
    private FirebaseAuth mAuth;
    private final static int ID_SERVICIO = 99;
    private DatabaseReference mDatabase;
    private TokenProvider tokenProvider;
    private NotificationProvider notificationProvider;
    private Vibrator vibrator;
    TextView photo;
    String defaultPhoto = "https://firebasestorage.googleapis.com/v0/b/gestor-de-pedidos-aukdefood.appspot.com/o/fotoDefault.jpg?alt=media&token=f74486bf-432e-4af6-b114-baa523e1f801";
    long[] pattern = {400, 600, 100,300,100,150,100,75};
    private TextView textoCronometro;
    SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat("HH:mm:ss");
    String formatoHora = simpleDateFormatHora.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);
        photo = findViewById(R.id.pathPhotoCall);
        textoCronometro = findViewById(R.id.cronometro);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        tokenProvider = new TokenProvider();
        notificationProvider = new NotificationProvider();

        ntfNumPedido = findViewById(R.id.notifyNumPedido);
        ntfNombre = findViewById(R.id.notifyNombreCliente);
        ntfTelefono = findViewById(R.id.notifyTelefono);
        ntfDirecion = findViewById(R.id.notifyDireccion);
        ntfHora = findViewById(R.id.notifyHora);
        ntfFecha = findViewById(R.id.notifyFecha);
        ntfGanancia = findViewById(R.id.notifyGanancia);
        ntfAukdeliver = findViewById(R.id.nombreAukdeliver);
        btnListaPedido = findViewById(R.id.btnVerLista);
        btnCerrar = findViewById(R.id.btnCerrar);

        mExtraNumPedido = getIntent().getStringExtra("numPedido");
        mExtraNombre = getIntent().getStringExtra("nombre");
        mExtraTelefono = getIntent().getStringExtra("telefono");
        mExtraDireccion = getIntent().getStringExtra("direccion");
        mExtraHora = getIntent().getStringExtra("hora");
        mExtraFecha = getIntent().getStringExtra("fecha");
        mExtraGanancia = getIntent().getStringExtra("ganancia");
        //
        mExtraRepartidor = getIntent().getStringExtra("repartidor");

        ntfNumPedido.setText(mExtraNumPedido);
        ntfNombre.setText(mExtraNombre);
        ntfTelefono.setText(mExtraTelefono);
        ntfDirecion.setText(mExtraDireccion);
        ntfHora.setText(mExtraHora);
        ntfFecha.setText(mExtraFecha);
        ntfGanancia.setText(mExtraGanancia);
        ntfAukdeliver.setText(mExtraRepartidor);

        String Delivery =  ntfGanancia.getText().toString();
        Double doubleDelivery = Double.parseDouble(Delivery);
        Double finalGananciaDelivery ;

        if(doubleDelivery < 4.00){
            finalGananciaDelivery = doubleDelivery * 0.5;
            ntfGanancia.setText(obtieneDosDecimales(finalGananciaDelivery));
        }
        if(doubleDelivery >= 4.00 && doubleDelivery < 9.00){
            finalGananciaDelivery = doubleDelivery * 0.4;
            ntfGanancia.setText(obtieneDosDecimales(finalGananciaDelivery));
        }
        if(doubleDelivery >= 9.00){
            finalGananciaDelivery = doubleDelivery * 0.3;
            ntfGanancia.setText(obtieneDosDecimales(finalGananciaDelivery));
        }

        mediaPlayer = MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.setLooping(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                             WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                             WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                             WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        btnListaPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(Notificacion.this, ForegroundServiceCronometro.class);
                service.putExtra("inputExtra",textoCronometro.getText().toString());
                startService(service);
                hora();
                horaAdmin();
                estadoAceptadoAdmin();
                estadoAceptadoAukdeliver();
                finishAndRemoveTask();
                cerrar();
                sendAcceptNotification();
                vibrator.cancel();//cancela vibración
            }
        });

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoCanceladoAdmin();
                eliminarPedidoAukdeliver();
                sendCancelNotification();
                vibrator.cancel();//cancela vibración
                cerrar();
                finishAndRemoveTask();
            }
        });

        vibrator.vibrate(pattern, 0);
        mDatabase.child("Usuarios").child("Aukdeliver").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("foto")){
                    String Foto = dataSnapshot.child("foto").getValue().toString();
                    photo.setText(Foto);
                }
                else {
                    photo.setText(defaultPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ForegroundServiceCronometro.setUpdateListener(this);

    }

    private String obtieneDosDecimales(double valor) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        return format.format(valor);
    }



    private void cerrar() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
    }

    private void estadoCanceladoAdmin() {
        String dataNumPedido = ntfNumPedido.getText().toString();
        Query reference = FirebaseDatabase.getInstance().getReference().child("PedidosPorLlamada").child("pedidos").orderByChild("numPedido").equalTo(dataNumPedido);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    //Toast.makeText(DetallePedido.this, "Id : "+key, Toast.LENGTH_SHORT).show();
                    Map<String, Object> map = new HashMap<>();
                    map.put("estado", "Rechazado");
                    mDatabase.child("PedidosPorLlamada").child("pedidos").child(key).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void estadoAceptadoAdmin(){
        String dataNumPedido = ntfNumPedido.getText().toString();
        Query reference = FirebaseDatabase.getInstance().getReference().child("PedidosPorLlamada").child("pedidos").orderByChild("numPedido").equalTo(dataNumPedido);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    //Toast.makeText(DetallePedido.this, "Id : "+key, Toast.LENGTH_SHORT).show();
                    Map<String, Object> map = new HashMap<>();
                    map.put("estado", "En proceso");
                    mDatabase.child("PedidosPorLlamada").child("pedidos").child(key).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void eliminarPedidoAukdeliver() {
        String dataNumPedido = ntfNumPedido.getText().toString();
        Query reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Aukdeliver").child(mAuth.getUid()).child("pedidos").orderByChild("numPedido").equalTo(dataNumPedido);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    mDatabase.child("Usuarios").child("Aukdeliver").child(mAuth.getUid()).child("pedidos").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.error(Notificacion.this, "Pedido rechazado!", Toast.LENGTH_LONG, true).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.info(Notificacion.this, "Error al rechazar pedido", Toast.LENGTH_LONG, true).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendCancelNotification() {
        final String numPedNotify = ntfNumPedido.getText().toString();
        final String dataRepartidor = ntfAukdeliver.getText().toString();
        String[] admins = {"nS8J0zEj53OcXSugQsXIdMKUi5r1", "UnwAmhwRzmRLn8aozWjnYFOxYat2",
                "9sjTQMmowxWYJGTDUY98rAR2jzB3"};

        for (int i = 0; i < admins.length; i++) {
        tokenProvider.getToken(admins[i]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    String ruta = photo.getText().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "Pedido #" + numPedNotify);
                    map.put("body", "El repartidor " + dataRepartidor + "\nha RECHAZADO el pedido!");
                    map.put("path", ruta);
                    FCMBody fcmBody = new FCMBody(token, "high", map);
                    notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() != null) {
                                if (response.body().getSuccess() == 1) {
                                    //Toast.makeText(RealizarPedido.this, "Notificación enviada", Toast.LENGTH_LONG).show();
                                } else {
                                    Toasty.error(Notificacion.this, "No se envió la notificación", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toasty.error(Notificacion.this, "No se envió la notificación", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error", "Error encontrado" + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(Notificacion.this, "No existe token se sesión", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    }

    private void sendAcceptNotification(){
        final String numPedNotify = ntfNumPedido.getText().toString();
        final String dataRepartidor = ntfAukdeliver.getText().toString();
        String[] admins = {"nS8J0zEj53OcXSugQsXIdMKUi5r1", "UnwAmhwRzmRLn8aozWjnYFOxYat2",
                "9sjTQMmowxWYJGTDUY98rAR2jzB3"};

        for (int i = 0; i < admins.length; i++) {
            tokenProvider.getToken(admins[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String token = dataSnapshot.child("token").getValue().toString();
                        String ruta = photo.getText().toString();
                        Map<String, String> map = new HashMap<>();
                        map.put("title", "Pedido #" + numPedNotify);
                        map.put("body", "El repartidor " + dataRepartidor + "\nha ACEPTADO el pedido!");
                        map.put("path", ruta);
                        FCMBody fcmBody = new FCMBody(token, "high", map);
                        notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().getSuccess() == 1) {
                                        //Toast.makeText(RealizarPedido.this, "Notificación enviada", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toasty.error(Notificacion.this, "No se envió la notificación", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toasty.error(Notificacion.this, "No se envió la notificación", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {
                                Log.d("Error", "Error encontrado" + t.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(Notificacion.this, "No existe token se sesión", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void actualizarCronometro(double tiempo) {
        int s = (int)tiempo%60;
        int m = (int)tiempo/60;
        int h = (int)(tiempo/(60*60))%24;
        textoCronometro.setText(String.format("%02d:%02d:%02d", h,m,s));
        sendTime();
    }

    private void sendTime(){
        mDatabase.child("Tiempo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String , Object> tiempo = new HashMap<>();
                tiempo.put("tiempo",textoCronometro.getText().toString());
                mDatabase.child("Tiempo").child(mAuth.getUid()).updateChildren(tiempo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void hora(){
        String dataNumPedido = ntfNumPedido.getText().toString();
        Query reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Aukdeliver").child(mAuth.getUid()).child("pedidos").orderByChild("numPedido").equalTo(dataNumPedido);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    final String key = childSnapshot.getKey();
                    mDatabase.child("Usuarios").child("Aukdeliver").child(mAuth.getUid()).child("pedidos").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String , Object> map = new HashMap<>();
                            map.put("tiempo1",formatoHora);
                            mDatabase.child("Usuarios").child("Aukdeliver").child(mAuth.getUid()).child("pedidos").child(key).child("tiempo").updateChildren(map);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void horaAdmin(){
        String dataNumPedido = ntfNumPedido.getText().toString();
        Query reference = FirebaseDatabase.getInstance().getReference().child("PedidosPorLlamada").child("pedidos").orderByChild("numPedido").equalTo(dataNumPedido);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                   final String key = childSnapshot.getKey();
                    //Toast.makeText(DetallePedido.this, "Id : "+key, Toast.LENGTH_SHORT).show();
                    mDatabase.child("PedidosPorLlamada").child("pedidos").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("tiempo1",formatoHora);
                            mDatabase.child("PedidosPorLlamada").child("pedidos").child(key).child("tiempo").updateChildren(map);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void estadoAceptadoAukdeliver(){
        String dataNumPedido = ntfNumPedido.getText().toString();
        Query reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Aukdeliver").child(mAuth.getUid()).child("pedidos").orderByChild("numPedido").equalTo(dataNumPedido);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    final String key = childSnapshot.getKey();
                            Map<String, Object> map = new HashMap<>();
                            map.put("estado", "En proceso");
                            mDatabase.child("Usuarios").child("Aukdeliver").child(mAuth.getUid()).child("pedidos").child(key).updateChildren(map);
                        }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer !=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer !=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.release();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer !=null){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer !=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }
    }
}