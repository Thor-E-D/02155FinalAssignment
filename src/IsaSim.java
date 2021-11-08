import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * RISC-V Instruction Set Simulator
 * <p>
 * A tiny first step to get the simulator started. Can execute just a single
 * RISC-V instruction.
 *
 * @author Martin Schoeberl (martin@jopdesign.com)
 */
public class IsaSim {

    static int pc;
    static int reg[] = new int[32];

    // Here the first program hard coded as an array
    static byte[] buf;
    static int[] progr;
    static {
        try {
            buf = Files.readAllBytes(Paths.get("tests\\task1\\addlarge.bin"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static int[] convert(byte buf[]) {


        int intArr[] = new int[buf.length / 4];
        int offset = 0;
        for(int i = 0; i < intArr.length; i++) {

            intArr[i] = (buf[3 + offset] & 0xFF)<< 24 | ((buf[2 + offset] & 0xFF) << 16) |
                    ((buf[1 + offset] & 0xFF) << 8) | ((buf[0 + offset] & 0xFF) );
            offset += 4;
            System.out.println(Integer.toHexString(intArr[i]));
        }
        return intArr;
    }

    public static void main(String[] args) {
        progr = convert(buf);
        System.out.println("Hello RISC-V World!");
        System.out.println(progr.length);
        pc = 0;

        for (; ; ) {

            int instr = progr[pc >> 2];
            int opcode = instr & 0x7f;
            int rd = (instr >> 7) & 0x01f;
            int rs1 = (instr >> 15) & 0x01f;
            int rs2 = (instr >> 20) & 0x01f;
            int imm = (instr >> 20);
            boolean flag = false;
            switch (opcode) {

                case 0x13:
                    reg[rd] = reg[rs1] + imm;
                    break;
                case 0x33:
                    reg[rd] = reg[rs1] + reg[rs2];
                    break;
                case 73:
                    flag = true;
                    break;
                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }

            pc += 4; // One instruction is four bytes

            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            if ((pc >> 2) >= progr.length) {
                break;
            } if(flag) break;
            System.out.println();
        }

        System.out.println("Program exit");

    }

}
