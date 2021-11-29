import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


class IsaSimTest {

    //Assuming the files to compare is in tests/ and in testOutput.
    public void testBinaryDump(String folder,String fileName) throws IOException, ClassNotFoundException {
        IsaSim.main(new String[] {"tests/"+ folder + fileName + ".bin","testOutput/" + fileName + ".res"});

        byte[] buf;
        buf = Files.readAllBytes(Paths.get("tests/" + folder + fileName + ".res"));
        Integer[] arrRes = IsaSim.convert(buf);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                "testOutput/" + fileName + ".res"));
        int[] ia = (int[]) (ois.readObject());
        ois.close();

        assertEquals(arrRes.length, ia.length);
        for (int i = 0; i < ia.length; i++) {
            assertEquals((int) arrRes[i], ia[i]); //Checking that each of the registers are the same.
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
        testBinaryDump("task3/","loop");
        testBinaryDump("task3/","recursive");
        testBinaryDump("task3/","string");
    }

    @org.junit.jupiter.api.Test
    void task4Tests() throws IOException, ClassNotFoundException {
        testBinaryDump("task4/","t1");
        testBinaryDump("task4/","t2");
        testBinaryDump("task4/","t3");
        testBinaryDump("task4/","t4");
        testBinaryDump("task4/","t5");
        testBinaryDump("task4/","t6");
        testBinaryDump("task4/","t7");
        testBinaryDump("task4/","t8");
        testBinaryDump("task4/","t9");
        testBinaryDump("task4/","t10");
        testBinaryDump("task4/","t11");
        testBinaryDump("task4/","t12");
        testBinaryDump("task4/","t13");
        testBinaryDump("task4/","t14");
        testBinaryDump("task4/","t15");
    }

    // Extra testing.
    @org.junit.jupiter.api.Test
    void instructionTests() throws IOException, ClassNotFoundException {
        testBinaryDump("InstructionTests/","test_add");
        testBinaryDump("InstructionTests/","test_addi");
        testBinaryDump("InstructionTests/","test_and");
        testBinaryDump("InstructionTests/","test_andi");
        testBinaryDump("InstructionTests/","test_auipc");
        testBinaryDump("InstructionTests/","test_beq");
        testBinaryDump("InstructionTests/","test_bge");
        testBinaryDump("InstructionTests/","test_bgeu");
        testBinaryDump("InstructionTests/","test_blt");
        testBinaryDump("InstructionTests/","test_bne");
        testBinaryDump("InstructionTests/","test_jal");
        testBinaryDump("InstructionTests/","test_jalr");
    }

}