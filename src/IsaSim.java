import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
    static Integer[] progr;
    static String input_path = "tests/task1/shift2.bin";
    static {
        try {
            buf = Files.readAllBytes(Paths.get(input_path));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static Integer[] convert(byte[] buf) {
        ArrayList<Integer> list = new ArrayList<>();
        int offset = 0;
        for(int i = 0; i < buf.length / 4; i++) {

            list.add((buf[3 + offset] & 0xFF)<< 24 | ((buf[2 + offset] & 0xFF) << 16) |
                    ((buf[1 + offset] & 0xFF) << 8) | ((buf[0 + offset] & 0xFF)));
            offset += 4;
            System.out.println(Integer.toHexString(list.get(i)));
            if (list.get(i) == 4) { //4 is at the end for every .bin file. We do not know why
                list.remove(i);
                break;
            }
        }
        Integer[] arr = new Integer[list.size()];
        arr = list.toArray(arr);

        return arr;
    }

    public static void main(String[] args) {
        progr = convert(buf);
        System.out.println("Hello RISC-V World!");

        pc = 0;

        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.MAX_VALUE << 1);

        while(true) {

            int instr = progr[pc >> 2];
            int opcode = instr & 0x7f;
            int funct3 = (instr >> 12) & 0x7;
            int funct7 = (instr >> 25) & 0x7f;
            int rd = (instr >> 7) & 0x01f;
            int rs1 = (instr >> 15) & 0x01f;
            int rs2 = (instr >> 20) & 0x01f;
            int imm = (instr >> 20);
            int immTypeU = (instr >> 12);

            switch (opcode) {
                case 0x13:
                    switch (funct3) {
                        case 0x0: //addi
                            reg[rd] = reg[rs1] + imm;
                            break;
                        case 0x1: //slli
                            reg[rd] = reg[rs1] << (imm & 0x1f);
                            break;
                        case 0x2: //slti
                            if (rs1<imm) { reg[rd] = 1; }
                            else reg[rd] = 0;
                            break;
                        case 0x3: //sltiu
                            if ( (rs1 & 0xffffffffL) < (imm & 0xffffffffL)) { reg[rd] = 1; }
                            else reg[rd] = 0;
                            break;
                        case 0x4: //xori
                            reg[rd] = reg[rs1] ^ imm;
                            break;
                        case 0x5:
                            switch (funct7) {
                                case 0x0: //srli
                                    reg[rd] = reg[rs1] >>> (imm & 0x1f);
                                    break;
                                case 0x20: //srai
                                    reg[rd] = reg[rs1] >> (imm & 0x1f);
                                    break;
                                default:
                                    System.out.println("opcode: " + Integer.toHexString(opcode) + " funct3: " + Integer.toHexString(funct3) + "funct7" + Integer.toHexString(funct7) + " not yet implemented");
                                    break;
                            }
                            break;
                        case 0x6: //ori
                            reg[rd] = reg[rs1] | imm;
                            break;
                        case 0x7: //andi
                            reg[rd] = reg[rs1] & imm;
                            break;
                        default:
                            System.out.println("opcode: " + Integer.toHexString(opcode) + " funct3: " + Integer.toHexString(funct3) + " not yet implemented");
                            break;
                    }
                    break;
                case 0x33:
                    switch (funct3) {
                        case 0x0:
                            switch (funct7) {
                                case 0x0: //add
                                    reg[rd] = reg[rs1] + reg[rs2];
                                    break;
                                case 0x20: //sub
                                    reg[rd] = reg[rs1] - reg[rs2];
                                    break;
                                default:
                                    System.out.println("opcode: " + Integer.toHexString(opcode) + " funct3: " + Integer.toHexString(funct3) + "funct7" + Integer.toHexString(funct7) + " not yet implemented");
                                    break;
                            }
                            break;
                        case 0x1: //sll
                            reg[rd] = reg[rs1] << (reg[rs2] & 0x1f);
                            break;
                        case 0x2: //slt
                            if (rs1<rs2) { reg[rd] = 1; }
                            else reg[rd] = 0;
                            break;
                        case 0x3: //sltu
                            if ( (rs1 & 0xffffffffL) < (rs2 & 0xffffffffL)) { reg[rd] = 1; }
                            else reg[rd] = 0;
                            break;
                        case 0x4: //xor
                            reg[rd] = reg[rs1] ^ reg[rs2];
                            break;
                        case 0x5:
                            switch (funct7) {
                                case 0x0: //srl
                                    reg[rd] = reg[rs1] >>> (reg[rs2] & 0x1f);
                                    break;
                                case 0x20: //sra
                                    reg[rd] = reg[rs1] >> (reg[rs2] & 0x1f);
                                    break;
                                default:
                                    System.out.println("opcode: " + Integer.toHexString(opcode) + " funct3: " + Integer.toHexString(funct3) + "funct7" + Integer.toHexString(funct7) + " not yet implemented");
                                    break;
                            }
                            break;
                        case 0x6: //or
                            reg[rd] = reg[rs1] | reg[rs2];
                            break;
                        case 0x7: //and
                            reg[rd] = reg[rs1] & reg[rs2];
                            break;
                        default:
                            System.out.println("opcode: " + Integer.toHexString(opcode) + " funct3: " + Integer.toHexString(funct3) + " not yet implemented");
                    }
                    break;
                case 0x37: //lui
                    reg[rd] = (immTypeU << 12) & 0xFFFFF000;
                    break;
                case 0x73: //ecall
                    //TODO print everything
                    break;
                default:
                    System.out.println("Opcode " + Integer.toHexString(opcode)  + " not yet implemented");
                    break;
            }

            pc += 4; // One instruction is four bytes

            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            if ((pc >> 2) >= progr.length) {
                break;
            }
            System.out.println();
        }

        System.out.println("Program exit");

    }

}
