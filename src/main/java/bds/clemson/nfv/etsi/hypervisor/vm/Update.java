package bds.clemson.nfv.etsi.hypervisor.vm;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.compute.VirtualMachine;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;
import bds.clemson.nfv.workflow.compute.VMOperation;

/**
 * Maps to ETSI GS NFV-MAN 001 7.6.2 "Update a virtual machine" 
 * 
 * 
 * @author rakurai
 */

public class Update extends VMOperation {

	private String vmId;
	private String operation;
	private String device;

	@Override
	protected void mapProperties(Properties[] prop) throws UsageException {
		// TODO Auto-generated method stub
		vmId = Configuration.map(prop, Configuration.Key.VM_ID, Requirement.REQUIRED);
		operation = Configuration.map(prop, Configuration.Key.OPERATION, Requirement.REQUIRED);
		device = Configuration.map(prop, Configuration.Key.DEVICE_ID, Requirement.REQUIRED);
	}

	public static void main(String[] args) {
		Update operation = new Update();
		operation.execute();
	}

    
	protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
		super.executeInternal();
		
		VirtualMachine vm = vmSupport.getVirtualMachine(vmId);
		
		if (vm == null)
			throw new ResourcesException("No virtual machine with ID '" + vmId + "' exists.");

	}
}
