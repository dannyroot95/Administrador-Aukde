package aukde.food.administrador.paquetes.Actividades.Pedidos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Inclusiones.MiToolbar;
import aukde.food.administrador.paquetes.Modelos.PedidoLlamada;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import es.dmoral.toasty.Toasty;

public class WoocommerceDetailOrder extends AppCompatActivity {


    TextView name , lastName , phone , email , address , status, sku ,
            productName , quantity , price , subtotal , total , shipping,
            payMethod;
    String id;
    ImageView methodPayIMG;

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

        /*for (int i = 0 ; i<woo.getMeta_data().size() ; i++){
            if (woo.getMeta_data().get(i).getKey().equals("billing_lat")){
                data.add(woo.getMeta_data().get(i).getValue());
                latitude.setText(data.get(4));
            }
        }

        for (int j = 0 ; j<woo.getMeta_data().size() ; j++){
            if (woo.getMeta_data().get(j).getKey().equals("billing_long")){
                data.add(woo.getMeta_data().get(j).getValue());
                longitude.setText(data.get(5));
            }
        }*/



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
            sku.append(woo.getLine_items().get(k).getSku()+"\n");
            productName.append(woo.getLine_items().get(k).getName()+"\n");
            quantity.append(woo.getLine_items().get(k).getQuantity()+"\n");
            price.append("S/"+woo.getLine_items().get(k).getPrice()+"\n");
            subtotal.append("S/"+woo.getLine_items().get(k).getSubtotal()+"\n");
        }

        shipping.setText(woo.getShipping_total());
        if (shipping.getText().toString().equals("")){
            shipping.setText("S/0.00");
        }
        else{
            shipping.setText("S/"+woo.getShipping_total());
        }

        total.setText("S/"+woo.getTotal());
        //Toast.makeText(this, data.get(0), Toast.LENGTH_SHORT).show();

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


}