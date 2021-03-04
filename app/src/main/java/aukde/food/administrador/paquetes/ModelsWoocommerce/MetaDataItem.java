package aukde.food.administrador.paquetes.ModelsWoocommerce;

import java.io.Serializable;

public class MetaDataItem implements Serializable {
	private int id;
	private String value;
	private String key;

	public MetaDataItem(int id, String value, String key) {
		this.id = id;
		this.value = value;
		this.key = key;
	}

	public int getId(){
		return id;
	}

	public String getValue(){
		return value;
	}

	public String getKey(){
		return key;
	}
}
