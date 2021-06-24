import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Processor
{
	int pc=0; //16 bit register, maximum value of 2048 //should default to zero?
	int[] instructMemory=new int[1024]; //each element must be 16 bits
	int instrCount;
	byte[] dataMemory=new byte[2048];	//each element must be 8 bits
	byte[] registerFile=new byte[64]; 	//there are 64 general-purpose registers only
	int[] statusRegister=new int[8];
	Object[] pipeline = new Object[3];
	
	public static ArrayList<String[]> readProgram(String s) 
	{
		//return an arrayList of each instruction in the program 
		//each instruction is split by space and put in an array
		
		ArrayList<String[]> program = new ArrayList<>();
		File file = new File(s);

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String command;
			while ((command = reader.readLine()) != null)
				program.add(command.split(" "));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("YOOOOO");
		System.out.println("Program to string now:");
		for(int i=0;i<program.size();i++) {
			String[] strLst=program.get(i);
			System.out.println(strLst[0]+" "+strLst[1]+" "+strLst[2]);
			
			}
		System.out.println();
		return program;
	}

	
	
	
	
	
	
	//converts the program from the path to instructions and save them in the instruction memory
	public void parse(String path) throws ProcessorException {
		ArrayList<String[]> program = readProgram(path);
		if(program.size()==0)
			throw new ProcessorException("Program is empty!");
			
		if(program.size() > instructMemory.length)
			throw new ProcessorException("Too long Program");
		Iterator<String[]> it = program.iterator();
		String op, r;
		int r0, r1;
		
		System.out.println("HERE");
		System.out.println(it.hasNext());
		
		if(instrCount != 0){
			instructMemory = new int[1024];
			instrCount = 0;
		}
		//the first operand is always a register
		while(it.hasNext()){
			String[] instr = it.next(); //current instruction we are parsing
			System.out.println(instr[0]+" "+instr[1]+" "+instr[2]);
			switch (instr[0]){
				case ("ADD"):
					op = "0000";
					r1 = Integer.parseInt(instr[2].substring(1)); //substring to get register number(remove the R)
					break;
				case ("SUB"):
					op = "0001";
					r1 = Integer.parseInt(instr[2].substring(1));
					break;
				case ("MUL"):
					op = "0010";
					r1 = Integer.parseInt(instr[2].substring(1));
					break;
				case ("MOVI"):
					op = "0011";
					if(instr[2].startsWith("R"))
						throw new ProcessorException("This is immediate instruction");
					r1 = Integer.parseInt(instr[2]);
					break;
				case ("BEQZ"):
					op = "0100";
					if(instr[2].startsWith("R"))
						throw new ProcessorException("This is immediate instruction");
					r1 = Integer.parseInt(instr[2]);
					break;
				case ("ANDI"):
					op = "0101";
					if(instr[2].startsWith("R"))
						throw new ProcessorException("This is immediate instruction");
					r1 = Integer.parseInt(instr[2]);
					break;
				case ("EOR"):
					op = "0110";
					r1 = Integer.parseInt(instr[2].substring(1));
					break;
				case ("BR"):
					op = "0111";
					r1 = Integer.parseInt(instr[2].substring(1));
					break;
				case ("SAL"):
					if(instr[2].startsWith("R"))
						throw new ProcessorException("This is immediate instruction");
					op = "1000";
					r1 = Integer.parseInt(instr[2]);
					break;
				case ("SAR"):
					if(instr[2].startsWith("R"))
						throw new ProcessorException("This is immediate instruction");
					op = "1001";
					r1 = Integer.parseInt(instr[2]);
					break;
				case ("LDR"):
					if(instr[2].startsWith("R"))
						throw new ProcessorException("This is immediate instruction");
					op = "1010";
					r1 = Integer.parseInt(instr[2]);
					break;
				case ("STR"):
					if(instr[2].startsWith("R"))
						throw new ProcessorException("This is immediate instruction");
					op = "1011";
					r1 = Integer.parseInt(instr[2]);
					break;
				default:
					throw new ProcessorException("Invalid Instruction");
			}
			r0 = Integer.parseInt(instr[1].substring(1));
			
			//r0 is the first operand must be a register
			//r1 is the second operand
			
			if(r0 < 0 || r0 > 63 || (r1 < 0 && instr[2].startsWith("R")) || (r1 > 63&& instr[2].startsWith("R")) || (r1 < -32 && !instr[2].startsWith("R")) || (r1 > 31 && !instr[2].startsWith("R")))
				throw new ProcessorException("Register Number Not Valid");
			r = op + toBinaryString(Integer.toBinaryString(r0)) + toBinaryString(Integer.toBinaryString(r1));
//			System.out.println(instrCount);
//			System.out.println(Integer.parseInt(r,2));
			instructMemory[instrCount] = Integer.parseInt(r,2);
			instrCount++;
		}
		System.out.println("instruction count is "+instrCount);
	}
	
	public static String toBinaryString(String a) {
		//used for the 2 operands, not the opcode!
		while(a.length()<6) {
			a="0"+a;
		}
		return a;
		
	}
	
	
	
	public int fetch(int pc) {
		return instructMemory[pc];
	}
	
	
	public byte[] decode(int instruction) {
        //decodes the instruction coming from fetch() and divides it into a string for the opcode, an int for R1 and an int for R2 or immediate
        String bin = Integer.toBinaryString(instruction);
      
        while(bin.length()<16) {
        	bin="0"+bin;
        	}
        
        String opcode= bin.substring(0,4);
        String operand1= bin.substring(4,10);
        String operand2= bin.substring(10,16);

        byte[] values=new byte[3];
        values[0]=(byte)Integer.parseInt(opcode,2);
        values[1]=(byte)Integer.parseInt(operand1,2);
        values[2]=(byte)Integer.parseInt(operand2,2);

        return values;
    }
	
	public void execute(byte opcode, byte operand1, byte operand2) 
	{
		System.out.println("Opcode is "+opcode);
		int intResult=0;
		switch (opcode) {
		case 0:
			registerFile[operand1]=(byte)(registerFile[operand1]+registerFile[operand2]);
			
			intResult=registerFile[operand1]+registerFile[operand2]; //wrong?
			statusRegister[4]=getLastBit(intResult);
			if(registerFile[operand1]>0 && registerFile[operand2]>0) {
				if(registerFile[operand1]<0)
					statusRegister[3]=1; //overflow bit V
			}
			else if( (registerFile[operand1]<0 && registerFile[operand2]<0)) {
				if(registerFile[operand1]>0)
					statusRegister[3]=1;
			}
			updateNFlag(registerFile[operand1]);
			updateSFlag();
			updateZFlag(registerFile[operand1]);
			break;
		case 1:
			registerFile[operand1]=(byte)(registerFile[operand1]-registerFile[operand2]);
			intResult=registerFile[operand1]-registerFile[operand2];
			statusRegister[4]=getLastBit(intResult);
			if(registerFile[operand1]>0 && registerFile[operand2]<0) {
				if(registerFile[operand1]<0)
					statusRegister[3]=1;
			}
			else if( (registerFile[operand1]<0 && registerFile[operand2]>0)) {
				if(registerFile[operand1]>0)
					statusRegister[3]=1;
			}
			updateNFlag(registerFile[operand1]);
			updateSFlag();
			updateZFlag(registerFile[operand1]);
			break;
		case 2:
			registerFile[operand1]=(byte)(registerFile[operand1]*registerFile[operand2]);
			intResult=registerFile[operand1]*registerFile[operand2];
			statusRegister[4]=getLastBit(intResult);
			updateNFlag(registerFile[operand1]);
			updateZFlag(registerFile[operand1]);
			break;
		case 3:
			System.out.println("In MOVI operand2 is "+operand2);
			System.out.println("In MOVI operand1 is "+operand1);
			registerFile[operand1]=operand2;
			break;
		case 4:
			System.out.println("In case of BEZQ");
			if(registerFile[operand1]==0)
				incrementPC(operand2);
			break;
		case 5:
			registerFile[operand1]=(byte) (registerFile[operand1]&operand2);
			updateNFlag(registerFile[operand1]);
			updateZFlag(registerFile[operand1]);
			break;
		case 6:
			registerFile[operand1]=(byte) (registerFile[operand1]^registerFile[operand2]);
			updateNFlag(registerFile[operand1]);
			updateZFlag(registerFile[operand1]);
			break;
		case 7:
			pc=Integer.parseInt(registerFile[operand1]+""+registerFile[operand2]);
			break;
		case 8:
			registerFile[operand1]=(byte) (registerFile[operand1]<<operand2);
			updateNFlag(registerFile[operand1]);
			updateZFlag(registerFile[operand1]);
			break;
		case 9:
			registerFile[operand1]=(byte) (registerFile[operand1]>>operand2);
			updateNFlag(registerFile[operand1]);
			updateZFlag(registerFile[operand1]);
			break;
		case 10:
			registerFile[operand1]=dataMemory[operand2];
			break;
		case 11:
			System.out.println("Data Memory operand1 is "+operand1);
			System.out.println("Data Memory operand2 is "+operand2);
			dataMemory[operand2]=registerFile[operand1];
			break;
		}
	}
	
	public void incrementPC(int value) {
		//pc++;
		if(pc+value>2048)
			pc=0;
		else
			pc+=value;
	}
	
	public void updateNFlag(byte result) {
		if (result<0)
			statusRegister[2]=1;
		else
			statusRegister[2]=0;
	}
	
	public void updateSFlag() {
		statusRegister[1]=statusRegister[3]^statusRegister[2];
	}
	
	public void updateZFlag(byte result) {
		if(result==0)
			statusRegister[0]=1;
		else
			statusRegister[0]=0;
	}
	
	public static int getLastBit(int number)
	{
		return Integer.parseInt((Integer.toBinaryString(number).charAt(0)+""));
	}
	
	public void startExecution(String path) throws ProcessorException {
		this.parse(path);
		int cycles=0;
		boolean branch=false;
		boolean executing=true;
		pipeline[0]=-1; //arbitrary value to make it not null
		cycles++;
		while(executing) {
			
			//display stuff
			System.out.println("Clock Cycle is "+cycles);
			System.out.println("PC is "+pc);
			
			if(branch==true) {
				System.out.println("-------------");
				System.out.println("BRANCH JUST OCCURED");
				System.out.println("-------------");
				
				byte operand1=((byte[])pipeline[2])[2];
				pipeline=new Object[3];  //flush entire pipeline (discard sequential instruction in stages before execute)
				pipeline[0]=pc+1+operand1;
			}
			else if(pipeline[0]==null &&pipeline[1]==null) { //last cycle (unless branching occurs)
				
				System.out.println("CASE 1");
				byte opcode=((byte[])pipeline[2])[0];
				byte operand1=((byte[])pipeline[2])[1];
				byte operand2=((byte[])pipeline[2])[2];
				execute(opcode,operand1,operand2);
				
				if( opcode==7 || (opcode==4 && operand1==0))
					branch=true;
				
			}
			else if(pipeline[0]==null) { //cycle before cycle (unless branching occurs)
				System.out.println("CASE 2");
				pipeline[1]=decode((Integer)pipeline[1]);		
				byte opcode=((byte[])pipeline[2])[0];
				byte operand1=((byte[])pipeline[2])[1];
				byte operand2=((byte[])pipeline[2])[2];
				execute(opcode,operand1,operand2);
				
				if( opcode==7 || (opcode==4 && operand1==0))
					branch=true;
			}
			else if(pipeline[1]==null&&pipeline[2]==null) {
				System.out.println("CASE 3");
				pipeline[0]=fetch(pc);
				System.out.println("Pipeline[0] is "+pipeline[0]);
			}
			else if(pipeline[2]==null) {
				System.out.println("CASE 4");
				pipeline[0]=fetch(pc);
				pipeline[1]=decode((Integer)pipeline[1]); 
				
				byte opcode=((byte[])pipeline[1])[0]; //just before the pipeline shift happened (so 2nd element not 3rd not a typo)
				byte operand1=((byte[])pipeline[1])[1];
				if( opcode==7 || (opcode==4 && operand1==0))
					branch=true;

			}
			else {
				System.out.println("CASE 5");
				pipeline[0]=fetch(pc);
				pipeline[1]=decode((Integer)pipeline[1]);	//It is shifted to the next position and is handled in the next cycle		

				byte opcode=((byte[])pipeline[2])[0];
				byte operand1=((byte[])pipeline[2])[1];
				byte operand2=((byte[])pipeline[2])[2];
				execute(opcode,operand1,operand2);
				
				if( opcode==7 || (opcode==4 && operand1==0))
					branch=true;
			}
			
			cycles++;
			pc++;
			
			Object nextInst=null;
			if(pc<instrCount) {
				nextInst=pc;
			}
			System.out.println("Next Instruction is "+nextInst);
			pipeline=shiftPipeline(pipeline,nextInst); //null should change to the next instruction (the one pc points to)
			
			System.out.println("Pipeline[0] is "+pipeline[0]);
			System.out.println("Pipeline[1] is "+pipeline[1]);
			System.out.println("Pipeline[1] is "+pipeline[2]);
//			System.out.println("Pipeline[2] is {"+((byte[])pipeline[2])[0]+","+((byte[])pipeline[2])[1]+","+((byte[])pipeline[2])[2]+"}");
			
			if(pipeline[0]==null&& pipeline[1]==null&&pipeline[2]==null)
				break;
//			if(pc==instrCount)//false it is more?
//				break;
			
			System.out.println();
		}
		System.out.println("Number of cycles is "+cycles);
		System.out.println("At the end PC is "+pc);		
	}
	
	
	public static Object[] shiftPipeline(Object[] pipeline,Object newFetch) {
		Object first=pipeline[0];
		Object second=pipeline[1];
		Object[] pipe=new Object[3];
		pipe[0]=newFetch;;
		pipe[1]=first;
		pipe[2]=second;
		return pipe;
	}
	
	public static void main(String[] args)
	{
		Processor processor=new Processor();
		try{
			System.out.println("AAA\n");
			processor.startExecution("src/Program1.txt");
			System.out.println("\nZZZZ");
		
		}
		catch(Exception e) {}
		
		
		int r1=processor.registerFile[1];
		int r0=processor.registerFile[0];
		int r3=processor.registerFile[3];
		int r4=processor.registerFile[4];
		int r5=processor.registerFile[5];
		
		

		System.out.println("R0 is "+r0);
		System.out.println("R1 is "+r1);
		System.out.println("R3 is "+r3);
		System.out.println("R4 is "+r4);
		System.out.println("R5 is "+r5);
		
		System.out.println(processor.dataMemory[3]);
	
//		processor.execute("0011000101000011");
//		byte opcode=toBinaryString("0011");
//		byte operand1=;
//		byte operand2=;
//		
//		processor.execute(,"000101","000011");
//	
	
	
	}
}
























