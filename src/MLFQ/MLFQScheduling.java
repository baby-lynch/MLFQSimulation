package MLFQ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import MLFQ.Process;

import java.util.LinkedList;

public class MLFQScheduling extends Thread{
	
	static int timeSlice=2;                          //最小时间片为2
	static int queuesize=10;                          
	static int processnum=0;                         //进程编号
	static int currentTime = 0;                      
	static Process[] newprocess;
	static Process[] pool=new Process[queuesize];    //存储待排序处理的所有输入进程信息
	
	static DefaultTableModel res = new DefaultTableModel();
	static DefaultTableModel firstqueue = new DefaultTableModel();
	static JTable jt_first=new JTable(firstqueue);
	static DefaultTableModel secondqueue = new DefaultTableModel();
	static JTable jt_second=new JTable(secondqueue);
	static DefaultTableModel thirdqueue = new DefaultTableModel();
	static JTable jt_third=new JTable(thirdqueue);
	
	static JLabel clock = new JLabel();
	static JPanel picture=new JPanel(new BorderLayout());
	
	static LinkedList<Process> firstQueue = new LinkedList<>();   
	static LinkedList<Process> secondQueue = new LinkedList<>();  
	static LinkedList<Process> thirdQueue = new LinkedList<>();
			
    static JFrame f = new JFrame("多级反馈队列调度算法模拟   by E21714039 - 高雷");
	 
	public static void createProcess(int pid,int arrivetime,int servicetime) {
		if(processnum<queuesize) {
			Process p=new Process(pid,arrivetime,servicetime);
			pool[processnum]=p;
			processnum++;
		}
	}
	
	public static void startScheduling() throws InterruptedException{
		newprocess=Schedule(sortbyarrival(pool));	
			
	}

	public static void doneScheduling() throws InterruptedException {
		
		System.out.println("-----------Execution Results-----------");
		for(Process p:newprocess) {			
			System.out.println("Process"+p.getPid()+":");
			
			System.out.println("Arrivetime: "+p.getArriveTime());
			System.out.println("Servicetime: "+p.getServiceTime());
			
			System.out.println("Starttime: "+p.getStartTime());
			System.out.println("Endtime: "+p.getEndTime());
			
			System.out.println("Responsetime: "+(p.getStartTime()-p.getArriveTime()));
			System.out.println("Turnaround: "+(p.getEndTime()-p.getArriveTime()));
			
			System.out.println();
			
			res.addRow(new Object[] {p.getPid(),p.getStartTime()-p.getArriveTime(),p.getEndTime(),p.getEndTime()-p.getArriveTime()});
		}
	}
	
	public static Process[] sortbyarrival(Process[] pool) {
		//将所有的输入进程按到达时间进行排序
		Process[] waitingList=new Process[processnum];
		
		for(int i=0;i<waitingList.length;i++) {
			waitingList[i]=pool[i];
		}
		
		for(int i=0;i<waitingList.length;i++) {
			for(int j=0;j<waitingList.length-1;j++) {
				if(waitingList[j].getArriveTime()>waitingList[j+1].getArriveTime()) {
					Process temp=waitingList[j];
					waitingList[j]=waitingList[j+1];
					waitingList[j+1]=temp;
				}
			}			
		}
		return waitingList;
	}
	
	public static Process[] Schedule(Process[] process) throws InterruptedException{
		
		int index = 0;              //进程数组的下标
		int lastTime = -1;          //上一时刻的时间
		
		int firstTimeSlice = 2;    
		int secondTimeSlice = 4; 
		int thirdTimeSlice = 8; 
		int firstSlice = 0;    
		int secondSlice = 0;    
		int thirdSlice = 0;  //时间片计时器
		
		int currQueueId = -1;   
		int new_index = 0;   //返回新进程数组的下标
		
		Process execProcess = null;   //当前执行进程
		Process[] newProcess = new Process[process.length];  //返回的新进程数组	
		
		
		while(index < process.length || firstQueue.size() != 0 || 
				secondQueue.size() != 0 || thirdQueue.size() != 0) {
			//当各队列不为空或进程数组还有进程，循环继续
			if((firstQueue.size() != 0 || secondQueue.size() != 0 || thirdQueue.size() != 0)) { 
				//判断时间差，有时间差即进程执行，否则无执行
				lastTime = currentTime - 1;
			}else {
				lastTime = currentTime;
			}
			while(index < process.length && process[index].getArriveTime() == currentTime) {  //每一时刻判断是否有进程入队
				firstQueue.offerLast(process[index]);   
				index++;
			}

			if(currQueueId == 1) { 
				//当前执行的进程属于第一队列，使用第一队列的时间片
				execProcess.setRunTime(execProcess.getRunTime()+currentTime-lastTime);//更新进程已执行时间
				firstSlice += currentTime-lastTime;  //更新时间片的计时器
				if(execProcess.getServiceTime() == execProcess.getRunTime()) {
		        //判断已执行时间是否等于服务时间,即进程完成
				//更新进程的属性，初始化时间片等属性，将进程存入新进程数组
					firstQueue.pollFirst();
					execProcess.setEndTime(currentTime);
					execProcess.setTurnaround(execProcess.getEndTime()-execProcess.getArriveTime());
					newProcess[new_index++] = execProcess;
					firstSlice = 0;
					currQueueId = -1;
				}else if(firstSlice == firstTimeSlice){//时间片用完，将该进程拿到下一等级队列的队尾，初始化各属性
					firstSlice = 0;
					currQueueId = -1;
					firstQueue.pollFirst();
					secondQueue.offerLast(execProcess);
				}
			}else if(currQueueId == 2) {//当前执行的进程属于第二队列，使用第二队列的时间片
				execProcess.setRunTime(execProcess.getRunTime()+currentTime-lastTime);
				secondSlice += currentTime-lastTime;
				if(execProcess.getServiceTime() == execProcess.getRunTime()) {
					secondQueue.pollFirst();
					execProcess.setEndTime(currentTime);
					execProcess.setTurnaround(execProcess.getEndTime()-execProcess.getArriveTime());
					newProcess[new_index++] = execProcess;
					secondSlice = 0;
					currQueueId = -1;
				}else if(secondSlice == secondTimeSlice){
					secondSlice = 0;
					currQueueId = -1;
					secondQueue.pollFirst();
					thirdQueue.offerLast(execProcess);
				}
			}else if(currQueueId == 3) {//当前执行的进程属于第三队列，使用第三队列的时间片
				execProcess.setRunTime(execProcess.getRunTime()+currentTime-lastTime);
				thirdSlice += currentTime-lastTime;
				if(execProcess.getServiceTime() == execProcess.getRunTime()) {
					thirdQueue.pollFirst();
					execProcess.setEndTime(currentTime);
					execProcess.setTurnaround(execProcess.getEndTime()-execProcess.getArriveTime());
					newProcess[new_index++] = execProcess;
					thirdSlice = 0;
					currQueueId = -1;
				}else if(thirdSlice == thirdTimeSlice){
					thirdSlice = 0;
					currQueueId = -1;
					thirdQueue.pollFirst();
					thirdQueue.offerLast(execProcess);
				}
			}
			
		
			if(firstQueue.size()!=0) { 
				execProcess = firstQueue.peekFirst();
				if(!execProcess.isExecute()) {//判断进程是否头次执行
					execProcess.setStartTime(currentTime);
					execProcess.setExecute(true);
				}
				currQueueId = 1;
			}else if(secondQueue.size() != 0) {
				execProcess = secondQueue.peekFirst();
				currQueueId = 2;
			}else if(thirdQueue.size() != 0) {
				execProcess = thirdQueue.peekFirst();
				currQueueId = 3;
			}
			
			System.out.println("-----------Time:"+ currentTime+"-----------");		
			clock.setText("当前时间："+currentTime);
			
			
			System.out.print("Q1:[");
			for(int i=0;i<firstQueue.size();i++) {
				System.out.print("Process"+firstQueue.get(i).getPid()+",");
				firstqueue.addRow(new Object[] {"Process "+firstQueue.get(i).getPid()});
				jt_first.setFont(new Font("Arial", Font.BOLD, 14));
				jt_first.setBackground(Color.PINK);					
			}
			System.out.print("]");
			System.out.println();
			
			System.out.print("Q2:[");
			for(int i=0;i<secondQueue.size();i++) {
				System.out.print("Process"+secondQueue.get(i).getPid()+",");
				secondqueue.addRow(new Object[] {"Process "+secondQueue.get(i).getPid()});
				jt_second.setFont(new Font("Arial", Font.BOLD, 14));
				jt_second.setBackground(Color.ORANGE);
					
			}
			System.out.print("]");
			System.out.println();
			
			System.out.print("Q3:[");
			for(int i=0;i<thirdQueue.size();i++) {
				System.out.print("Process"+thirdQueue.get(i).getPid()+",");
				thirdqueue.addRow(new Object[] {"Process "+thirdQueue.get(i).getPid()});
				jt_third.setFont(new Font("Arial", Font.BOLD, 14));
				jt_third.setBackground(Color.MAGENTA);
					
			}
			System.out.print("]");
			System.out.println();
			
			Thread.sleep(1000);
			firstqueue.setRowCount(0);
			secondqueue.setRowCount(0);
			thirdqueue.setRowCount(0);
			currentTime++;		
		}
		return newProcess;
	}
	
    public static void initFrame() {   
        //进程信息表
		DefaultTableModel info = new DefaultTableModel();
		JTable jt1=new JTable(info);
		jt1.setEnabled(false);
		info.addColumn("进程编号");
		info.addColumn("到达时间");
		info.addColumn("服务时间");
		JScrollPane sp1=new JScrollPane(jt1);   
		
		//运行结果表
		JTable jt2=new JTable(res);
		jt2.setEnabled(false);
		res.addColumn("进程编号");
		res.addColumn("响应时间");
		res.addColumn("结束时间"); 
		res.addColumn("周转时间");
		JScrollPane sp2=new JScrollPane(jt2);
		
		//按钮及功能
		JButton create_btn= new JButton("新建");
		create_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int row= jt1.getRowCount()+1;
				JTextField xField = new JTextField(5);
				JTextField yField = new JTextField(5);
				
				JPanel enter_info = new JPanel();
				enter_info.setLayout(new BoxLayout(enter_info, BoxLayout.Y_AXIS));
				enter_info.add(new JLabel("到达时间:"));
				enter_info.add(xField);
				enter_info.add(Box.createHorizontalStrut(15)); 
				enter_info.add(new JLabel("服务时间:"));
				enter_info.add(yField);
				
				int result = JOptionPane.showConfirmDialog(null,enter_info,"进程信息", JOptionPane.OK_CANCEL_OPTION);

				if (result == JOptionPane.OK_OPTION) {
					if(xField.getText().equals(null)||xField.getText().equals("")||yField.getText().equals(null)||yField.getText().equals("")){	
					}else{
						info.addRow(new Object[] {Integer.toString(row), xField.getText(), yField.getText()});
						int arrivetime=Integer.valueOf(xField.getText());				
						int servicetime=Integer.valueOf(yField.getText());
						createProcess(row,arrivetime,servicetime);
					}				
				}
			}
		});
		
		JButton start_btn= new JButton("开始");
		start_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
								
				new Thread(new Runnable() {
					@Override
					public void run() {																				
						try {
							startScheduling();
							doneScheduling();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					} 	
				}).start();
			}
		});
		
		
		JButton clear_btn= new JButton("清空");
		clear_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				info.setRowCount(0);
				res.setRowCount(0);
				processnum=0;						
				pool=null;
				pool=new Process[queuesize];
				newprocess=null;
				currentTime=0;	
				clock.setText("当前时间："+currentTime);
				firstqueue.setRowCount(0);
				secondqueue.setRowCount(0);
				thirdqueue.setRowCount(0);
			}
		});
		
		//队列状态图
		JPanel queues = new JPanel(new BorderLayout());
		DefaultTableCellRenderer r=new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        //一级队列
		jt_first.setRowHeight(50);
		jt_first.setPreferredScrollableViewportSize(new Dimension(250,300));
		jt_first.setEnabled(false);
        jt_first.setDefaultRenderer(Object.class,r);
		firstqueue.addColumn("一级队列");
		JTableHeader head_first=jt_first.getTableHeader();
		head_first.setPreferredSize(new Dimension(head_first.getWidth(), 35));
		head_first.setFont(new Font(null, Font.PLAIN, 15));
		JScrollPane sp_first=new JScrollPane(jt_first);
		//二级队列	
		jt_second.setRowHeight(50);
		jt_second.setPreferredScrollableViewportSize(new Dimension(250,300));
		jt_second.setEnabled(false);
        jt_second.setDefaultRenderer(Object.class,r);
		secondqueue.addColumn("二级队列");
		JTableHeader head_second=jt_second.getTableHeader();
		head_second.setPreferredSize(new Dimension(head_second.getWidth(), 35));
		head_second.setFont(new Font(null, Font.PLAIN, 15));
		JScrollPane sp_second=new JScrollPane(jt_second);
		//三级队列		
		jt_third.setRowHeight(50);
		jt_third.setPreferredScrollableViewportSize(new Dimension(250,300));
		jt_third.setEnabled(false);
		jt_third.setDefaultRenderer(Object.class,r);
		thirdqueue.addColumn("三级队列");
		JTableHeader head_third=jt_third.getTableHeader();
		head_third.setPreferredSize(new Dimension(head_third.getWidth(), 35));
		head_third.setFont(new Font(null, Font.PLAIN, 15));
		JScrollPane sp_third=new JScrollPane(jt_third);
		
		queues.add(sp_first,BorderLayout.WEST);
		queues.add(sp_second,BorderLayout.CENTER);
		queues.add(sp_third,BorderLayout.EAST);
		
        
		//信息表和按钮
		JPanel buttons = new JPanel(new GridLayout(1,4,5,5));
		buttons.add(create_btn);
		buttons.add(start_btn);
		buttons.add(clear_btn);		
		JPanel left_up= new JPanel(new BorderLayout());
		left_up.add(sp1, BorderLayout.CENTER);
		left_up.add(buttons, BorderLayout.SOUTH);
		
		//左边表格部分
		JPanel table = new JPanel(new GridLayout(2,1,5,5));
		table.add(left_up);
		table.add(sp2);
		
		//右边状态图部分
		clock.setFont(new Font("New Times Roman", Font.BOLD, 20));
		clock.setPreferredSize(new Dimension(750,100));
		
		picture.add(clock,BorderLayout.NORTH);
		picture.add(queues,BorderLayout.CENTER);
		
		
		f.add(table, BorderLayout.WEST); 
		f.add(picture, BorderLayout.EAST);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(new Dimension(1228, 625));
		f.setLocationRelativeTo(null);
        f.setVisible(true);
	}
	 
	public static void main(String[] args) {	
			
		initFrame();	
					
	}
}
