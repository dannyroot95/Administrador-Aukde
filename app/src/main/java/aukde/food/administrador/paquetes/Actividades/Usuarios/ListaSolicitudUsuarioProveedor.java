package aukde.food.administrador.paquetes.Actividades.Usuarios;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Adaptadores.AdapterSolicitudUsuarioProveedor;
import aukde.food.administrador.paquetes.Inclusiones.MiToolbar;
import aukde.food.administrador.paquetes.Modelos.UsuarioProveedor;

public class ListaSolicitudUsuarioProveedor extends AppCompatActivity {

    DatabaseReference mDatabaseReference;
    ArrayList<UsuarioProveedor> listSocios;
    RecyclerView recyclerViewSocios;
    SearchView searchViewSocios;
    AdapterSolicitudUsuarioProveedor adapterSolictudProveedor;
    LinearLayoutManager linearLayoutManager;
    private ProgressDialog mDialogActualizeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_solicitud_usuario_proveedor);

        MiToolbar.Mostrar(this,"Lista de socios",true);
        mDialogActualizeData = new ProgressDialog(this,R.style.MyAlertDialogData);
        mDialogActualizeData.setCancelable(false);
        mDialogActualizeData.show();
        mDialogActualizeData.setContentView(R.layout.dialog_data);
        mDialogActualizeData.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SolicitudSocio");
        recyclerViewSocios = findViewById(R.id.recyclerSolicitudRegistroProveedor);
        searchViewSocios= findViewById(R.id.searchSocio);
        searchViewSocios.setBackgroundColor(Color.WHITE);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSocios.setLayoutManager(linearLayoutManager);
        listSocios = new ArrayList<>();
        adapterSolictudProveedor = new AdapterSolicitudUsuarioProveedor(listSocios);
        recyclerViewSocios.setAdapter(adapterSolictudProveedor);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    listSocios.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        UsuarioProveedor pd = snapshot.getValue(UsuarioProveedor.class);
                        listSocios.add(pd);
                    }
                    Collections.reverse(listSocios);
                    adapterSolictudProveedor.notifyDataSetChanged();
                    mDialogActualizeData.dismiss();
                }
                else {
                    Toast.makeText(ListaSolicitudUsuarioProveedor.this, "No se encontraron socios registrados", Toast.LENGTH_SHORT).show();
                    mDialogActualizeData.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListaSolicitudUsuarioProveedor.this, "Error de base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        searchViewSocios.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {
                buscar(texto);
                return true;
            }
        });

    }

    private void buscar(String texto) {
        ArrayList<UsuarioProveedor> lista = new ArrayList<>();
        for(UsuarioProveedor object : listSocios){
            if (object.getFirstName().toLowerCase().contains(texto.toLowerCase())
                    || object.getName_store().toLowerCase().contains(texto.toLowerCase())
                   )
            {
                lista.add(object);
            }
            else {
            }
        }
        AdapterSolicitudUsuarioProveedor adapter = new AdapterSolicitudUsuarioProveedor(lista);
        recyclerViewSocios.setAdapter(adapter);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}