package aukde.food.administrador.paquetes.ModelsWoocommerce;

import java.io.Serializable;

public class Products implements Serializable {

    String id;
    String name;
    String price;
    String sku;

    public Products(String id, String name, String price, String sku) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sku = sku;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getSku() {
        return sku;
    }
}
