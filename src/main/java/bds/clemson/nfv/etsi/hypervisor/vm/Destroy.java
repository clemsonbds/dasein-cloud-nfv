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
 * Maps to ETSI GS NFV-MAN 001 7.6.2 "Destroy virtual machine" 
 * uses Dasein's "stop" with forced power off
 * 
 * @author rakurai
 */

public class Destroy extends VMStateChangeOperation {

	public static void main(String[] args) throws UnsupportedOperationException {
		Destroy operation = new Destroy();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
    	super.executeInternal();
        
        VmState currentState = vmSupport.getVirtualMachine(vmId).getCurrentState();

        if (vmSupport.getCapabilities().canStop(currentState))
        	throw new CloudException("VM cannot be destroyed from state " + currentState);

        vmSupport.stop(vmId, true);
    }
}
