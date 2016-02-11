package com.javsalgar.cloudsimrt;
import java.util.List;

import org.cloudbus.cloudsim.Vm;

public class DefaultCloudletVMScheduler implements CloudletVMScheduler {

	@Override
	public int getNextVM(List<Vm> listVM, int cloudletid) {
		return -1;
	}

}
