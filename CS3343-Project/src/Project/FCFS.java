package Project;

import java.util.*;

public class FCFS implements Algorithm {
    private ArrayList<Process> ready_queue = new ArrayList<>();
    private ArrayList<Process> block_queue_K = new ArrayList<>();
    private ArrayList<Process> processes_done = new ArrayList<>();
    private int complete_num = 0;
    private int dispatched_tick = 0;
    private int cur_process_id = -1, prev_process_id = -1;
    private int MAX_LOOP=1000;

    private static FCFS instance = new FCFS();
    
    private FCFS() {}
    
    public static FCFS getInstance() {
    	return instance;
    }

    public Result schedule(ArrayList<Process> processes){
    	
    	
    	//storage of loggers for each process
    	HashMap<Integer, ProcessInCPU> logger_map = new HashMap<Integer, ProcessInCPU>();
    	
    	//Create new logger for each process
    	for(int i=0; i<processes.size(); i++) {
    		ProcessInCPU logger = new ProcessInCPU(processes.get(i));
    		logger_map.put(processes.get(i).getId(), logger);
    	}
    	
        // main loop
        for(int cur_tick = 0; cur_tick < MAX_LOOP; cur_tick++){
            // long term scheduler
            for (int i = 0; i < processes.size(); i++)
            {
                if (processes.get(i).getArrivalTime() == cur_tick)
                { // process arrives at current tick
                    ready_queue.add(processes.get(i));
                }
            }

             // keyboard I/O device scheduling
            if (!block_queue_K.isEmpty())
            {
                Process cur_io_process = block_queue_K.get(0); // always provide service to the first process in block queue
                // AYAN TODO
                // for all other processes in queue, except for current, increment k_queuing_time: For loop from index 1 to end
                for (int i=1; i<block_queue_K.size(); i++) {
                	block_queue_K.get(i).updateQueueingTime();
                }
                
                if (cur_io_process.cur_service_tick >= cur_io_process.getCurServiceTime())
                { // I/O service is completed
                    cur_io_process.proceedToNextService();
                    SystemHelper.moveProcessFrom(block_queue_K, ready_queue);
                    if (!block_queue_K.isEmpty()) cur_io_process = block_queue_K.get(0); 
                    else cur_io_process = null;
                }
                if (cur_io_process != null) cur_io_process.cur_service_tick++; // increment the num of ticks that have been spent on current service
                
                
            }

            // CPU scheduling
            if (ready_queue.isEmpty())
            {                         // no process for scheduling
                prev_process_id = -1; // reset the previous dispatched process ID to empty
            }
            else
            {
                Process cur_process = ready_queue.get(0); // always dispatch the first process in ready queue
                cur_process_id = cur_process.getId();
                if (cur_process_id != prev_process_id)
                { // store the tick when current process is dispatched
                    dispatched_tick = cur_tick;
                }
                
                cur_process.cur_service_tick++; // increment the num of ticks that have been spent on current service
                // AYAN TODO
                // for all other processes in queue, except for current, increment p_queuing_time: For loop from index 1 to end
                for (int i=1; i<ready_queue.size(); i++) {
                	ready_queue.get(i).updateQueueingTime();
                }
                
                if (cur_process.cur_service_tick >= cur_process.getCurServiceTime())
                { // current service is completed
                    ManageNextServiceFCFS.manageNextServiceFcfs(cur_process, complete_num, dispatched_tick, cur_tick, ready_queue,
                                            processes_done, block_queue_K, logger_map.get(cur_process.getId())); // look for next service
                }
                
                prev_process_id = cur_process_id; // log the previous dispatched process ID
            }
            if (complete_num == processes.size())
            { // all process completed
                break;
            }
        }
        
        
        //Generate result
    	Result res = new Result(processes);
        res.setSequence(logger_map);
        
        res.printSequences();
        res.printQueueingTimes();
        
        return res;

    }
}
