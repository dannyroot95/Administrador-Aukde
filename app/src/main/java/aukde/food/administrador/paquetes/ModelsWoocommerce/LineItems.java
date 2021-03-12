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

	@SerializedName("product_id")
	private String product_id;

	@SerializedName("variation_id")
	private String variation_id;

	public LineItems(String quantity, String total, String subtotal, String price, String name,
					 String sku, List<MetaDataItem> meta_data, String product_id, String variation_id) {
		this.quantity = quantity;
		this.total = total;
		this.subtotal = subtotal;
		this.price = price;
		this.name = name;
		this.sku = sku;
		this.meta_data = meta_data;
		this.product_id = product_id;
		this.variation_id = variation_id;
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

	public String getProduct_id() {
		return product_id;
	}

	public String getVariation_id() {
		return variation_id;
	}
}