package bds.clemson.nfv.workflow.etsi;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineSupport;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.workflow.Operation;

public class ListVirtualMachines extends Operation {

	protected void mapArguments(String[] args) {
		providerName = args[0];
	}
	
	protected void usage() {
		System.out.println("usage: "
				+ ListVirtualMachines.class.getName()
				+ " <cloud name>"
		);
	}
	
	public static void main(String[] args) {
		ListVirtualMachines operation = new ListVirtualMachines();
		operation.execute(args);
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
        // see if the cloud provider has any compute services
        ComputeServices compute = provider.getComputeServices();

        if( compute == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support any compute services.");

        // see if it specifically supports virtual machines
        VirtualMachineSupport vmSupport = compute.getVirtualMachineSupport();

        if( vmSupport == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support virtual machines.");

        for( VirtualMachine vm : vmSupport.listVirtualMachines() ) {
            System.out.println(vm.getName() + " [" + vm.getProviderVirtualMachineId() + "] (" + vm.getCurrentState() + ")");
        }
    }
}
