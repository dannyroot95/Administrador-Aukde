package aukde.food.administrador.paquetes.ModelsWoocommerce;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LineItems implements Serializable {


	private String quantity;
	private String total;
	private String subtotal;
	private String price;
	private String name;
	private String sku;

	@SerializedName("meta_data")
	public List<MetaDataItem> meta_data;

	public LineItems(String quantity, String total, String subtotal, String price, String name, String sku,
					 List<MetaDataItem> meta_data) {
		this.quantity = quantity;
		this.total = total;
		this.subtotal = subtotal;
		this.price = price;
		this.name = name;
		this.sku = sku;
		this.meta_data = meta_data;
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

	public List<MetaDataItem> getMeta_data() {
		return meta_data;
	}
}