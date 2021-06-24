
public class Main {

	public static void main(String[] args) {
//		String bin="1111000000111111";
//		String opcode= bin.substring(0,4);
//		String operand1= bin.substring(4,10);
//		String operand2= bin.substring(10,16);
//	
//		Object[] values=new Object[3];
//		values[0]=Integer.parseInt(opcode,2);
//		values[1]=Integer.parseInt(operand1,2);
//		values[2]=Integer.parseInt(operand2,2);
//		
//		for(Object value:values) {
//			System.out.println(value.toString());
//		}
	
		byte[] a=new byte[3];
		Object[] huss=new Object[3];
		System.out.println(huss[0]);
	
		Object obj=a;
		
		byte b=((byte[])obj)[0];
	
	
		Processor processor=new Processor();
		
		byte qq=0;
		boolean bool= (qq==0);
		System.out.println(bool);
		
//		try{
//
//			processor.parse("src/Program1.txt");
//			System.out.println("Yoo");
//			System.out.println();
//		
//		}
//		catch(Exception e) {}
//		System.out.println("PC is "+processor.pc);
//		int instruction=processor.fetch(processor.pc); //is 12291
//		System.out.println("Instruction is "+instruction);
//		byte[] instructionDecoded=processor.decode(instruction);
//		byte opcode=instructionDecoded[0];
//		byte operand1=instructionDecoded[1];
//		byte operand2=instructionDecoded[2];
//		System.out.println("Opcode is "+opcode);
//		System.out.println("Operand 1 is "+operand1);
//		System.out.println("Operand 2 is "+operand2);		
////		processor.execute(opcode,operand1,operand2);
//		
//		for(int i=0;i<processor.statusRegister.length;i++)
//			System.out.println(processor.statusRegister[i]);
	}
	
	
}


