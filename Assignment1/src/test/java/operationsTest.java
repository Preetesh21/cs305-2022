import Classes.actor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void main() {
    }

    @Test
    void connection() {
    }

    @Test
    void propagatePropsToQuery() {
    }

    @Test
    void propagatePropsToQuery2() {
    }

    @Test
    void selectOne() {
        List<String> tt;
        tt=new ArrayList<>();
        tt.add("RamosIV");
        new_obj.selectOne("findActor",tt,actor.class);
    }

    @Test
    void selectMany() {
        String[] names={"RamosIV"};
        new_obj.selectMany("findActors", names,Classes.actor.class);
    }

    @Test
    void update() {
        param1 new_param=new param1();
        new_param.propX= new ArrayList<>();
        new_param.propX.add("RamosIV");
        new_param.propY="PECK";
        new_obj.update("updateActor",new_param);
    }

    @Test
    void insert() {
        Timestamp CreatedDate = Timestamp.valueOf("2022-02-10 01:42:42");
        actor new_actor=new actor(100,"Sergio","Alves",CreatedDate);
        new_obj.insert("addActor", new_actor);
    }

    @Test
    void delete() {
        new_obj.delete("deleteActor", 100);
    }
}