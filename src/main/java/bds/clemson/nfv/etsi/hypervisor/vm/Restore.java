package bds.clemson.nfv.etsi.hypervisor.vm;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VmState;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.workflow.compute.VMStateChangeOperation;

/**
 * Maps to ETSI GS NFV-MAN 001 7.6.2 "Restore a virtual machine" 
 * uses Dasein's "resume"
 * 
 * @author rakurai
 */

public class Restore extends VMStateChangeOperation {

	public static void main(String[] args) throws UnsupportedOperationException {
		Restore operation = new Restore();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
    	super.executeInternal();
        
        VmState currentState = vmSupport.getVirtualMachine(vmId).getCurrentState();

        if (vmSupport.getCapabilities().canResume(currentState))
        	throw new CloudException("VM cannot restore from state " + currentState);

        vmSupport.resume(vmId);
    }
}
