package MLFQ;

public class Process {
	
	private int pid;               //进程名
	private int arriveTime;       //进程到达时间
	private int serviceTime;      //进程所需服务时间
	
	private int startTime;        //进程开始时间
	private int runTime;          //进程运行时间
	private int endTime;          //结束时间
	private int turnaround;       //周转时间
	
	private boolean execute = false;//是否头次执行
	
	public Process() {	
	}

	public Process(int pid, int arriveTime, int serviceTime) {
		super();
		this.pid = pid;
		this.arriveTime = arriveTime;
		this.serviceTime = serviceTime;
	}

	public int getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(int arriveTime) {
		this.arriveTime = arriveTime;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getRunTime() {
		return runTime;
	}

	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getTurnaround() {
		return turnaround;
	}

	public void setTurnaround(int turnaround) {
		this.turnaround = turnaround;
	}

	public int getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(int serviceTime) {
		this.serviceTime = serviceTime;
	}

	public boolean isExecute() {
		return execute;
	}

	public void setExecute(boolean execute) {
		this.execute = execute;
	}
	
	
	

}
