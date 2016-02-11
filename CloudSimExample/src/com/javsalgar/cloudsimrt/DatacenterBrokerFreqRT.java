package com.javsalgar.cloudsimrt;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

public class DatacenterBrokerFreqRT extends DatacenterBrokerFreq {

	public DatacenterBrokerFreqRT(String name, double freq, int totalTasks, RealTimeCloudlet task, CloudletVMScheduler vmSched) throws Exception {
		super(name, freq, totalTasks, task, vmSched);
	}


	protected Cloudlet createNewCloudlet() {
		UtilizationModelFull u = new UtilizationModelFull();
		return new RealTimeCloudlet(nextId, 1, 1, task.getCloudletFileSize(), task.getCloudletOutputSize(), u, u, u, 0);
	}
	

	protected void configureCloudlet(Cloudlet cloudlet) {
		RealTimeCloudlet rtc = (RealTimeCloudlet) cloudlet;
		rtc.setDeadline(((RealTimeCloudlet)task).getDeadline());
		super.configureCloudlet(rtc);
	} 
	
	protected void sendNewCloudlet() { 
		
		for (Cloudlet cl: cloudletList) { 
			RealTimeCloudlet rcl = (RealTimeCloudlet) cl;
			if (rcl.getCreationTime() == -1) { 
				rcl.setCreationTime(CloudSim.clock());
			}
		}
		
		super.sendNewCloudlet();
	}
	

	protected void processCloudletReturn(SimEvent ev) {
		RealTimeCloudlet cloudlet = (RealTimeCloudlet) ev.getData();

		if (cloudlet.getFinishTime() - cloudlet.getCreationTime() > cloudlet.getDeadline()) { 
			try {
				cloudlet.setDeadline_status(RealTimeCloudlet.DEADLINE_FAILED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		super.processCloudletReturn(ev);
		 
	}
}
