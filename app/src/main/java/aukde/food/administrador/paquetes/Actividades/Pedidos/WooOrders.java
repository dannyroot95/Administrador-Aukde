package aukde.food.administrador.paquetes.Actividades.Pedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.ModelsWoocommerce.LineItems;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import aukde.food.administrador.paquetes.Retrofit.WoocommerceAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WooOrders extends AppCompatActivity {

    TextView txtAPI;
    WoocommerceAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woo_orders);
        Gson gson = new GsonBuilder().serializeNulls().create();
        txtAPI = findViewById(R.id.txtApi);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://aukde.com/wp-json/wc/v3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(WoocommerceAPI.class);

        getData();

    }

    private void getData() {
        Call<List<Woocommerce>> call = api.getWooData();
        call.enqueue(new Callback<List<Woocommerce>>() {
            @Override
            public void onResponse(Call<List<Woocommerce>> call, Response<List<Woocommerce>> response) {
                if(!response.isSuccessful()){
                    txtAPI.setText("Code :"+response.code());
                    return;
                }

                List<Woocommerce> woocommerce = response.body();
                for (Woocommerce woo : woocommerce){
                    String content = "";
                    String lat = "";
                    String lon = "";
                    content += "ID de Pedido : "+woo.getId()+"\n";
                    content += "Fecha: "+woo.getDate_created()+"\n";
                    content += "Estado : "+woo.getStatus()+"\n";
                    content += "Total : "+"S/"+woo.getTotal()+"\n";
                    content += "Nota : "+woo.getCustomer_note()+"\n";
                    content += "Método de pago : "+woo.getPayment_method_title()+"\n";
                    content += "Nombre : "+woo.getBilling().getFirstName()+"\n";
                    content += "Apellido : "+woo.getBilling().getLastName()+"\n";
                    content += "Dirección : "+woo.getBilling().getAddress_1()+"\n";
                    content += "Email : "+woo.getBilling().getEmail()+"\n";

                    for (int i = 0 ; i<woo.getLine_items().size() ; i++){
                        content += "Producto "+i +" : "+woo.getLine_items().get(i).getName()+"\n";
                    }

                    for (int j = 0 ; j<woo.getMeta_data().size() ; j++){
                        if (woo.getMeta_data().get(j).getKey().equals("billing_lat")){
                            content += "latitud : "+woo.getMeta_data().get(j).getValue()+"\n";
                        }
                        if (woo.getMeta_data().get(j).getKey().equals("billing_long")){
                            content += "longitud : "+woo.getMeta_data().get(j).getValue()+"\n";
                        }

                    }

                    content += ""+"\n";
                    txtAPI.append(content);
                }
            }
            @Override
            public void onFailure(Call<List<Woocommerce>> call, Throwable t) {
                txtAPI.setText(t.getMessage());
            }
        });

    }
}