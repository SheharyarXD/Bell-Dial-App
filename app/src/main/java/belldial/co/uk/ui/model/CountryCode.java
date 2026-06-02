package belldial.co.uk.ui.model;

/** Created by Kaushik on 8/10/2017. */
public class CountryCode {

    String name;
    int icon;
    String code;

    public CountryCode(String name, int icon, String code) {
        this.name = name;
        this.icon = icon;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
