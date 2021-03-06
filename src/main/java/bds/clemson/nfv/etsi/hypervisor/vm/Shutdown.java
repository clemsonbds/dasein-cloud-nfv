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
 * Maps to ETSI GS NFV-MAN 001 7.6.2 "Shutdown virtual machine" 
 * uses Dasein's "stop" without forcing
 * 
 * @author rakurai
 */

public class Shutdown extends VMStateChangeOperation {

	public static void main(String[] args) throws UnsupportedOperationException {
		Shutdown operation = new Shutdown();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
    	super.executeInternal();
        
        VmState currentState = vmSupport.getVirtualMachine(vmId).getCurrentState();

        if (vmSupport.getCapabilities().canStop(currentState))
        	throw new CloudException("VM cannot shutdown from state " + currentState);

        vmSupport.stop(vmId, false);
    }
}
