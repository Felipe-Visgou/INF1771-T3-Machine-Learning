import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class Main {
	public Main(){
		return;
	}
	public static void main (String[] args)throws IOException{
		Hashtable<String, Integer> LBoW = new Hashtable<String, Integer>(); //Used for Word Occurrence
		Hashtable<String, Integer> LBoWFull = new Hashtable<String, Integer>();//Used for in document occurrence
		ArrayList<Hashtable<String, Float>> Ltable = new ArrayList<Hashtable<String, Float>>(); //Initially used for TF
		ArrayList<Integer> Loccurrence = new ArrayList<Integer>();
		Scanner s = null;
		int idoc = 0;
		
		
/*		int counter=0;
		try {
        	s  = new Scanner(new BufferedReader(new FileReader("learndb.arff")));
//        	s  = new Scanner(new BufferedReader(new FileReader("oi.txt")));
        	
        	while (s.hasNext()){
        		String S = s.next();
        		System.out.println(S);
        		if (S.startsWith("0.0"))
        			break;
        		if (S.equals("@attribute"))
        			counter++;
        	}
        } finally {
            s.close();
        }
		System.out.println(counter);
		
*/        try {
        	s  = new Scanner(new BufferedReader(new FileReader("teste.txt")));
//        	s  = new Scanner(new BufferedReader(new FileReader("real.txt")));
        	s.next(); //Optional handle of a bug
        	String SS = new String(s.next()); //Document Separator
        	int iwordcount = 0;
        	while(s.hasNext())
        	{
        		String S = new String(s.next());
        		boolean bFlag = false;
        		int iBit = 1;
        		System.out.println(S);
        		if (S.equals(SS)){
        			Ltable.add(new Hashtable<String, Float>());
        			Enumeration<String> E = LBoW.keys();
        			System.out.println("Estou no TF: " + idoc);
        			for (int i = 0; i < LBoW.size(); i++){
        				String SSS = E.nextElement();
        				Ltable.get(idoc).put(SSS, (float)LBoW.remove(SSS)/iwordcount); //'put' returns the previous value        				
        			}
        			System.out.println("Saí do TF");
        			iwordcount = 0;
        			idoc++;
        			continue;
        		}
        		if (S.length() < 3) //Discarding small words
        			continue;

        		for(int i = 0; i < S.length(); i++){ //Discarding words with non-alphabetical characters
        			if (((int)S.charAt(i) & 0x80) != 0) //Above ASCII
        				bFlag = true;
        			else if (((int)S.charAt(i) & 0xFFC0) == 0) //Below '@' (easier for bitwise)
        				bFlag = true;
        			else if ((int)S.charAt(i) == 64) // '@'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 91) // '['
        				bFlag = true;
        			else if ((int)S.charAt(i) == 92) // '\'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 93) // ']'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 94) // '^'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 95) // '_'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 96) // '`'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 123) // '{'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 124) // '|'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 125) // '}'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 126) // '~'
        				bFlag = true;
        			else if ((int)S.charAt(i) == 127) // 'DEL'
        				bFlag = true;        			
        		}
        		if (bFlag == true)
        			continue;
        		
        		iwordcount++;
        		
        		if (!LBoWFull.containsKey(S)){
        			LBoWFull.put(S, 0);
 //       			Loccurrence.add(new Integer(iBit));
 //       			iBit *= 2;
        		}
        		if (!LBoW.containsKey(S))
        			LBoW.put(S, 1);        		
        		else     			
            		LBoW.put(S, (LBoW.get(S))+1);        		
        	}
        } finally {
            s.close();
        }
        
        System.out.println("antes de contar documento");
        Enumeration<String> E = LBoWFull.keys();
        for (int i = 0; i < LBoWFull.size(); i++){
        	String S = E.nextElement();
        	int icount = 0;
        	for (int j = 0; j < Ltable.size(); j++){
        		if (Ltable.get(j).containsKey(S))
        			icount++;
        	}
        	LBoWFull.put(S, icount);
        	System.out.println((i*100)/LBoWFull.size() + "%\n");
        }
        System.out.println("entrando no idf");
        for (int i = 0; i < Ltable.size(); i++){
        	E = LBoWFull.keys();
        	System.out.println((i*100)/Ltable.size() + "%\n");
        	for (int j = 0; j < LBoWFull.size(); j++){
        		String S = E.nextElement();
        		float f;
        		if (LBoWFull.get(S) == 0)
        			f = 0;
        		else{
        			if (!Ltable.get(i).containsKey(S))
        				f = 0;
        			else
        				f = (float) ((Ltable.get(i).get(S))*(Math.log((idoc+1)/LBoWFull.get(S))));
        		}
        		//Now Ltable will store TF*IDF
        		Ltable.get(i).put(S, f);
        	}
        }
        System.out.println("escrevendo arff");
        BufferedWriter writer = null;
		File Foutput = new File("teste.arff");
        try {
            writer = new BufferedWriter(new FileWriter(Foutput));
            writer.write("@relation teste\r\n\r\n");
            E = LBoWFull.keys();
            for(int i=0;i<LBoWFull.size();i++){
            	String S = E.nextElement();
            	if (S.contains("\'")){
            		S = S.replace("\'", "+");
            	}
            	if (S.contains("\"")){
            		S = S.replace("\"", "=");
            	}
            	if (S.contains(",")){
            		S = S.replace(",", "|");
            	}
            			
            	writer.write("@attribute " + S + " NUMERIC\r\n");
            }
            writer.write("@attribute @@class@@ {Bads,Goods}");
            writer.write("\r\n");
            writer.write("@data\r\n\r\n");
            
            for(int i=0;i<Ltable.size();i++){
            	E = LBoWFull.keys();
            	for(int j=0; j<LBoWFull.size();j++){            		
	            	String S = E.nextElement();
	            	if (Ltable.get(i).containsKey(S))
	            		writer.write(Ltable.get(i).get(S).toString() + ',');
	            	else
	            		writer.write("0.0,");
            	}
            	if (i<idoc/2){
            		writer.write("Bads");
            	}
            	else if (i>=idoc/2){
            		writer.write("Goods");
            	}
            	writer.write("\r\n");
            	
            }
        } finally {
        	writer.close();
        }
        System.out.println("FIM");
	}
}
