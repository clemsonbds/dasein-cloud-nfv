package bds.clemson.nfv.etsi.compute.vm;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.VirtualMachine;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.compute.VMOperation;

public class List extends VMOperation {

	protected void mapProperties(Properties[] prop) throws UsageException {
		// none
	}
	
	public static void main(String[] args) {
		List operation = new List();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
    	super.executeInternal();
    	
        for( VirtualMachine vm : vmSupport.listVirtualMachines() ) {
            System.out.println(vm.getName() + " [" + vm.getProviderVirtualMachineId() + "] (" + vm.getCurrentState() + ")");
        }
    }
}