package com.example.alek.shoppinglist;

        import io.realm.RealmObject;
        import io.realm.annotations.PrimaryKey;

/**
 * Created by Damian on 2017-09-21.
 */

public class ShoppingItem extends RealmObject {

    private String name;
    private String quantity;
    private boolean state;

    @PrimaryKey
    private String id;

    private long timestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
