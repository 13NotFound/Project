package team.re4group1.Utils;

import java.util.ArrayList;
import java.util.Objects;


public class RecourseLoader {
    /**
     * Recourse loader to avoid NullPointerException
     *
     * @param recourseObjects recourse Objects
     * @return String array of file path
     */
    public static ArrayList<String> load(RecourseObject... recourseObjects){
        ArrayList<String> paths = new ArrayList<>();
        for (RecourseObject recourseObject : recourseObjects) {
            try {
                paths.add(Objects.requireNonNull(
                        recourseObject.pathClass().getResource(recourseObject.filePaths())
                ).toExternalForm());
            } catch (NullPointerException ignored){}
        }

        return paths;
    }
}
