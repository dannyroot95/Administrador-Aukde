package aukde.food.administrador.paquetes.Adaptadores;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Actividades.Pedidos.DetallePedido;
import aukde.food.administrador.paquetes.Actividades.Pedidos.WoocommerceDetailOrder;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;
import aukde.food.administrador.paquetes.Retrofit.WoocommerceAPI;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterWoocommerce extends RecyclerView.Adapter<AdapterWoocommerce.viewHolderOrders>  {

    private List<Woocommerce> woo;
    private Context c;

    public AdapterWoocommerce(List<Woocommerce> woocommerces) {
        this.woo = woocommerces ;
    }

    @NonNull
    @Override
    public viewHolderOrders onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        c = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_woocommerce_orders,parent,false);
        viewHolderOrders holder = new viewHolderOrders(view);
        return holder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AdapterWoocommerce.viewHolderOrders holder, final int position) {

        final Woocommerce ls = woo.get(position);

        holder.order.setText(ls.getId().toString());
        holder.name.setText(ls.getBilling().getFirstName());
        holder.status.setText(ls.getStatus());
        holder.lastName.setText(ls.getBilling().getLastName());
        holder.phone.setText(ls.getBilling().getPhone());


        for (int j = 0 ; j<ls.getMeta_data().size();j++){
            if (ls.getMeta_data().get(j).getKey().equals("billing_long")){
                holder.lng = ls.getMeta_data().get(j).getValue();
            }
        }

        if ( holder.status.getText().equals("on-hold")){
            holder.status.setText("En espera");
            holder.status.setTextColor(Color.parseColor("#00AC9A"));
        }

        if ( holder.status.getText().equals("pending")){
            holder.status.setText("Falta de pago");
            holder.status.setTextColor(Color.parseColor("#2874A6"));
        }

        if ( holder.status.getText().equals("cancelled")){
            holder.status.setText("Cancelado");
            holder.status.setTextColor(Color.parseColor("#FC0000"));
        }

        if ( holder.status.getText().equals("processing")){
            holder.status.setText("Procesando");
            holder.status.setTextColor(Color.parseColor("#FFC000"));
        }

        if ( holder.status.getText().equals("completed")){
            holder.status.setText("Completado");
            holder.status.setTextColor(Color.parseColor("#5bbd00"));
        }

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c,WoocommerceDetailOrder.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dataOrder",ls);
                intent.putExtras(bundle);
                c.startActivity(intent);
                //Toast.makeText(c, "Latitud : "+holder.lat+" , "+"Longitud : "+holder.lng, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {

        return woo.size();
    }

    public class viewHolderOrders extends RecyclerView.ViewHolder {

        TextView order , name , lastName , status , phone;
        String lat="";
        String lng="";
        CircleImageView detail;

        public viewHolderOrders(@NonNull final View itemView) {
            super(itemView);

            order = itemView.findViewById(R.id.lsNumOrder);
            name = itemView.findViewById(R.id.lsName);
            lastName = itemView.findViewById(R.id.lsLastName);
            status = itemView.findViewById(R.id.lsStatus);
            phone = itemView.findViewById(R.id.lsPhone);
            detail = itemView.findViewById(R.id.detailOrder);

        }
    }

}
