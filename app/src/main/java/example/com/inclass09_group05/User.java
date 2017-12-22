package example.com.inclass09_group05;

import java.io.Serializable;

/**
 * Created by panindra on 11/7/17.
 */

public class User implements Serializable {
    String id, title;
    Boolean myMessage = false;

    public Boolean getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(Boolean myMessage) {
        this.myMessage = myMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", myMessage=" + myMessage +
                '}';
    }
}

