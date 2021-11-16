package Project;

import java.util.ArrayList;

public class Case {
	private static int newid = 0;
	
	private final int id;
	private ArrayList<AlgorithmResult> results;
	
	private Case(int id, ArrayList<Algorithm> algorithms, ArrayList<Process> processes) {
		this.id = id;
		results = new ArrayList<AlgorithmResult>();
		for (Algorithm algo : algorithms) {
			ArrayList<ProcessInCPU> rawProcessResults = algo.schedule(processes);
			AlgorithmResult algoResult = AlgorithmResult.create(rawProcessResults, algo.getType());
			
			results.add(algoResult);
		}
//		printTable();
	}
	
	public static Case create(ArrayList<Algorithm> algorithms, ArrayList<Process> processes) {
		Case newCase;
		try {
			newCase = new Case(newid++, algorithms, processes);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			newCase = null;
		}
		return newCase;
	}
	
	public ArrayList<AlgorithmType> bestAlgorithm() {
		ArrayList<AlgorithmType> bestAlgorithms = new ArrayList<AlgorithmType>();
		
		double minAvgTRT = results.get(0).getAvgTurnaroundTime();
		
		for (int i = 1; i < results.size(); i++) {
			AlgorithmResult result = results.get(i);
			if (result.getAvgTurnaroundTime() < minAvgTRT) {
				minAvgTRT = result.getAvgTurnaroundTime();
			}
		}
		
		for (AlgorithmResult result : results) {
			if (result.getAvgTurnaroundTime() == minAvgTRT) {
				bestAlgorithms.add(result.getAlgorithmType());
			}
		}
		return bestAlgorithms;
	}
	
	public static Case findCaseWithId(int id, ArrayList<Case> cases) {
		for (Case c : cases) {
			if (c.id == id) return c;
		}
		return null;
	}
	
	public int getId() { return id; }
	
	public void printTable() {
		System.out.format("%-15s%-15s%-15s%-15s%-15s\n", 
				"Type", 
				"Duration",
				"CPU Util",
				"Avg Turnaround",
				"Avg Queuing");
		for (AlgorithmResult res : results) {
			res.printStats();
		}
		System.out.println();
	}
	
	public void printAlgoShort(AlgorithmType algo) {
		System.out.print(String.format("%-15s", ("Case #" + id)));
		for (AlgorithmResult result : results) {
			if (result.getAlgorithmType() == algo) {
				result.printStats();
			}
		}
	}
	
	public void printAlgoDetail(AlgorithmType algo) {
		for (AlgorithmResult result : results) {
			if (result.getAlgorithmType() == algo) {
				result.printDetails();
			}
		}
	}
}
