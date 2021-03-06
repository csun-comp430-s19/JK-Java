package JK_Lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws TokenizerException, ParserException, TypeErrorException, IOException, CCodeGeneratorException {
    	File inFile = null;
    	boolean printgccoutput = false;
    	String filename = "output.c";
    	String exename = filename.substring(0, filename.lastIndexOf("."));
    	
    	if (0 < args.length) {
    	   inFile = new File(args[0]);
    	} else {
    	   System.err.println("Invalid arguments count:" + args.length);
    	   System.exit(0);
    	}

       BufferedReader br = null;
       String input = "";
       
       try {

           String sCurrentLine;

           br = new BufferedReader(new FileReader(inFile));

           while ((sCurrentLine = br.readLine()) != null) {
               input+=sCurrentLine; 
           }

       } 

       catch (IOException e) {
           e.printStackTrace();
       } 

       finally {
           try {
               if (br != null)br.close();
           } catch (IOException ex) {
               ex.printStackTrace();
           }
       }
       
       //System.out. println(input);
       
       final Tokenizer tokenizer = new Tokenizer(input.toCharArray());
   	   final List<Token> tokenList = tokenizer.tokenize(); 
   	   Token[] tokenArray = new Token[tokenList.size()];
   	   tokenArray = tokenList.toArray(tokenArray); 
   	   final Parser parser = new Parser(tokenArray); 
   	   final Program prog = parser.parseProgram(); 
   	   Typechecker.typecheckProgram(prog);
   	   String output = convertToCProgram(prog); 
   	   
   	   //System.out.println(output); 
   	   
   	   PrintWriter out = new PrintWriter(filename); 
   	   try {
   		   out.println(output); 
   	   }finally {
   		   out.close(); 
   	   }
   	   Process p = Runtime.getRuntime().exec("gcc " + filename + " -o " + exename + " -w");
   	   
   	   BufferedReader stdInput = new BufferedReader(new 
            InputStreamReader(p.getInputStream()));

       BufferedReader stdError = new BufferedReader(new 
            InputStreamReader(p.getErrorStream()));
       
       if(printgccoutput) {
    	   // read the output from the command
    	   String s = null;
    	   while ((s = stdInput.readLine()) != null) {
    		   System.out.println(s);
    	   }
       
    	   // read any errors from the attempted command
    	   while ((s = stdError.readLine()) != null) {
    		   System.out.println(s);
    	   }
       }
       
   }
    public static String convertToCProgram(Program p) throws IOException, CCodeGeneratorException {
    	CCodeGenerator cg = new CCodeGenerator(p); 
    	final File file = File.createTempFile("testfile", ".c"); 
    	cg.generate(file);
    	final String received = convertFileToString(file); 
    	return received; 
    }
    public static String convertFileToString(File file) throws IOException{
		StringBuilder sb = new StringBuilder((int)file.length());
		Scanner scanner = new Scanner(file);
		while(scanner.hasNextLine()) {
			sb.append(scanner.nextLine()+"\n");
		}
		scanner.close();
		return sb.toString(); 
    }
}
