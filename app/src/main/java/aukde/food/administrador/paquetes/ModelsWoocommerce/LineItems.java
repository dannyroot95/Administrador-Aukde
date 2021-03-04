package aukde.food.administrador.paquetes.ModelsWoocommerce;

import java.io.Serializable;

public class LineItems implements Serializable {


	private int quantity;
	private String total;
	private String subtotal;
	private double price;
	private String name;
	private String sku;

	public int getQuantity() {
		return quantity;
	}

	public String getTotal() {
		return total;
	}

	public String getSubtotal() {
		return subtotal;
	}

	public double getPrice() {
		return price;
	}

	public String getName() {
		return name;
	}

	public String getSku() {
		return sku;
	}
}