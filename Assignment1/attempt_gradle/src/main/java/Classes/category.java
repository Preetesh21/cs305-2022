package Classes;

import java.sql.Timestamp;

public class category {
    public   int actor_id;
    public   String name;
    public Timestamp last_update;

    public category(){
        actor_id=0;
        name=null;
        last_update=null;
    }
    public category(int actor_id_, String name_, java.sql.Timestamp last_update_)
    {
        this.actor_id = actor_id_;
        this.name = name_;
        this.last_update = last_update_;
    }
}
