package aukde.food.administrador.paquetes.Actividades.Pedidos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Inclusiones.MiToolbar;
import aukde.food.administrador.paquetes.Modelos.PedidoLlamada;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import es.dmoral.toasty.Toasty;

public class WoocommerceDetailOrder extends AppCompatActivity implements OnMapReadyCallback {


    TextView name , lastName , phone , email , address , status, sku ,
            productName , quantity , price , subtotal , total ,
            payMethod , aditional , priceAditional , payWith;
    String id;
    EditText shipping;
    ImageView methodPayIMG;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mapView;
    private Geocoder geocoder;
    private GoogleMap mMap;
    double latitude ;
    double longitude ;
    String [] negocios ;
    String listSku = "";
    LinearLayout linear;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woocommerce_detail_order);
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

        geocoder = new Geocoder(this);

        negocios = getResources().getStringArray(R.array.negocios);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        Woocommerce woo = (Woocommerce) bundle.getSerializable("dataOrder");
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
                if(woo.getLine_items().get(k).getMeta_data().get(n).getKey().contains("Adicionales")){
                    data.add(woo.getLine_items().get(k).getMeta_data().get(n).getValue());
                    data.add(woo.getLine_items().get(k).getMeta_data().get(n).getKey());
                    String addOn = data.get(10);
                    String price = data.get(11);
                    aditional.append(addOn);
                    priceAditional.append(price);
                }
            }
        }
        String regx1 = priceAditional.getText().toString().replaceAll("Adicionales","");
        String regx2 = regx1.replaceAll("\\s*","");
        String rexg3 = regx2.replaceAll("[()]","");
        priceAditional.setText(rexg3);


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
            listSku = woo.getLine_items().get(k).getSku()+"\n";
            productName.append(woo.getLine_items().get(k).getName()+"\n");
            quantity.append(woo.getLine_items().get(k).getQuantity()+"\n");
            price.append("S/"+woo.getLine_items().get(k).getPrice()+"\n");
            subtotal.append("S/"+woo.getLine_items().get(k).getSubtotal()+"\n");
        }

        String [] c = listSku.split("-");
        Toast.makeText(this, c[0], Toast.LENGTH_SHORT).show();
        sku.setText(c[0]);

        shipping.setText(woo.getShipping_total());
        if (shipping.getText().toString().equals("")){
            shipping.setText("S/0.00");
        }
        else{
            shipping.setText("S/"+woo.getShipping_total());
        }
        total.setText("S/"+woo.getTotal());

        //allBusiness();
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

        if (status.getText().toString().equals("cancelled")){
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

    private void allBusiness(){

        String code = sku.getText().toString();
        String[] skCode = code.split("\\n");

            for(int i = 0 ; i<negocios.length ; i++){
                String [] val = negocios[i].split("-");
                Toast.makeText(this, val[i], Toast.LENGTH_SHORT).show();
                if (negocios[i].split("-").equals(skCode)){
                    Toast.makeText(this, negocios[i], Toast.LENGTH_SHORT).show();
                }
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