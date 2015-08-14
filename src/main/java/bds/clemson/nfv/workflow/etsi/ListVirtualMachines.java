package bds.clemson.nfv.workflow.etsi;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.VirtualMachine;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.VMOperation;

public class ListVirtualMachines extends VMOperation {

	protected void mapProperties(Properties prop) throws UsageException {
		// none
	}
	
	public static void main(String[] args) {
		ListVirtualMachines operation = new ListVirtualMachines();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
    	super.executeInternal();
    	
        for( VirtualMachine vm : vmSupport.listVirtualMachines() ) {
            System.out.println(vm.getName() + " [" + vm.getProviderVirtualMachineId() + "] (" + vm.getCurrentState() + ")");
        }
    }
}
