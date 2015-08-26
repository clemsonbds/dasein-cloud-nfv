package bds.clemson.nfv.workflow.etsi.vm;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.VmState;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.workflow.VMStateChangeOperation;

/**
 * will start the VM instance 
 * example provider name "AWS"
 * example input arguments are virtual machine id "i-790cb7bc"
 * 
 * @author uagarwa
 */

public class Start extends VMStateChangeOperation {

	public static void main(String[] args) throws UnsupportedOperationException {
		Start operation = new Start();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
    	super.executeInternal();
    	changeState(VmState.RUNNING);
    }
}
