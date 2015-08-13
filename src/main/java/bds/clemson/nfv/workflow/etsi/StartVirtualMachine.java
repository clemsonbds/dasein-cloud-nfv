package bds.clemson.nfv.workflow.etsi;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.VmState;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.workflow.VMStateChangeOperation;

/**
 * will start the VM instance 
 * example provider name "AWS"
 * example input arguments are virtual machine id "i-790cb7bc"
 * 
 * @author uagarwa
 */

public class StartVirtualMachine extends VMStateChangeOperation {

	public static void main(String[] args) throws CapabilitiesException {
		StartVirtualMachine operation = new StartVirtualMachine();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ExecutionException, ResourcesException, ConfigurationException {
    	super.executeInternal();
    	changeState(VmState.RUNNING);
    }
}
