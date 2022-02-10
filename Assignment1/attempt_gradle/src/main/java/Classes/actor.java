package Classes;

import java.sql.Timestamp;

public class actor
{
    public   int actor_id;
    public   String first_name;
    public   String last_name;
    public   Timestamp last_update;

    public actor(){
        actor_id=0;
        first_name=null;
        last_name=null;
        last_update=null;
    }
    public actor(int actor_id_, String first_name_, String last_name_, java.sql.Timestamp last_update_)
    {
        this.actor_id = actor_id_;
        this.first_name = first_name_;
        this.last_name = last_name_;
        this.last_update = last_update_;
    }
}