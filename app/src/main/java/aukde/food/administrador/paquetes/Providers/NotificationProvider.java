package aukde.food.administrador.paquetes.Providers;

import aukde.food.administrador.paquetes.Modelos.FCMBody;
import aukde.food.administrador.paquetes.Modelos.FCMResponse;
import aukde.food.administrador.paquetes.Retrofit.IFCMapi;
import aukde.food.administrador.paquetes.Retrofit.RetrofitClient;
import retrofit2.Call;

public class NotificationProvider {

    private  String url = "https://fcm.googleapis.com";

    public NotificationProvider(){
    }

    public Call<FCMResponse> sendNotificacion(FCMBody body){
        return RetrofitClient.getClientObject(url).create(IFCMapi.class).send(body);
    }

}