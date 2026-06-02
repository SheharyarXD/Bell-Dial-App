package belldial.co.uk.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Message {

    @SerializedName("Extension")
    @Expose
    private String extension;

    @SerializedName("Password")
    @Expose
    private String password;

    @SerializedName("PhoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("Domain")
    @Expose
    private String domain;

    @SerializedName("OrderId")
    @Expose
    private String orderId;

    @SerializedName("Products")
    @Expose
    private List<Product> products = new ArrayList<>();

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
