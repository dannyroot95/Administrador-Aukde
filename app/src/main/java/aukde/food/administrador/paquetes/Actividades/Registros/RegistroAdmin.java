package aukde.food.administrador.paquetes.Actividades.Registros;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Actividades.Logins.LoginAdmin;
import aukde.food.administrador.paquetes.Inclusiones.MiToolbar;
import aukde.food.administrador.paquetes.Modelos.Administrador;
import aukde.food.administrador.paquetes.Providers.AdminProvider;
import aukde.food.administrador.paquetes.Providers.AuthProviders;
import es.dmoral.toasty.Toasty;


public class RegistroAdmin extends AppCompatActivity {

    private TextInputEditText edtNombres, edtApellidos, edtDni, edtTelefono,
            edtEmail, edtPassword, edtRepetirPass, edtClaveAuth;

    private ProgressDialog mDialog;
    Button mButtonRegistro;
    AuthProviders mAuthProviders;
    AdminProvider mAdminProvider;
    private Vibrator vibrator;
    long tiempo = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_admin);
        MiToolbar.Mostrar(this,"Registro Administrador",true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mAuthProviders = new AuthProviders();
        mAdminProvider = new AdminProvider();
        mDialog = new ProgressDialog(this);
        mButtonRegistro = findViewById(R.id.btnRegistrarse);

        edtNombres = findViewById(R.id.AdminNombres);
        edtApellidos = findViewById(R.id.AdminApellidos);
        edtDni = findViewById(R.id.AdminDNI);
        edtTelefono = findViewById(R.id.AdminTeléfono);
        edtEmail = findViewById(R.id.AdminEmail);
        edtPassword = findViewById(R.id.AdminEdtPassword);
        edtRepetirPass = findViewById(R.id.AdminRepetirContrasena);
        edtClaveAuth = findViewById(R.id.AdminClaveAutorización);


        mButtonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(tiempo);
                ClickRegistro();
            }
        });
    }

    private void ClickRegistro() {

        final String nombres = edtNombres.getText().toString();
        final String apellidos = edtApellidos.getText().toString();
        final String dni = edtDni.getText().toString();
        final String telefono = edtTelefono.getText().toString();
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();
        final String repetirPass = edtRepetirPass.getText().toString();
        final String ClaveAuth = edtClaveAuth.getText().toString();

        if(!nombres.isEmpty() && !apellidos.isEmpty() && !dni.isEmpty() && !telefono.isEmpty() && !email.isEmpty()
               && !password.isEmpty() && !repetirPass.isEmpty() && !ClaveAuth.isEmpty() ){
            mDialog.show();
            mDialog.setMessage("Registrando usuario...");
            if(password.length()>=6){
                if(password.equals(repetirPass)){
                    if(ClaveAuth.equals("AUK2020+*") || ClaveAuth.equals("WRZ20@") || ClaveAuth.equals("GOGOOL*")){
                        registrar(nombres,apellidos,dni,telefono,email,password);
                    }
                    else {
                        mDialog.dismiss();
                        Toasty.error(RegistroAdmin.this, "Clave de autorización inválida", Toast.LENGTH_LONG,true).show();
                    }
                }
                else {
                    mDialog.dismiss();
                    Toasty.warning(RegistroAdmin.this,"Las contraseñas no coinciden",Toast.LENGTH_LONG,true).show();
                }
            }
            else {
                mDialog.dismiss();
                Toasty.info(RegistroAdmin.this,"La contraseña debe ser mayor a 6 caracteres",Toast.LENGTH_LONG,true).show();
            }
        }

        else {
            Toasty.info(RegistroAdmin.this,"Complete los todos campos",Toast.LENGTH_LONG,true).show();
        }

    }


    private void registrar(final String nombres, final String apellidos, final String dni, final String telefono, final String email, String password) {

      mAuthProviders.Registro(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

              String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
              Administrador administrador = new Administrador(id,nombres,apellidos,dni,telefono,email);
              mapear(administrador);

          }
      });
    }

    void mapear(Administrador administrador){

        mAdminProvider.Mapear(administrador).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDialog.dismiss();
                    logout();
                }
                else {
                    mDialog.dismiss();
                    Toasty.error(RegistroAdmin.this, "No se pudo crear un nuevo usuario", Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }

    void logout() {
        Intent intent = new Intent(getApplicationContext(), LoginAdmin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("dato","valor");
        startActivity(intent);
        finish();
        mAuthProviders.Logout();
    }


}