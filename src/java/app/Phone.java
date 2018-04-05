package app;

public class Phone {
    private String id;
    private String number;

    public Phone() {
    }

    public Phone(String id) {
        this.id = id;
    }

    public Phone(String id, String number) {
        this.id = id;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


}
