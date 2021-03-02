package aukde.food.administrador.paquetes.ModelsWoocommerce;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Woocommerce implements Serializable {

    private Integer id;
    private String date_created;
    private String status;
    private String total;
    private String customer_note;
    private String payment_method_title;
    private Billing billing;

    @SerializedName("line_items")
    public  List<LineItems> line_items;

    public Woocommerce(Integer id, String date_created, String status, String total,
                       String customer_note, String payment_method_title,
                       Billing billing, List<LineItems> line_items) {
        this.id = id;
        this.date_created = date_created;
        this.status = status;
        this.total = total;
        this.customer_note = customer_note;
        this.payment_method_title = payment_method_title;
        this.billing = billing;
        this.line_items = line_items;
    }

    public Integer getId() {
        return id;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getStatus() {
        return status;
    }

    public String getTotal() {
        return total;
    }

    public String getCustomer_note() {
        return customer_note;
    }

    public String getPayment_method_title() {
        return payment_method_title;
    }

    public Billing getBilling() {
        return billing;
    }

    public List<LineItems> getLine_items() {
        return line_items;
    }
}
