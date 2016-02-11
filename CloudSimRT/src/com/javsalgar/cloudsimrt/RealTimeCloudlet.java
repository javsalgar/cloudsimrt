package com.javsalgar.cloudsimrt;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

public class RealTimeCloudlet extends Cloudlet {

	private double deadline;
	private double creationTime = -1;
	private int deadline_status = DEADLINE_MET;
	
	public int getDeadline_status() {
		return deadline_status;
	}

	public void setDeadline_status(int deadline_status) {
		this.deadline_status = deadline_status;
	}

	public static final int DEADLINE_FAILED = 2;
	public static final int DEADLINE_MET = 1;
	
	public double getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(double creationTime) {
		this.creationTime = creationTime;
	}

	public double getDeadline() {
		return deadline;
	}

	public void setDeadline(double deadline) {
		this.deadline = deadline;
	}

	public RealTimeCloudlet(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize,
			long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam,
			UtilizationModel utilizationModelBw, double deadline) {
		super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
				utilizationModelRam, utilizationModelBw);
		this.deadline = deadline;

	}
	
	

}
