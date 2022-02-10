import Classes.actor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class operationsTest {

    operations new_obj;
    @BeforeEach
    void setUp() {
        new_obj=new operations();
    }

    @AfterEach
    void tearDown() {
        new_obj=null;
    }


    @Test
    void connection() throws ClassNotFoundException, SQLException {
        Connection conn = new_obj.Connection(1);
        assertNotNull(conn);
        Connection conn2 = new_obj.Connection(-1);
        assertNull(conn2);
    }

    @Test
    void attempt(){
        xml_parser.find("wewe");
    }

    @Test
    void queryHelper() throws SQLException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Connection conn = new_obj.Connection(1);
        String sql="Select * from actor where actor_id=${propX};";
        new_obj.queryHelper(sql,1,conn);
    }

    @Test
    void insertObjectHelper() throws SQLException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Connection conn = new_obj.Connection(1);
        String sql="Select * from actor where actor_id=(${propX});";
        new_obj.insertObjectHelper(sql,1,conn);
    }

    @Test
    void selectOne() {
        List<String> tt;
        tt=new ArrayList<>();
        tt.add("KILMER");
        actor hero;
        hero= (actor) new_obj.selectOne("findActor",tt,actor.class);
        assertNotNull(hero);
        assert hero.last_name.equals("KILMER");

        actor obj0;
        obj0=(actor)new_obj.selectOne("findActorString","KILMER",actor.class);
        assertNotNull(obj0);

        actor obj1;
        obj1=(actor)new_obj.selectOne("findActor",tt,null);
        assertNull(obj1);

        List<String> tt2;
        tt2=new ArrayList<>();
        tt2.add("P");
        actor obj2;
        obj2=(actor) new_obj.selectOne("findActor",tt2,actor.class);
        assertNull(obj2);

        int[] ids={1,2};
        actor obj3;
        obj3=(actor) new_obj.selectOne("findActorArray",ids,actor.class);
        assert obj3==null;

        int id =1;
        actor obj4;
        obj4=(actor) new_obj.selectOne("findActorInt",id,actor.class);
        assertNotNull(obj4);

        actor obj5;
        obj5=(actor) new_obj.selectOne("findActors",tt2,actor.class);
        assertNull(obj5);

        Object obj6;
        obj6=new_obj.selectOne("findActors","P",actor.class);
        assertNull(obj6);
    }

    @Test
    void selectMany() {
        String[] names={"KILMER"};
        int[] array={4,5,6,7};
        List<Object> obj1,obj2,obj3;
        obj1= (List<Object>) new_obj.selectMany("findActorss", names, actor.class);
        assertNotNull(obj1);

        assert (obj1.size()==1);
        obj2= (List<Object>) new_obj.selectMany("findActors", names,null);
        assertNull(obj2);
        obj3= (List<Object>) new_obj.selectMany("findActorArray",array, actor.class);
        assertNotNull(obj3);
        assert (obj3.size()==4);

        List<String> tt2;
        tt2=new ArrayList<>();
        tt2.add("P");
        Object obj4;
        obj4= new_obj.selectMany("findActors",tt2,actor.class);
        assertNull(obj4);

        Object obj6;
        obj6=new_obj.selectMany("findActors","P",actor.class);
        assertNull(obj6);

    }

    @Test
    void update() {
        param1 new_param=new param1();
        new_param.propX= new ArrayList<>();
        new_param.propX.add("PECK");
        new_param.propY="RamosIV";
        int a=new_obj.update("updateActor",new_param);
        System.out.println(a);
        //assert a==1;

        int b=new_obj.update("updateActor",null);
        assert b==-1;

        int c=new_obj.update("updateActors",new_param);
        assert c==-1;
        Timestamp CreatedDate = Timestamp.valueOf("2022-02-10 02:42:42");
        List<Timestamp> time;
        time=new ArrayList<>();
        time.add(CreatedDate);
        int f=new_obj.update("Actors",time);
        System.out.println(f);
    }

    @Test
    void insert() {
        Timestamp CreatedDate = Timestamp.valueOf("2022-02-10 01:42:42");
        actor new_actor=new actor(100,"Sergio","Alves",CreatedDate);
        int a=new_obj.insert("addActor", new_actor);
        assert a==1;
        int b=new_obj.insert("addActor", null);
        assert b==-1;
        int c=new_obj.insert("addActors", new_actor);
        assert c==-1;
    }

    @Test
    void last() {
        int c;
        int a=new_obj.delete("deleteActor", 100);
        System.out.println(a);
        assert a==1;
        int b=new_obj.delete("deleteActor", null);
        System.out.println(b);
        assert b==-1;
        int d=new_obj.delete("deleteActors", 100);
        System.out.println(d);
        assert d==-1;
    }
}