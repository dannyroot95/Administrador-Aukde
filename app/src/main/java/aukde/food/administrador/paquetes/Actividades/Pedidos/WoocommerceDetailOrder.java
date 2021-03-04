package aukde.food.administrador.paquetes.Actividades.Pedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Inclusiones.MiToolbar;
import aukde.food.administrador.paquetes.Modelos.PedidoLlamada;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import es.dmoral.toasty.Toasty;

public class WoocommerceDetailOrder extends AppCompatActivity {


    TextView name , lastName , phone , email;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woocommerce_detail_order);
        name = findViewById(R.id.orderNameClient);
        lastName = findViewById(R.id.orderLastNameClient);
        phone = findViewById(R.id.orderPhoneClient);
        email = findViewById(R.id.orderEmailClient);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        Woocommerce woo = (Woocommerce) bundle.getSerializable("dataOrder");
        ArrayList<String> data;
        data = new ArrayList<>();
        data.add(woo.getId().toString());
        id = data.get(0);

        data.add(woo.getBilling().getFirstName());
        data.add(woo.getBilling().getLastName());
        data.add(woo.getBilling().getPhone());
        data.add(woo.getBilling().getEmail());

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

        name.setText(data.get(1));
        lastName.setText(data.get(2));
        phone.setText(data.get(3));
        email.setText(data.get(4));
        //Toast.makeText(this, data.get(0), Toast.LENGTH_SHORT).show();

    }


}