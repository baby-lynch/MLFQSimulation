package MLFQ;

import java.util.LinkedList;

public class MultiLevelQueue {	
	private int priority;   
	private int time_slice; 
    private LinkedList<Process> queue = new LinkedList<Process>();
    
	public MultiLevelQueue(int priority, int time_slice) {
		this.priority = priority;
		this.time_slice=time_slice;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getTime_slice() {
		return time_slice;
	}

	public void setTime_slice(int time_slice) {
		this.time_slice = time_slice;
	}

	public void enque(Process pcb) {
		queue.add(pcb);
	}
	
	public void deque(Process pcb) {
		queue.remove(pcb);
	}
    

}
