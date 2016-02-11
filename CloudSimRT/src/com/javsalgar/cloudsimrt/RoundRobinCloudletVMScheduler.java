package com.javsalgar.cloudsimrt;
import java.util.List;

import org.cloudbus.cloudsim.Vm;

public class RoundRobinCloudletVMScheduler implements CloudletVMScheduler {

	
	public int getNextVM(List<Vm> listVM, int cloudletid) {
		int res = (cloudletid % listVM.size());
		return res;
	}

}
