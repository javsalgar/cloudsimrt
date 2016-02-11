package com.javsalgar.cloudsimrt.examples;
/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import com.javsalgar.cloudsimrt.CloudletVMScheduler;
import com.javsalgar.cloudsimrt.DatacenterBrokerFreqRT;
import com.javsalgar.cloudsimrt.RealTimeCloudlet;
import com.javsalgar.cloudsimrt.RoundRobinCloudletVMScheduler;

/**
 * A simple example showing how to create a data center with one host and run one cloudlet on it.
 */
public class CloudSimRTExample1 {


	/**
	 * Creates main() to run this example.
	 *
	 * @param args the args
	 */
	public static void main(String[] args) {
		Log.printLine("Starting RealTimeCloudSimExample1...");

		try {
			// First step: Initialize the CloudSim package. It should be called before creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
 			boolean trace_flag = false; // trace events

			CloudSim.init(num_user, calendar, trace_flag);

			/*
			 * Broker properties
			 */
			
			double freq = 10;
			int totalTasks = 200;
			CloudletVMScheduler vmSched = new RoundRobinCloudletVMScheduler();
			
 			/*
 			 * Repeated Task Properties 
 			 */
 			
 			int cloudletLength = 100000;
 			int pesNumber = 1;
 			int cloudletFileSize = 500;
 			int cloudletOutputSize = 500;
 			UtilizationModelFull utilization = new UtilizationModelFull();
 			int deadline = 10;
 			
 			RealTimeCloudlet rtc = new RealTimeCloudlet(0, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, 
 					utilization, utilization, utilization, deadline);
 			

			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			// Third step: Create Broker
			DatacenterBroker broker = createBroker(1, freq, totalTasks, rtc, vmSched);
			int brokerId = broker.getId();

			// Fourth step: Create one virtual machine
			List<Vm> vmlist = new ArrayList<Vm>();
			
			// VM description
			int mips = 50000;
			long size = 10000; // image size (MB)
			int ram = 512; // vm memory (MB)
			long bw = 1000;
			int vmpesNumber = 12; // number of cpus
			String vmm = "Xen"; // VMM name

			for (int i = 0; i < 4; i++) {

				int thisbrokerid = brokerId ;

				Vm vm = new Vm(i, thisbrokerid, mips, vmpesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());

				vmlist.add(vm);

			}

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			NetworkTopology.addLink(datacenter0.getId(),broker.getId(),1.0,5);

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			//Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			
			printCloudletList(newList);

			Log.printLine("CloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	/**
	 * Creates the datacenter.
	 *
	 * @param name the name
	 *
	 * @return the datacenter
	 */
	private static Datacenter createDatacenter(String name) {

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		// our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 10000000;

		// 3. Create PEs and add these into a list.

		for (int i = 0; i < 6; i++) {
			peList.add(new Pe(i, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating			
		}

		// 4. Create Host with its id and list of PEs and add them to the list
		// of machines
		int ram = 8192; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 1000;

		for (int i = 0; i < 1200; i++) {
			
			hostList.add(
				new Host(
					i,
					new RamProvisionerSimple(ram),
					new BwProvisionerSimple(bw),
					storage,
					peList,
					new VmSchedulerTimeShared(peList)
				)
			); // This is our machine
			
		}

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.1; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	// We strongly encourage users to develop their own broker policies, to
	// submit vms and cloudlets according
	// to the specific rules of the simulated scenario
	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	private static DatacenterBrokerFreqRT createBroker(int n, double freq, int totalTasks, RealTimeCloudlet rtc, CloudletVMScheduler vmSched) {
		DatacenterBrokerFreqRT broker = null;
					
		try {
			broker = new DatacenterBrokerFreqRT("Sender_" + n,freq, totalTasks, rtc, vmSched);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		RealTimeCloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========================================================================================================");
		Log.printLine("Cloudlet ID" + indent + "DEADLINE" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Total Time" + indent
				+ "Start Time" + indent + "Finish Time" + indent + "Deadline");
		Log.printLine("========================================================================================================");
		
		DecimalFormat dft = new DecimalFormat("###.##");
		DecimalFormat dft2 = new DecimalFormat("000");
		for (int i = 0; i < size; i++) {
			cloudlet = (RealTimeCloudlet) list.get(i);
			Log.print(indent + dft2.format(cloudlet.getCloudletId()) + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {

				if (cloudlet.getDeadline_status() == RealTimeCloudlet.DEADLINE_MET) { 
					Log.print("SUCCESS");
				} else {
					Log.print("FAILED");
				}
				
				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime() - cloudlet.getCreationTime()) + indent
						+ indent + indent + dft.format(cloudlet.getCreationTime())
						+ indent + indent + indent
						+ dft.format(cloudlet.getFinishTime()) + indent + indent 
						+ dft.format(cloudlet.getDeadline()));
			}
		}
	}
}