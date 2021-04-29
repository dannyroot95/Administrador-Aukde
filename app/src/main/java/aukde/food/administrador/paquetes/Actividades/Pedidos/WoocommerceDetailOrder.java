package aukde.food.administrador.paquetes.Actividades.Pedidos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Inclusiones.MiToolbar;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Products;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import aukde.food.administrador.paquetes.Retrofit.WoocommerceAPI;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WoocommerceDetailOrder extends AppCompatActivity implements OnMapReadyCallback {


    TextView name , lastName , phone , email , address , status, sku ,
            productName , quantity , price , subtotal , total ,
            payMethod , aditional , priceAditional , payWith;

    String [] negocios;

    String id;
    EditText shipping;
    ImageView methodPayIMG;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mapView;
    private Geocoder geocoder;
    private GoogleMap mMap;
    double latitude ;
    double longitude ;

    String listSku = "";
    LinearLayout linear;
    int idProduct;
    int variationID;
    String variation = "";
    WoocommerceAPI api;
    Button btnSend , btnEquals;

    DatabaseReference mReference;
    String business;
    String [] resourceBusiness;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woocommerce_detail_order);
        mReference = FirebaseDatabase.getInstance().getReference().child("Orders");
        negocios = getResources().getStringArray(R.array.negocios);
        name = findViewById(R.id.orderNameClient);
        lastName = findViewById(R.id.orderLastNameClient);
        phone = findViewById(R.id.orderPhoneClient);
        email = findViewById(R.id.orderEmailClient);
        address = findViewById(R.id.orderAddressClient);
        status = findViewById(R.id.orderStatusClient);
        sku = findViewById(R.id.lsSku);
        productName = findViewById(R.id.lsProduct);
        quantity = findViewById(R.id.lsQuantity);
        price = findViewById(R.id.lsPrice);
        subtotal = findViewById(R.id.lsSubtotal);
        shipping = findViewById(R.id.orderShippingClient);
        total = findViewById(R.id.orderTotalClient);
        payMethod = findViewById(R.id.orderPayMethod);
        methodPayIMG = findViewById(R.id.imgPayMethod);
        linear = findViewById(R.id.linearPayWith);
        payWith = findViewById(R.id.payWith);
        aditional = findViewById(R.id.lsProductAditional);
        priceAditional = findViewById(R.id.lsPriceAditional);
        btnSend = findViewById(R.id.prueba);
        btnEquals = findViewById(R.id.prueba2);
        geocoder = new Geocoder(this);
        resourceBusiness = getResources().getStringArray(R.array.negocios);

        Gson gson = new GsonBuilder().serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://aukde.com/wp-json/wc/v3/products/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(WoocommerceAPI.class);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final Woocommerce woo = (Woocommerce) bundle.getSerializable("dataOrder");
        ArrayList<String> data;
        data = new ArrayList<>();
        data.add(woo.getId().toString());
        id = data.get(0);

        data.add(woo.getStatus());
        data.add(woo.getBilling().getFirstName());
        data.add(woo.getBilling().getLastName());
        data.add(woo.getBilling().getPhone());
        data.add(woo.getBilling().getEmail());
        data.add(woo.getBilling().getAddress_1());

        for (int i = 0 ; i<woo.getMeta_data().size() ; i++){
            if (woo.getMeta_data().get(i).getKey().equals("billing_lat")){
                data.add(woo.getMeta_data().get(i).getValue());
                 latitude = Double.parseDouble((data.get(7)));
            }
        }

        for (int j = 0 ; j<woo.getMeta_data().size() ; j++){
            if (woo.getMeta_data().get(j).getKey().equals("billing_long")){
                data.add(woo.getMeta_data().get(j).getValue());
                longitude = Double.parseDouble((data.get(8)));
            }
        }

        for (int m = 0 ; m<woo.getMeta_data().size() ; m++){
            if (woo.getMeta_data().get(m).getKey().equals("additional_monto")){
                data.add(woo.getMeta_data().get(m).getValue());
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                linear.setLayoutParams(param);
                payWith.setText(data.get(9));
            }
        }

        for (int k = 0 ; k<woo.getLine_items().size(); k++)
        {
            for (int n = 0 ; n<woo.getLine_items().get(k).getMeta_data().size() ; n++)
            {
                String x = woo.getLine_items().get(k).getMeta_data().get(n).getKey();

                if(x.contains("Adicionales ")){
                    String addOn = woo.getLine_items().get(k).getMeta_data().get(n).getValue();
                    String price = woo.getLine_items().get(k).getMeta_data().get(n).getKey();
                    aditional.append(addOn+"\n");
                    String convert = price.replaceAll("Adicionales","");
                    String convert2 = convert.replaceAll("\\s*","");
                    String convert3 = convert2.replaceAll("[()]","");
                    priceAditional.append(convert3+"\n");
                }
            }
        }


        MiToolbar.Mostrar(this,"Detalle del pedido #"+id,true);

        status.setText(data.get(1));
        verifyState();

        name.setText(data.get(2));
        lastName.setText(data.get(3));
        phone.setText(data.get(4));
        email.setText(data.get(5));
        address.setText(data.get(6));

        payMethod.setText(woo.getPayment_method_title());
        checkPayImage();

        for (int k = 0 ; k<woo.getLine_items().size(); k++){
            //productName.append(woo.getLine_items().get(k).getName()+"\n");
            quantity.append(woo.getLine_items().get(k).getQuantity()+"\n");
            idProduct = Integer.parseInt(woo.getLine_items().get(k).getProduct_id());
            variation = woo.getLine_items().get(k).getVariation_id();
            variationID = Integer.parseInt(woo.getLine_items().get(k).getVariation_id());

            if(variation.equals("0")){
                Call<Products> call = api.getProductData(idProduct);
                final int finalK = k;
                call.enqueue(new Callback<Products>() {
                    @Override
                    public void onResponse(Call<Products> call, Response<Products> response) {
                        if(!response.isSuccessful()){
                            Toasty.error(WoocommerceDetailOrder.this, "Error : " + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String value = response.body().getPrice();
                        String nameProduct = response.body().getName();
                        listSku = response.body().getSku();
                        double valueDouble = Double.parseDouble(value);
                        int cant = Integer.parseInt(woo.getLine_items().get(finalK).getQuantity());
                        double sum = cant*valueDouble;
                        price.append("S/"+value+"\n");
                        subtotal.append("S/"+(sum)+"\n");
                        productName.append(nameProduct+"\n");
                        String [] c = listSku.split("-");
                        sku.append(c[0]+"\n");
                        business = sku.getText().toString();
                        String [] currentBusiness = business.split("\n");
                        sku.setText("");
                        for(String name : currentBusiness){
                            for (String s : resourceBusiness) {
                                String[] x = s.split("-");
                                if (name.equalsIgnoreCase(x[0])) {
                                    sku.append(x[1] + "\n");
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Products> call, Throwable t) {
                    }
                });
            }
            else {
                Call<Products> call = api.getProductData(variationID);
                final int finalK = k;
                call.enqueue(new Callback<Products>() {
                    @Override
                    public void onResponse(Call<Products> call, Response<Products> response) {
                        if(!response.isSuccessful()){
                            Toasty.error(WoocommerceDetailOrder.this, "Error : " + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String value = response.body().getPrice();
                        String nameProduct = response.body().getName();
                        listSku = response.body().getSku();
                        double valueDouble = Double.parseDouble(value);
                        int cant = Integer.parseInt(woo.getLine_items().get(finalK).getQuantity());
                        double sum = cant*valueDouble;
                        price.append("S/"+value+"\n");
                        subtotal.append("S/"+(sum)+"\n");
                        productName.append(nameProduct+"\n");

                        String [] c = listSku.split("-");
                        sku.append(c[0]+"\n");
                        String [] currentBusiness= business.split("\n");
                        sku.setText("");
                        for(String name : currentBusiness){
                            for(String s : resourceBusiness) {
                                String[] x = s.split("-");
                                if (name.equalsIgnoreCase(x[0])) {
                                    sku.append(x[1] + "\n");
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Products> call, Throwable t) {
                    }
                });
            }
        }

        shipping.setText(woo.getShipping_total());
        if (shipping.getText().toString().equals("")){
            shipping.setText("S/0.00");
        }
        else{
            shipping.setText("S/"+woo.getShipping_total());
        }
        total.setText("S/"+woo.getTotal());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //allBusiness();
                searchBusinessID();
            }
        });
        btnEquals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              equalsx();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void verifyState(){

        if(status.getText().toString().equals("cancelled")){
            status.setText("Cancelado");
            status.setTextColor(Color.parseColor("#FC0000"));
        }
        else if (status.getText().toString().equals("on-hold")){
            status.setText("En espera");
            status.setTextColor(Color.parseColor("#00AC9A"));
        }
        else if (status.getText().toString().equals("processing")){
            status.setText("Procesando");
            status.setTextColor(Color.parseColor("#FFC000"));
        }
        else if (status.getText().toString().equals("pending")){
            status.setText("Falta de pago");
            status.setTextColor(Color.parseColor("#2874A6"));
        }
        else {
            status.setText("Completado");
            status.setTextColor(Color.parseColor("#5bbd00"));
        }
    }

    private void checkPayImage(){
        String nameMethodPay = payMethod.getText().toString();
        if (nameMethodPay.equals("Efectivo")){
            methodPayIMG.setImageResource(R.drawable.payefectivo);
        }
        else if (nameMethodPay.equals("Yape")){
            methodPayIMG.setImageResource(R.drawable.payyape);
        }
        else{
            methodPayIMG.setImageResource(R.drawable.paycard);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng aukde = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(aukde)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
                        .title("CLIENTE");
                mMap.addMarker(markerOptions).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aukde, 16));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void equalsx(){
        String business = "Aukde\nCoco Ice Cream\nAukde\n";
        String [] val = business.split("\\n");
        for (int i = 0 ; i<sku.length(); i++){
            for(int j = 0 ; j<sku.length(); j++){
               if(val[i].equals(val[j])){
                   Toast.makeText(this, val[i]+" es igual a : " + val[j], Toast.LENGTH_SHORT).show();
               }
            }
        }
    }

    private void searchBusinessID(){
        //String business = "Aukde\nCoco Ice Cream\nQ' Tal Concha\n";
        String business = "Aukde\nCoco Ice Cream\n";
        String [] val = business.split("\\n");
        final String [] valProduct = productName.getText().toString().split("\\n");

        for(int i = 0 ; i<val.length; i++) {

            Query reference = FirebaseDatabase.getInstance().getReference()
                    .child("Usuarios")
                    .child("Proveedor")
                    .orderByChild("nombre empresa")
                    .equalTo(val[i]);

            final int tempI = i;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        //aditional.append(key+"\n");
                        Map<String , Object> map = new HashMap<>();
                        map.put("numOrder",id);
                        map.put("name",name.getText().toString());
                        map.put("lastName",lastName.getText().toString());
                        map.put("status",status.getText().toString());
                        map.put("phone",phone.getText().toString());
                        map.put("product",valProduct[tempI]+"\n");
                        mReference.child(Objects.requireNonNull(key)).child(id).updateChildren(map);
                    }
                    Toasty.success(WoocommerceDetailOrder.this, "Exito!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(WoocommerceDetailOrder.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}