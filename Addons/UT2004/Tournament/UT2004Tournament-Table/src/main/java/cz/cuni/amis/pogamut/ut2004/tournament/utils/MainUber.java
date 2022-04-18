package cz.cuni.amis.pogamut.ut2004.tournament.utils;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

public class MainUber {
	
	private static boolean headerOutput = false;;
	
	private static void header() {		
		if (headerOutput) return;
		System.out.println();
		System.out.println("=============================");
		System.out.println("Pogamut UT2004 Table Executor");
		System.out.println("============================");
		System.out.println();
		headerOutput = true;
	}
	
	private static void fail(String errorMessage, Throwable e) {
		header();
		System.out.println("ERROR: " + errorMessage);
		System.out.println();
		if (e != null) {
			e.printStackTrace();
			System.out.println("");
		}		
        System.out.println("Usage: java -jar ut2004-tournament-dm-table.jar");
        System.out.println("                 -type [type] ... type == DM, DM-EXCEL, TDM, TDM-EXCEL");
        System.out.println("The rest of arguments according to different types.");
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}
	
	public static void main(String[] args) throws JSAPException {
		
		header();
		
		if (args.length < 1) fail("Bad argumetns...", null);
		
		List<String> subArgs = new ArrayList<String>(args.length);
		
		String type = "";
		
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-type")) {
				if (i+1 < args.length) {
					type = args[i+1];
					i += 1;
				} else {
					fail("Bad arguments, missing -type ...", null);
				}
			} else {
				subArgs.add(args[i]);
			}
		}
		
		switch (type) {
		case "DM":
			cz.cuni.amis.pogamut.ut2004.tournament.dm.table.Main.main(subArgs.toArray(new String[0]));
			return;
		case "DM-EXCEL":
			cz.cuni.amis.pogamut.ut2004.tournament.dm.table.MainExcel.main(subArgs.toArray(new String[0]));
			return;
		case "TDM":
			cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.Main.main(subArgs.toArray(new String[0]));
			return;
		case "TDM-EXCEL":
			cz.cuni.amis.pogamut.ut2004.tournament.tdm.table.MainExcel.main(subArgs.toArray(new String[0]));
			return;
		}
		
	}

}
