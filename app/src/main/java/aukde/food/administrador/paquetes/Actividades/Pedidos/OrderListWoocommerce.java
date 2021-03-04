package aukde.food.administrador.paquetes.Actividades.Pedidos;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Adaptadores.AdapterWoocommerce;
import aukde.food.administrador.paquetes.Inclusiones.MiToolbar;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import aukde.food.administrador.paquetes.Retrofit.WoocommerceAPI;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderListWoocommerce extends AppCompatActivity {


    RecyclerView recyclerViewOrders;
    AdapterWoocommerce adapterOrdersWoocommerce;
    ArrayList<Woocommerce> listItems;
    LinearLayoutManager linearLayoutManager;
    SearchView searchViewOrders;
    WoocommerceAPI api;
    private ProgressDialog mDialogActualizeData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_woocommerce);
        MiToolbar.Mostrar(this,"Pedidos de productos",true);

        mDialogActualizeData = new ProgressDialog(this,R.style.MyAlertDialogData);
        mDialogActualizeData.setCancelable(false);
        mDialogActualizeData.show();
        mDialogActualizeData.setContentView(R.layout.dialog_data);
        mDialogActualizeData.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        listItems = new ArrayList<>();
        adapterOrdersWoocommerce = new AdapterWoocommerce(listItems);

        Gson gson = new GsonBuilder().serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://aukde.com/wp-json/wc/v3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(WoocommerceAPI.class);

        searchViewOrders = findViewById(R.id.searchOrders);
        searchViewOrders.setBackgroundColor(Color.WHITE);
        recyclerViewOrders = findViewById(R.id.recyclerviewOrders);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewOrders.setLayoutManager(linearLayoutManager);
        recyclerViewOrders.setAdapter(adapterOrdersWoocommerce);

        Call<List<Woocommerce>> call = api.getWooData();
        call.enqueue(new Callback<List<Woocommerce>>() {
            @Override
            public void onResponse(Call<List<Woocommerce>> call, Response<List<Woocommerce>> response) {
                if(!response.isSuccessful()){
                    Toasty.error(OrderListWoocommerce.this, "Error : " + response.code(), Toast.LENGTH_SHORT).show();
                    mDialogActualizeData.dismiss();
                    return;
                }
                List<Woocommerce> woocommerce = response.body();
                for (Woocommerce woo : woocommerce){
                    listItems.add(woo);
                }
                adapterOrdersWoocommerce.notifyDataSetChanged();
                mDialogActualizeData.dismiss();
            }

            @Override
            public void onFailure(Call<List<Woocommerce>> call, Throwable t) {

            }
        });

        searchViewOrders.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                search(text);
                return true;
            }
        });

    }

    private void search(String text) {
        ArrayList<Woocommerce> list = new ArrayList<>();
        for(Woocommerce object : listItems){
            if (object.getId().toString().toLowerCase().contains(text.toLowerCase()))
            {
                list.add(object);
            }
            else {
            }
        }
        AdapterWoocommerce adapter = new AdapterWoocommerce(list);
        recyclerViewOrders.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

}