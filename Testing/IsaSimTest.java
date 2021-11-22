import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


class IsaSimTest {

    public void testBinaryDump(String folder,String fileName) throws IOException, ClassNotFoundException {
        IsaSim.main(new String[] {"tests/"+ folder + fileName + ".bin","testOutput/" + fileName + ".res"});

        byte[] buf;
        buf = Files.readAllBytes(Paths.get("tests/" + folder + fileName + ".res"));
        Integer[] arrRes = IsaSim.convert(buf);
        //Read objects or arrays from binary file "o.dat":
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                "testOutput/" + fileName + ".res"));
        int[] ia = (int[]) (ois.readObject());
        ois.close();

        assertEquals(arrRes.length, ia.length);
        for (int i = 0; i < ia.length; i++) {
            assertEquals((int) arrRes[i], ia[i]);
        }
    }


    @org.junit.jupiter.api.Test
    void task1Tests() throws IOException, ClassNotFoundException {
        testBinaryDump("task1/","addlarge");
        testBinaryDump("task1/","addneg");
        testBinaryDump("task1/","addpos");
        testBinaryDump("task1/","bool");
        testBinaryDump("task1/","set");
        testBinaryDump("task1/","shift");
        testBinaryDump("task1/","shift2");
    }

    @org.junit.jupiter.api.Test
    void task2Tests() throws IOException, ClassNotFoundException {
        testBinaryDump("task2/","branchcnt");
        testBinaryDump("task2/","branchmany");
        testBinaryDump("task2/","branchtrap");
    }

    @org.junit.jupiter.api.Test
    void task3Tests() throws IOException, ClassNotFoundException {
        testBinaryDump("task3/","width");
        testBinaryDump("task3/","string");
    }
}