package com.javsalgar.cloudsimrt;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

public class DatacenterBrokerFreq extends DatacenterBroker {

	protected double freq;
	protected int nextId;
	protected int totalTasks;
	protected Cloudlet task;
	protected CloudletVMScheduler vmSched;
	
	
	public double getFreq() {
		return freq;
	}

	public void setFreq(double freq) {
		this.freq = freq;
	}

	public DatacenterBrokerFreq(String name, double freq, int totalTasks, Cloudlet task, CloudletVMScheduler vmSched) throws Exception {
		super(name);
		this.freq = freq;
		this.totalTasks = totalTasks;
		this.task = task;
		this.nextId = 0;
		this.vmSched = vmSched;
	}
	
	protected Cloudlet createNewCloudlet() {
		UtilizationModelFull u = new UtilizationModelFull();
		Cloudlet res = new  Cloudlet(nextId, 1, 1, task.getCloudletFileSize(), task.getCloudletOutputSize(), u, u, u);
		return res;
	}
	
	protected void configureCloudlet(Cloudlet cloudlet) {
		
		cloudlet.setCloudletLength(task.getCloudletLength());
		cloudlet.setNumberOfPes(task.getNumberOfPes());
		cloudlet.setUtilizationModelCpu(task.getUtilizationModelCpu());
		cloudlet.setUtilizationModelBw(task.getUtilizationModelBw());
		cloudlet.setUtilizationModelRam(task.getUtilizationModelRam());
		
		cloudlet.setUserId(this.getId());
		cloudlet.setVmId(vmSched.getNextVM(this.getVmList(), nextId));
		
	} 
	
	protected void sendNewCloudlet() { 
		
		submitCloudlets();
		List <Cloudlet> l = new ArrayList<Cloudlet>();
		Cloudlet c = createNewCloudlet();
		configureCloudlet(c);
		l.add(c);
		nextId++;
		this.submitCloudletList(l);
		
		if (totalTasks > nextId) { 
			schedule(getId(), 1/freq, CloudSimTags.NextCycle);			
		}
	}
	
	
	protected void processOtherEvent(SimEvent ev) {
		if (ev == null) {
			Log.printLine(getName() +  ".processOtherEvent(): Error - an event is null.");
			return;
		}
		
		if (ev.getTag() == CloudSimTags.NextCycle) { 
			sendNewCloudlet();
		}
		
		
	}
	
	
	protected void processVmCreate(SimEvent ev) { 
		
		super.processVmCreate(ev);
		
		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
			Log.printLine(getName() +  ".processVmCreate(): All VMs granted, starting tasks");
			schedule(getId(), freq, CloudSimTags.NextCycle);
		
		}
	}
	
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
		Log.printLine(CloudSim.clock() +  ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId() +
				" received");
		
		cloudletsSubmitted--;
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} 
	}
	

}
