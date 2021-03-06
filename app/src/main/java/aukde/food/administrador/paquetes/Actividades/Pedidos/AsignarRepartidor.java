package aukde.food.administrador.paquetes.Actividades.Pedidos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Modelos.FCMBody;
import aukde.food.administrador.paquetes.Modelos.FCMResponse;
import aukde.food.administrador.paquetes.Providers.NotificationProvider;
import aukde.food.administrador.paquetes.Providers.PedidoProvider;
import aukde.food.administrador.paquetes.Providers.TokenProvider;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsignarRepartidor extends AppCompatActivity{

    private ProgressDialog mDialog;
    SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat("HH:mm");
    SimpleDateFormat simpleDateFormatFecha = new SimpleDateFormat("dd/MM/yyy");
    int dia, mes, year, horaRel, minutoRel;
    String formatoHora = simpleDateFormatHora.format(new Date());
    String formatoFecha = simpleDateFormatFecha.format(new Date());

    TextView horaPedido, fechaPedido, fechaEntrega , horaEntrega
            , precioProductoTotal, precioNetoTotal, vuelto;

    private TextView edtNumPedido,textSocio,txtProducto,txtDescripcion,txtPrecioUnitario,txtDelivery , edtCantidad , txtCantidad ,
            txtPtotal , txtNeto , txtGananciaPorDelivery , txtPrecioComisionProducto , txtNetoComision , IDpedido;


    public EditText  edtMontoCliente , edtNombreCliente, edtTelefono , edtDireccion  , edtReferencia;
    PedidoProvider mpedidoProvider;

    DatabaseReference mUsuarioAukdeliver;
    private DatabaseReference pedidosActualizadoAdmin;
    private DatabaseReference mDatabase;
    private DatabaseReference pedidoParaAukdeliver;
    Spinner mSpinner;
    Button mFloatingButton;

    TextView estado ;
    TextView txtEncargado , idAukdeliver;
    String stEncargado = "";
    TextView latitud,logitud;
    TextView RepartidorActual;

    private Dialog aukdeliverOcupado;
    private Button cerrarPopup;
    private ImageView cerrarImg;

    private NotificationProvider notificationProvider;
    private TokenProvider tokenProvider;
    private FirebaseAuth mAuth ;

    Bundle numeroPedido;
    private Vibrator vibrator;
    long tiempo = 100;
    //----------------------------

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignar_repartidor);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        horaPedido = findViewById(R.id.horaPedido);
        fechaPedido = findViewById(R.id.fechaPedido);
        horaEntrega = findViewById(R.id.horaEntrega);
        fechaEntrega = findViewById(R.id.fechaEntrega);
        horaEntrega.setText(formatoHora);
        fechaEntrega.setText(formatoFecha);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vuelto = findViewById(R.id.vuelto);
        horaPedido.setText(formatoHora);
        fechaPedido.setText(formatoFecha);
        edtNombreCliente = findViewById(R.id.nombreCliente);
        edtNumPedido = findViewById(R.id.numPedido);

        aukdeliverOcupado = new Dialog(this);

        IDpedido = findViewById(R.id.idEditPedido);
        numeroPedido  = getIntent().getExtras();
        String stExtraNumPedido = numeroPedido.getString("numPedido");
        edtNumPedido.setText(stExtraNumPedido);
        // nuevo

        textSocio = findViewById(R.id.lsSocio);
        txtProducto = findViewById(R.id.lsProducto);
        txtDescripcion = findViewById(R.id.lsDescripcion);
        txtPrecioUnitario = findViewById(R.id.lsPUnitario);
        txtDelivery = findViewById(R.id.lsPDelivery);
        txtCantidad = findViewById(R.id.lsCant);
        txtPtotal = findViewById(R.id.lsPTotal);
        txtNeto = findViewById(R.id.precioProductoTotal);
        txtGananciaPorDelivery = findViewById(R.id.gananciaDelivery);
        txtPrecioComisionProducto = findViewById(R.id.lsComision);
        txtNetoComision = findViewById(R.id.gananciaAdmin);
        precioNetoTotal = findViewById(R.id.neto);
        RepartidorActual = findViewById(R.id.txtRepartidorActual);

        obtenerPedido();

        //---------------
        mDialog = new ProgressDialog(this,R.style.ThemeOverlay);
        edtMontoCliente = findViewById(R.id.montoPagarcliente);
        precioProductoTotal = findViewById(R.id.precioProductoTotal);
        edtTelefono = findViewById(R.id.telefonoCliente);
        edtDireccion = findViewById(R.id.direcionCliente);
        edtDireccion.setEnabled(false);
        edtReferencia = findViewById(R.id.referenciaCliente);
        txtEncargado = findViewById(R.id.txtRepartidor);
        idAukdeliver = findViewById(R.id.txtIdAukdeliver);
        pedidoParaAukdeliver = FirebaseDatabase.getInstance().getReference();
        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();

        latitud = findViewById(R.id.txtLatitud);
        logitud = findViewById(R.id.txtLongitud);

        // intentExtra numero de pedido
        mpedidoProvider = new PedidoProvider();
        mSpinner = findViewById(R.id.spinnerAukdeliver);
        estado = findViewById(R.id.txtEstado);

        mUsuarioAukdeliver = FirebaseDatabase.getInstance().getReference();
        pedidosActualizadoAdmin = FirebaseDatabase.getInstance().getReference("PedidosPorLlamada").child("pedidos");

        obtenerUsuarioAukdeliver();
        mFloatingButton = findViewById(R.id.floatRegister);
        fechaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickFecha();
            }
        });
        horaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickHora();
            }
        });

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(tiempo);
                AlertDialog.Builder builder = new AlertDialog.Builder(AsignarRepartidor.this,R.style.ThemeOverlay);
                builder.setTitle("Confirmar cambios!");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_correcto);
                builder.setMessage("Deseas asignar a este nuevo repartidor? ");
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickActualizarPedido();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();
            }
        });

    }


    private void ClickFecha() {
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AsignarRepartidor.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (month < 10 && dayOfMonth < 10) {
                    fechaEntrega.setText("0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                } else if (month > 10 && dayOfMonth < 10) {
                    fechaEntrega.setText("0" + dayOfMonth + "/" + (month + 1) + "/" + year);
                } else if (month < 10 && dayOfMonth > 10) {
                    fechaEntrega.setText(dayOfMonth + "/" + "0" + (month + 1) + "/" + year);
                } else {
                    fechaEntrega.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }
        }, year, mes, dia);
        datePickerDialog.show();
    }

    private void ClickHora() {

        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.HOUR_OF_DAY);
        mes = c.get(Calendar.MINUTE);

        TimePickerDialog PickerHora = new TimePickerDialog(AsignarRepartidor.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 && minute < 10) {
                    horaEntrega.setText("0" + hourOfDay + ":" + "0" + minute);
                } else if (hourOfDay < 10 && minute > 10) {
                    horaEntrega.setText("0" + hourOfDay + ":" + minute);
                } else if (hourOfDay > 10 && minute < 10) {
                    horaEntrega.setText(hourOfDay + ":" + "0" + minute);
                } else {
                    horaEntrega.setText(hourOfDay + ":" + minute);
                }
            }
        }, horaRel, minutoRel, false);

        PickerHora.show();

    }


    public void obtenerUsuarioAukdeliver(){
        final List<aukde.food.administrador.paquetes.Modelos.Spinner> aukdelivers = new ArrayList<>();
        mUsuarioAukdeliver.child("Usuarios").child("Aukdeliver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren() ){
                        String id = ds.getKey();
                        String nombres = ds.child("nombres").getValue().toString();
                        aukdelivers.add(new aukde.food.administrador.paquetes.Modelos.Spinner(id,nombres));
                    }

                    final ArrayAdapter<aukde.food.administrador.paquetes.Modelos.Spinner> arrayAdapter
                            = new ArrayAdapter<>(AsignarRepartidor.this , R.layout.custom_spinner,aukdelivers);
                    mSpinner.setAdapter(arrayAdapter);
                    arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            stEncargado = parent.getItemAtPosition(position).toString();
                            String idx = aukdelivers.get(position).getId();
                            txtEncargado.setText(stEncargado);
                            idAukdeliver.setText(idx);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void clickActualizarPedido(){

        mUsuarioAukdeliver.child("Usuarios").child("Aukdeliver")
                .child(idAukdeliver.getText().toString()).child("pedidos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Query reference = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Aukdeliver").child(idAukdeliver.getText().toString()).child("pedidos").orderByChild("estado").limitToLast(1);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                        String estado = childSnapshot.child("estado").getValue().toString();
                                        if (estado.equals("Completado")){
                                            procesoRegistro();
                                            break;
                                        }
                                        else {
                                             showEstadoOcupado();
                                             break;
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            procesoRegistro();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void procesoRegistro(){

        final String numeroPedido = edtNumPedido.getText().toString();
        final String nombreCliente = edtNombreCliente.getText().toString();
        final String telefonoCliente = edtTelefono.getText().toString();
        final String conCuantoVaAPagar = edtMontoCliente.getText().toString();
        final String direccion = edtDireccion.getText().toString();
        final String idAdminNumPedido = IDpedido.getText().toString();


        if( !nombreCliente.isEmpty() && !telefonoCliente.isEmpty() && !conCuantoVaAPagar.isEmpty() && !direccion.isEmpty()){
            if(!numeroPedido.isEmpty()){
                mDialog.show();
                mDialog.setMessage("Asignando a nuevo repartidor...");
                Map<String, Object> map = new HashMap<>();
                map.put("numPedido", edtNumPedido.getText().toString());
                map.put("horaEntrega", horaEntrega.getText().toString());
                map.put("fechaEntrega", fechaEntrega.getText().toString());
                map.put("proveedores",textSocio.getText().toString());
                map.put("productos",txtProducto.getText().toString());
                map.put("descripcion",txtDescripcion.getText().toString());
                map.put("precioUnitario",txtPrecioUnitario.getText().toString());
                map.put("cantidad",txtCantidad.getText().toString());
                map.put("precioTotalXProducto",txtPtotal.getText().toString());
                map.put("comision",txtPrecioComisionProducto.getText().toString());
                map.put("totalDelivery",txtDelivery.getText().toString());
                map.put("gananciaDelivery",txtGananciaPorDelivery.getText().toString());
                map.put("gananciaComision",txtNetoComision.getText().toString());
                map.put("totalPagoProducto",precioProductoTotal.getText().toString());
                map.put("nombreCliente", edtNombreCliente.getText().toString());
                map.put("telefono",edtTelefono.getText().toString());
                map.put("conCuantoVaAPagar",edtMontoCliente.getText().toString());
                map.put("totalCobro",precioNetoTotal.getText().toString());
                map.put("vuelto",vuelto.getText().toString());
                map.put("direccion",edtDireccion.getText().toString());
                map.put("encargado",txtEncargado.getText().toString());
                map.put("estado",estado.getText().toString());
                map.put("latitud",latitud.getText().toString());
                map.put("longitud",logitud.getText().toString());
                if (edtReferencia.getText().toString().equals("")){
                    //nada
                }
                else {
                    map.put("referencia",edtReferencia.getText().toString());
                }
                pedidosActualizadoAdmin.child(idAdminNumPedido).updateChildren(map);
                clickRegistroPedidoAukdeliver();
                mDialog.dismiss();
                sendNotificaction();
                Intent intent = new Intent(getApplicationContext(), ListaDePedidos.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toasty.success(AsignarRepartidor.this, "REPARTIDOR ASIGNADO!", Toast.LENGTH_LONG, true).show();

            }
            else {

                Toasty.warning(AsignarRepartidor.this, "Agrege el NÚMERO DE PEDIDO", Toast.LENGTH_LONG, true).show();
            }
        }

        else {
            mDialog.dismiss();
            Toasty.warning(AsignarRepartidor.this, "Verifique que los campos no estén vacios", Toast.LENGTH_LONG, true).show();
        }

    }


    private void clickRegistroPedidoAukdeliver(){

        String StAukdeliver = idAukdeliver.getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("numPedido", edtNumPedido.getText().toString());
        map.put("horaEntrega", horaEntrega.getText().toString());
        map.put("fechaEntrega", fechaEntrega.getText().toString());
        map.put("proveedores",textSocio.getText().toString());
        map.put("productos",txtProducto.getText().toString());
        map.put("descripcion",txtDescripcion.getText().toString());
        map.put("precioUnitario",txtPrecioUnitario.getText().toString());
        map.put("cantidad",txtCantidad.getText().toString());
        map.put("precioTotalXProducto",txtPtotal.getText().toString());
        map.put("comision",txtPrecioComisionProducto.getText().toString());
        map.put("totalDelivery",txtDelivery.getText().toString());
        map.put("gananciaDelivery",txtGananciaPorDelivery.getText().toString());
        map.put("gananciaComision",txtNetoComision.getText().toString());
        map.put("totalPagoProducto",precioProductoTotal.getText().toString());
        map.put("nombreCliente", edtNombreCliente.getText().toString());
        map.put("telefono",edtTelefono.getText().toString());
        map.put("conCuantoVaAPagar",edtMontoCliente.getText().toString());
        map.put("totalCobro",precioNetoTotal.getText().toString());
        map.put("vuelto",vuelto.getText().toString());
        map.put("direccion",edtDireccion.getText().toString());
        map.put("estado",estado.getText().toString());
        map.put("latitud",latitud.getText().toString());
        map.put("longitud",logitud.getText().toString());

        if (edtReferencia.getText().toString().equals("")){
            //nada
        }
        else {
            map.put("referencia",edtReferencia.getText().toString());
        }

        pedidoParaAukdeliver.child("Usuarios").child("Aukdeliver").child(StAukdeliver).child("pedidos").push().setValue(map);
    }

    private void obtenerPedido(){
        String dataNumPedido = edtNumPedido.getText().toString();
        Query reference= FirebaseDatabase.getInstance().getReference().child("PedidosPorLlamada").child("pedidos").orderByChild("numPedido").equalTo(dataNumPedido);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    IDpedido.setText(key);
                    ObtenerDatosDePedido();
                    // Toast.makeText(DetallePedido.this, "Id : "+key, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ObtenerDatosDePedido(){
        String idPedidoData = IDpedido.getText().toString();
        mDatabase.child("PedidosPorLlamada").child("pedidos").child(idPedidoData).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String Socio = dataSnapshot.child("proveedores").getValue().toString();
                    String Producto = dataSnapshot.child("productos").getValue().toString();
                    String Nota = dataSnapshot.child("descripcion").getValue().toString();
                    String PrecioUnitario = dataSnapshot.child("precioUnitario").getValue().toString();
                    String Cantidad = dataSnapshot.child("cantidad").getValue().toString();
                    String PrecioTotal = dataSnapshot.child("precioTotalXProducto").getValue().toString();
                    String Comision = dataSnapshot.child("comision").getValue().toString();
                    String Delivery = dataSnapshot.child("totalDelivery").getValue().toString();
                    String TotalPagoProducto = dataSnapshot.child("totalPagoProducto").getValue().toString();
                    String GDelivery = dataSnapshot.child("gananciaDelivery").getValue().toString();
                    String GComision = dataSnapshot.child("gananciaComision").getValue().toString();
                    String TotalCobrar = dataSnapshot.child("totalCobro").getValue().toString();
                    String MontoPagoCliente = dataSnapshot.child("conCuantoVaAPagar").getValue().toString();
                    String Vuelto = dataSnapshot.child("vuelto").getValue().toString();
                    String Direccion = dataSnapshot.child("direccion").getValue().toString();
                    String nombreClientex = dataSnapshot.child("nombreCliente").getValue().toString();
                    String Telefono = dataSnapshot.child("telefono").getValue().toString();
                    String Aukdeliver = dataSnapshot.child("encargado").getValue().toString();
                    String LatitudX = dataSnapshot.child("latitud").getValue().toString();
                    String LongitudX = dataSnapshot.child("longitud").getValue().toString();

                    edtNombreCliente.setText(nombreClientex);
                    textSocio.setText(Socio);
                    txtProducto.setText(Producto);
                    txtDescripcion.setText(Nota);
                    txtPrecioUnitario.setText(PrecioUnitario);
                    txtCantidad.setText(Cantidad);
                    txtPtotal.setText(PrecioTotal);
                    txtPrecioComisionProducto.setText(Comision);
                    txtDelivery.setText(Delivery);
                    txtNeto.setText(TotalPagoProducto);
                    txtGananciaPorDelivery.setText(GDelivery);
                    txtNetoComision.setText(GComision);
                    precioNetoTotal.setText(TotalCobrar);
                    edtTelefono.setText(Telefono);
                    edtMontoCliente.setText(MontoPagoCliente);
                    vuelto.setText(Vuelto);
                    edtDireccion.setText(Direccion);
                    RepartidorActual.setText(Aukdeliver);
                    latitud.setText(LatitudX);
                    logitud.setText(LongitudX);
                    estado.setText("En espera");

                    if (dataSnapshot.hasChild("referencia"))
                    {
                        String Referencia = dataSnapshot.child("referencia").getValue().toString();
                        edtReferencia.setText(Referencia);
                    }
                    else{
                        //nada
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(AsignarRepartidor.this,"Error de peticion",Toast.LENGTH_SHORT,true).show();
            }
        });
    }

    private void sendNotificaction(){
        final String numPedNotify = edtNumPedido.getText().toString();
        tokenProvider.getToken(idAukdeliver.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String token = dataSnapshot.child("token").getValue().toString();
                    Map<String,String> map = new HashMap<>();
                    map.put("title","Nuevo pedido #"+numPedNotify);
                    map.put("body","Usted tiene un nuevo pedido"+"\n"+"Nombre del cliente : "
                            +edtNombreCliente.getText().toString()+"\n"+"Teléfono : "+edtTelefono.getText().toString());
                    map.put("idClient",mAuth.getUid());
                    map.put("numPedido",numPedNotify);
                    map.put("nombre",edtNombreCliente.getText().toString());
                    map.put("telefono",edtTelefono.getText().toString());
                    map.put("direccion",edtDireccion.getText().toString());
                    map.put("hora",horaEntrega.getText().toString());
                    map.put("fecha",fechaEntrega.getText().toString());
                    map.put("ganancia",txtGananciaPorDelivery.getText().toString());
                    map.put("repartidor",txtEncargado.getText().toString());
                    FCMBody fcmBody = new FCMBody(token,"high",map);
                    notificationProvider.sendNotificacion(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if(response.body() !=null){
                                if(response.body().getSuccess() == 1){
                                    //Toast.makeText(RealizarPedido.this, "Notificación enviada", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(AsignarRepartidor.this, "No se envió la notificación", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(AsignarRepartidor.this, "No se envió la notificación", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error","Error encontrado"+ t.getMessage());
                        }
                    });
                }

                else {
                    Toasty.error(AsignarRepartidor.this, "No se envió la notificación", Toast.LENGTH_LONG).show();                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showEstadoOcupado(){
        aukdeliverOcupado.setContentView(R.layout.dialog_ocupado);
        cerrarImg = (ImageView) aukdeliverOcupado.findViewById(R.id.closeDialog);
        cerrarPopup = (Button) aukdeliverOcupado.findViewById(R.id.btnCerrarDialog);

        cerrarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(tiempo);
                aukdeliverOcupado.dismiss();
            }
        });

        cerrarPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(tiempo);
                aukdeliverOcupado.dismiss();
            }
        });

        aukdeliverOcupado.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        aukdeliverOcupado.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}