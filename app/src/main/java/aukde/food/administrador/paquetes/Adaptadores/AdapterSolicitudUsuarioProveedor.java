package aukde.food.administrador.paquetes.Adaptadores;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import aukde.food.administrador.R;
import aukde.food.administrador.paquetes.Actividades.Usuarios.DetalleSolicitudUsuarioProveedor;
import aukde.food.administrador.paquetes.Modelos.UsuarioProveedor;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSolicitudUsuarioProveedor extends RecyclerView.Adapter<AdapterSolicitudUsuarioProveedor.viewHolder> {

    private List<UsuarioProveedor> socio;
    private Context c;

    public AdapterSolicitudUsuarioProveedor(List<UsuarioProveedor> socios){
        this.socio = socios;
    }

    @NonNull
    @Override
    public AdapterSolicitudUsuarioProveedor.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        c = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_lista_solicitud_usuario_proveedor,parent,false);
        viewHolder holder = new viewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterSolicitudUsuarioProveedor.viewHolder holder, final int position) {

        final UsuarioProveedor ls = socio.get(position);
        int state = ls.getProfileCompleted();
        String phone = String.valueOf(ls.getMobile());

        holder.store.setText(ls.getName_store());
        holder.name.setText(ls.getFirstName());
        if (state == 0){
            holder.status.setText("Perfil por confirmar");
            holder.status.setTextColor(Color.parseColor("#FF0000"));
        }
        else{
            holder.status.setText("Confirmado");
            holder.status.setTextColor(Color.parseColor("#5BBD00"));
        }

        holder.lastName.setText(ls.getLastName());
        holder.phone.setText(phone);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, DetalleSolicitudUsuarioProveedor.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dataUser",ls);
                intent.putExtras(bundle);
                c.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return socio.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView store , name , lastName ,phone,status;
        CircleImageView detail;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            store = itemView.findViewById(R.id.lsBusinnes);
            name = itemView.findViewById(R.id.lsName);
            lastName = itemView.findViewById(R.id.lsLastName);
            status = itemView.findViewById(R.id.lsStatus);
            phone = itemView.findViewById(R.id.lsPhone);
            detail = itemView.findViewById(R.id.detailOrder);
        }
    }
}
