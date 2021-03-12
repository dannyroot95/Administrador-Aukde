package aukde.food.administrador.paquetes.Retrofit;

import java.util.List;

import aukde.food.administrador.paquetes.ModelsWoocommerce.LineItems;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Products;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface WoocommerceAPI {

    @GET("orders?consumer_key=" +
            "ck_f31dc4c281b7fff42ac5a5d9c6d4cd3fab2c13a3" +
            "&consumer_secret=cs_b8cbad1525db61e68b5b4d4b23fc54bb424ed3b6&order=desc")
    Call<List<Woocommerce>> getWooData();

    @GET("{id}?consumer_key=" +
                    "ck_f31dc4c281b7fff42ac5a5d9c6d4cd3fab2c13a3" +
                    "&consumer_secret=cs_b8cbad1525db61e68b5b4d4b23fc54bb424ed3b6&order=desc")
    Call<Products> getProductData(@Path("id") int productId);

}
