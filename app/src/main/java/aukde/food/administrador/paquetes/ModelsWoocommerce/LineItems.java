package aukde.food.administrador.paquetes.ModelsWoocommerce;

import java.io.Serializable;

public class LineItems implements Serializable {


	private String quantity;
	private String total;
	private String subtotal;
	private String price;
	private String name;
	private String sku;

	public LineItems(String quantity, String total, String subtotal, String price, String name, String sku) {
		this.quantity = quantity;
		this.total = total;
		this.subtotal = subtotal;
		this.price = price;
		this.name = name;
		this.sku = sku;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getTotal() {
		return total;
	}

	public String getSubtotal() {
		return subtotal;
	}

	public String getPrice() {
		return price;
	}

	public String getName() {
		return name;
	}

	public String getSku() {
		return sku;
	}
}