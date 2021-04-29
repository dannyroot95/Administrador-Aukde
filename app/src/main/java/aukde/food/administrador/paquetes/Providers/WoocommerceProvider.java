package aukde.food.administrador.paquetes.Providers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import aukde.food.administrador.paquetes.Modelos.Administrador;
import aukde.food.administrador.paquetes.ModelsWoocommerce.Woocommerce;

public class WoocommerceProvider {

    DatabaseReference mDatabaseReference;

    public WoocommerceProvider(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("PedidosWeb");

    }

    public Task<Void> Mapear(Woocommerce woo , String id) {
        String idOrder = String.valueOf(woo.getId());
        Map<String , Object> map = new HashMap<>();
        map.put("numOrder",idOrder);
        map.put("name",woo.getBilling().getFirstName());
        map.put("lastName",woo.getBilling().getLastName());
        map.put("status",woo.getStatus());
        return mDatabaseReference.child(id).child(idOrder).updateChildren(map);
    }


}
