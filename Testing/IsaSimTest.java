import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


class IsaSimTest {

    //Assuming the files to compare is in tests/ and in testOutput.
    public void testBinaryDump(String folder,String fileName, String debuggingMode) throws IOException, ClassNotFoundException {
        IsaSim.main(new String[] {folder + fileName + ".bin","testOutput/" + fileName + ".res",debuggingMode});

        byte[] buf;
        buf = Files.readAllBytes(Paths.get(folder + fileName + ".res"));
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
        testBinaryDump("tests/task1/","addlarge", "true");
        testBinaryDump("tests/task1/","addneg", "false");
        testBinaryDump("tests/task1/","addpos", "false");
        testBinaryDump("tests/task1/","bool", "false");
        testBinaryDump("tests/task1/","set", "false");
        testBinaryDump("tests/task1/","shift", "false");
        testBinaryDump("tests/task1/","shift2", "false");
    }

    @org.junit.jupiter.api.Test
    void task2Tests() throws IOException, ClassNotFoundException {
        testBinaryDump("tests/task2/","branchcnt", "false");
        testBinaryDump("tests/task2/","branchmany", "false");
        testBinaryDump("tests/task2/","branchtrap", "false");
    }

    @org.junit.jupiter.api.Test
    void task3Tests() throws IOException, ClassNotFoundException {
        testBinaryDump("tests/task3/","width", "false");
        testBinaryDump("tests/task3/","loop", "false");
        testBinaryDump("tests/task3/","recursive", "false");
        testBinaryDump("tests/task3/","string", "false");
    }

    @org.junit.jupiter.api.Test
    void task4Tests() throws IOException, ClassNotFoundException {
        testBinaryDump("tests/task4/","t1", "false");
        testBinaryDump("tests/task4/","t2", "false");
        testBinaryDump("tests/task4/","t3", "false");
        testBinaryDump("tests/task4/","t4", "false");
        testBinaryDump("tests/task4/","t5", "false");
        testBinaryDump("tests/task4/","t6", "false");
        testBinaryDump("tests/task4/","t7", "false");
        testBinaryDump("tests/task4/","t8", "false");
        testBinaryDump("tests/task4/","t9", "false");
        testBinaryDump("tests/task4/","t10", "false");
        testBinaryDump("tests/task4/","t11", "false");
        testBinaryDump("tests/task4/","t12", "false");
        testBinaryDump("tests/task4/","t13", "false");
        testBinaryDump("tests/task4/","t14", "false");
        testBinaryDump("tests/task4/","t15", "false");
    }

    // Extra testing.
    @org.junit.jupiter.api.Test
    void instructionTests() throws IOException, ClassNotFoundException {
        testBinaryDump("tests/InstructionTests/","test_add", "false");
        testBinaryDump("tests/InstructionTests/","test_addi", "false");
        testBinaryDump("tests/InstructionTests/","test_and", "false");
        testBinaryDump("tests/InstructionTests/","test_andi", "false");
        testBinaryDump("tests/InstructionTests/","test_auipc", "false");
        testBinaryDump("tests/InstructionTests/","test_beq", "false");
        testBinaryDump("tests/InstructionTests/","test_bge", "false");
        testBinaryDump("tests/InstructionTests/","test_bgeu", "false");
        testBinaryDump("tests/InstructionTests/","test_blt", "false");
        testBinaryDump("tests/InstructionTests/","test_bne", "false");
        testBinaryDump("tests/InstructionTests/","test_jal", "false");
        testBinaryDump("tests/InstructionTests/","test_jalr", "false");
    }

}