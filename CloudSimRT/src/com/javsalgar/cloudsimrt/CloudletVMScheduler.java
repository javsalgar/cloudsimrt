package com.javsalgar.cloudsimrt;
import java.util.List;

import org.cloudbus.cloudsim.Vm;

public interface CloudletVMScheduler {

	public int getNextVM(List<Vm> listVM, int cloudletid);
	
}
