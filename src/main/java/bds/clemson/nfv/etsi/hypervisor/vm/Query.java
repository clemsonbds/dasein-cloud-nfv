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
 * Maps to ETSI GS NFV-MAN 001 7.6.2 "Query a virtual machine" 
 * This is a skeletal operation, as the protocol for the return information is currently undefined
 * 
 * @author rakurai
 */

public class Query extends VMOperation {

	private String vmId;

	@Override
	protected void mapProperties(Properties[] prop) throws UsageException {
		vmId = Configuration.map(prop, Configuration.Key.VM_ID, Requirement.REQUIRED);
	}

	public static void main(String[] args) {
		Query operation = new Query();
		operation.execute();
	}
    
	protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
		super.executeInternal();
		
		VirtualMachine vm = vmSupport.getVirtualMachine(vmId);
		
		if (vm == null)
			throw new ResourcesException("No virtual machine with ID '" + vmId + "' exists.");

		// compile and return query results
	}
}
