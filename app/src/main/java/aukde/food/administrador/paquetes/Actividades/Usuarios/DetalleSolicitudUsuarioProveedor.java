package aukde.food.administrador.paquetes.Actividades.Usuarios;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Actividades.Pedidos.DetallePedido;
import aukde.food.administrador.paquetes.Modelos.UsuarioProveedor;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import aukde.food.administrador.paquetes.Providers.AuthProviders;
import es.dmoral.toasty.Toasty;

public class DetalleSolicitudUsuarioProveedor extends AppCompatActivity {

   EditText name,lastName,dni,mobile,email,password,negocio,direccion,ruc,vende,delivery;
   String gender;
   Double lat;
   Double lon;
   String key;
   private ProgressDialog mDialog;
   Button btnConfirm,btnUpdate;
   FirebaseFirestore db = FirebaseFirestore.getInstance();
   Query mReference = FirebaseDatabase.getInstance().getReference().child("SolicitudSocio");
   DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("SolicitudSocio");
   int profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_usuario_proveedor);

        mDialog = new ProgressDialog(this,R.style.ThemeOverlay);
        name = findViewById(R.id.et_first_name);
        lastName = findViewById(R.id.et_last_name);
        dni = findViewById(R.id.et_dni);
        mobile = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        negocio = findViewById(R.id.et_store);
        direccion = findViewById(R.id.et_address);
        ruc = findViewById(R.id.et_ruc);
        vende = findViewById(R.id.et_type_product);
        delivery = findViewById(R.id.et_delivery);

        btnConfirm = findViewById(R.id.btnConfirmar);
        btnUpdate = findViewById(R.id.btnEditar);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final UsuarioProveedor provider = (UsuarioProveedor) bundle.getSerializable("dataUser");
        ArrayList<String> data;
        data = new ArrayList<>();
        data.add(provider.getGender());
        data.add(provider.getFirstName());
        data.add(provider.getLastName());
        data.add(provider.getDni());
        data.add(String.valueOf(provider.getMobile()));
        data.add(provider.getEmail());
        data.add(provider.getPassword());
        data.add(provider.getName_store());
        data.add(provider.getAddress());
        data.add(provider.getRuc());
        data.add(provider.getType_product());
        data.add(provider.getDelivery());
        data.add(provider.getPassword());
        profile = provider.getProfileCompleted();
        lat = provider.getLatitude();
        lon = provider.getLongitude();

        verifyButton();

        gender = data.get(0);
        name.setText(data.get(1));
        lastName.setText(data.get(2));
        dni.setText(data.get(3));
        mobile.setText(data.get(4));
        email.setText(data.get(5));
        password.setText(data.get(6));
        negocio.setText(data.get(7));
        direccion.setText(data.get(8));
        ruc.setText(data.get(9));
        vende.setText(data.get(10));
        delivery.setText(data.get(11));

        mReference.orderByChild("email").equalTo(email.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        key = childSnapshot.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderCancel = new AlertDialog.Builder(DetalleSolicitudUsuarioProveedor.this,R.style.ThemeOverlay);
                builderCancel.setTitle("Hey!");
                builderCancel.setCancelable(false);
                builderCancel.setIcon(R.drawable.ic_storeblack);
                builderCancel.setMessage("Deseas confirmar este socio..? ");
                builderCancel.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registrarUsuario();
                    }
                });
                builderCancel.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderCancel.create();
                builderCancel.show();

            }
        });

    }

    private void registrarUsuario(){

        mDialog.show();
        mDialog.setCancelable(false);
        mDialog.setMessage("Registrando...");

        AuthProviders mAuth = new AuthProviders();
        final String mail = email.getText().toString();
        String clave = password.getText().toString();
        final Long telefono = Long.parseLong(mobile.getText().toString());

        mAuth.Registro(mail,clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    final FirebaseUser mUser = task.getResult().getUser();

                    assert mUser != null;
                    UsuarioProveedor proveedor = new UsuarioProveedor(
                            mUser.getUid(),
                            name.getText().toString(),
                            lastName.getText().toString(),
                            dni.getText().toString(),
                            telefono,
                            ruc.getText().toString(),
                            gender,
                            mail,
                            "",
                            vende.getText().toString(),
                            delivery.getText().toString(),
                            negocio.getText().toString(),
                            "provider",
                            direccion.getText().toString(),
                            lat,lon,"",1
                    );

                    db.collection("users").document(proveedor.getId()).set(proveedor, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Map<String,Object> map = new HashMap<>();
                            map.put("profileCompleted",1);
                            map.put("id",mUser.getUid());
                            mData.child(key).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendSMS();
                                    mDialog.dismiss();
                                    finish();
                                    Toasty.success(DetalleSolicitudUsuarioProveedor.this, "Usuario Creado!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toasty.error(DetalleSolicitudUsuarioProveedor.this, "No se pudo crear el usuario!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    mDialog.dismiss();
                    Toasty.error(DetalleSolicitudUsuarioProveedor.this, "No se pudo crear el usuario!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void verifyButton(){
        if (profile == 0){
            btnConfirm.setVisibility(View.VISIBLE);
        }
        else {
            btnUpdate.setVisibility(View.VISIBLE);
        }
    }

    private void sendSMS(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ThreadSMS();
            }
        });
    }

    private void ThreadSMS(){
        String phone = mobile.getText().toString();
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String content = "{\"messages\": [{\"source\": \"mashape\",\"from\": \"Test\",\"body\": \"Su cuenta en AUKDE ha sido Activada!\",\"to\": \"+51"+phone+"\",\"schedule\": \"1452244637\",\"custom_string\": \"Su cuenta en AUKDE ha sido Activada!\" }]}";
        RequestBody body = RequestBody.create(mediaType, content);

    Request request = new Request.Builder()
            .url("https://clicksend.p.rapidapi.com/sms/send")
            .post(body)
            .addHeader("content-type", "application/json")
            .addHeader("authorization", "Basic Y29udGFjdG9AY29uZXhmaW4ub3JnOkZpbmFuemFzaW50ZWxpZ2VudGVzMTArKg==")
            .addHeader("x-rapidapi-key", "a79f8e5c23mshc421c059f1161bfp1c0b26jsn40bee7f91d53")
            .addHeader("x-rapidapi-host", "clicksend.p.rapidapi.com")
            .build();

        try {
            Response response = client.newCall(request).execute();
            Toast.makeText(this, "SMS enviado!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "ERROR!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}