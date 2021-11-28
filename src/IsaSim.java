
import sun.misc.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

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
    static byte[] memoryArr;

    static byte[] buf;
    static String output_path;
    private static boolean debuggingMode = true; //if set to true registers will be printed after every instruction

    public static Integer[] convert(byte[] buf) {
        ArrayList<Integer> list = new ArrayList<>();
        int offset = 0;

        for(int i = 0; i < buf.length / 4; i++) {

            list.add((buf[3 + offset] & 0xFF)<< 24 | ((buf[2 + offset] & 0xFF) << 16) |
                    ((buf[1 + offset] & 0xFF) << 8) | ((buf[0 + offset] & 0xFF)));
            offset += 4;
            if (debuggingMode) {
                System.out.println(Integer.toHexString(list.get(i)));
            }
        }
        Integer[] arr = new Integer[list.size()];
        arr = list.toArray(arr);

        return arr;
    }

    public static void main(String[] args) throws IOException {
        //Read file name
        if (args.length < 2) {
            System.out.println("Expecting two arguments: path to input file, path to output file");
        }

        try {
            buf = Files.readAllBytes(Paths.get(args[0]));
            memoryArr = new byte[1000000 + buf.length];
        } catch (IOException e) {
            System.out.println("INPUT FILE NOT FOUND!");
            e.printStackTrace();
        }
        output_path = args[1];

        //placing the instructions in memory
        for (int i = 0; i < buf.length; i++) {
            memoryArr[i] = buf[i];
        }


        System.out.println("Hello RISC-V World!");
        pc = 0;
        while(true) {

            int instr = ((memoryArr[3 + pc] & 0xFF)<< 24 | ((memoryArr[2 + pc] & 0xFF) << 16) |
                    ((memoryArr[1 + pc] & 0xFF) << 8) | ((memoryArr[0 + pc] & 0xFF))); //loading in the instruction from memory
            System.out.println(Integer.toHexString(instr));
            int opcode = instr & 0x7f;
            int funct3 = (instr >> 12) & 0x7;
            int funct7 = (instr >> 25) & 0x7f;
            int rd = (instr >> 7) & 0x01f;
            int rs1 = (instr >> 15) & 0x01f;
            int rs2 = (instr >> 20) & 0x01f;
            int imm = (instr >> 20);
            int immTypeU = (instr >> 12);

            switch (opcode) {
                case 0x3: // load instructions
                    switch (funct3) {
                        case 0x0: //lb
                            reg[rd] = memoryArr[reg[rs1] + imm];
                            break;
                        case 0x1: //lh
                            int tmpRes = 0;
                            tmpRes = tmpRes | (memoryArr[reg[rs1]+ imm] & 0xFF);
                            tmpRes = tmpRes | (memoryArr[reg[rs1]+ imm + 1] << 8);
                            reg[rd] = tmpRes;
                            break;
                        case 0x2: //lw
                            tmpRes = 0;
                            for (int i = 0; i < 4; i++) {
                                tmpRes = tmpRes | ((memoryArr[reg[rs1]+ imm + i] & 0xFF) << i*8);
                            }
                            reg[rd] = tmpRes;
                            break;
                        case 0x4: //lbu
                            reg[rd] = memoryArr[reg[rs1] + imm] & 0xFF;
                            break;
                        case 0x5: //lhu
                            tmpRes = 0;
                            for (int i = 0; i < 2; i++) {
                                tmpRes = tmpRes | ((memoryArr[reg[rs1]+ imm + i] & 0xFF) << i*8);
                            }
                            reg[rd] = tmpRes;
                            break;
                        default:
                            System.out.println("opcode: " + Integer.toHexString(opcode) + " funct3: " + Integer.toHexString(funct3) + " not yet implemented");
                    }
                    break;
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
                case 0x17: //auipc
                    reg[rd] =pc + ((immTypeU << 12) & 0xFFFFF000);
                    break;
                case 0x23: // store instructions
                    //constructing immediate for store instructions
                    int immStore = rd | funct7 << 5;
                    switch (funct3) {
                        case 0x0: //sb
                            memoryArr[reg[rs1]+immStore] = (byte) reg[rs2];
                            break;
                        case 0x1: //sh
                            for (int i = 0; i < 2; i++) {
                                memoryArr[reg[rs1]+immStore+i] = (byte) (reg[rs2] >> i*8);
                            }
                            break;
                        case 0x2: //sw
                            for (int i = 0; i < 4; i++) {
                                memoryArr[reg[rs1]+immStore+i] = (byte) (reg[rs2] >> i*8);
                            }
                            break;
                        default:
                            System.out.println("opcode: " + Integer.toHexString(opcode) + " funct3: " + Integer.toHexString(funct3) + " not yet implemented");
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
                case 0x63: //branches //note rd is the lower offset and func7 is the higher
                    switch (funct3) {
                        case 0x0: //beq
                            if (reg[rs1] == reg[rs2]) {
                                pc = pc + calculateBranch(funct7,rd);
                                continue;
                            }
                            break;
                        case 0x1: //bne
                            if (reg[rs1] != reg[rs2]) {
                                pc = pc + calculateBranch(funct7,rd);
                                continue;
                            }
                            break;
                        case 0x4: //blt
                            if (reg[rs1] < reg[rs2]) {
                                pc = pc + calculateBranch(funct7,rd);
                                continue;
                            }
                            break;
                        case 0x5: //bqe
                            if (reg[rs1] >= reg[rs2]) {
                                pc = pc + calculateBranch(funct7,rd);
                                continue;
                            }
                            break;
                        case 0x6: //bltu
                            if ((reg[rs1]& 0xffffffffL) < (reg[rs2]& 0xffffffffL)) {
                                pc = pc + calculateBranch(funct7,rd);
                                continue;
                            }
                        case 0x7: //bqeu
                            if ((reg[rs1]& 0xffffffffL) >= (reg[rs2]& 0xffffffffL)) {
                                pc = pc + calculateBranch(funct7,rd);
                                continue;
                            }
                            break;
                        default:
                            System.out.println("opcode: " + Integer.toHexString(opcode) + " funct3: " + Integer.toHexString(funct3) + " not yet implemented");
                    }
                    break;
                case 0x73: //ecall
                    if (true) {
                        for (int i = 0; i < reg.length; ++i) {
                            System.out.print(reg[i] + " ");
                        }
                        exit();
                        return;
                    }
                    break;
                case 0x6f: //jal
                    // Creating the offset:
                    int offset = imm & 0x7FE;
                    offset = offset | ((imm & 0x1) << 11);
                    offset = offset | (instr & 0xFF000);
                    offset = offset | ((imm & 0x800) << 20);
                    if ((imm & 0x800) >> 11 == 1) { //if the last bit is 1 sign extend with mask
                        offset = offset | 0xFFF80000;
                    }
                    reg[rd] = pc + 4;
                    pc += offset;
                    continue;
                case 0x67: //jalr
                    reg[rd] = pc + 4;
                    pc = reg[rs1] + imm;
                    continue;
                default:
                    System.out.println("Opcode " + Integer.toHexString(opcode)  + " not yet implemented");
                    break;
            }
            //If something was written to x0 remove it
            reg[0] = 0;

            pc += 4; // One instruction is four bytes

            if (debuggingMode) {
                for (int j : reg) {
                    System.out.print(j + " ");
                }
                System.out.println();
            }
            if ((pc >> 2) >= memoryArr.length) {
                break;
            }
        }
        exit();

    }

    private static void exit() throws IOException {
        //Creating a binary dump of the output.
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                output_path));
        oos.writeObject(reg);
        oos.close();
        Arrays.fill(reg,0);
        Arrays.fill(memoryArr, (byte) 0);

        System.out.println("Program exit");
    }

    public static int calculateBranch(int funct7, int rd) {
        int tmpRes = 0;
        tmpRes = tmpRes | (rd & 0x1E);
        tmpRes = tmpRes | (funct7 & 0x3F) << 5;
        tmpRes = tmpRes | (rd & 0x1) << 11;
        if ((funct7 & 0x40) >> 6 == 1) {
            tmpRes = tmpRes | 0xFFFFF000;
        }
        return tmpRes;
    }

}
