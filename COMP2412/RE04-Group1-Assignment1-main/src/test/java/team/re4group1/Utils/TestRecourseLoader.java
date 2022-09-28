package team.re4group1.Utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import team.re4group1.App;

public class TestRecourseLoader {

    @Test
    void testLoad(){
        assertArrayEquals(new String[]{}, RecourseLoader.load(new RecourseObject(App.class, "fileWhichNotExist")).toArray());
        assertArrayEquals(
                new String[]{TestRecourseLoader.class.getResource("TestApp.css").toExternalForm()},
                RecourseLoader.load(new RecourseObject(TestRecourseLoader.class, "TestApp.css")).toArray()
        );
        RecourseLoader recourseLoader = new RecourseLoader();
        assertEquals("class team.re4group1.Utils.RecourseLoader", recourseLoader.getClass().toString());
    }

}
