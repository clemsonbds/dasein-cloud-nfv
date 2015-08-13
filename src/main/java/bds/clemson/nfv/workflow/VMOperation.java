package bds.clemson.nfv.workflow;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.VirtualMachineSupport;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;

public abstract class VMOperation extends ComputeOperation {
	protected VirtualMachineSupport vmSupport;
	
	protected void executeInternal() throws CapabilitiesException, InternalException, CloudException, ExecutionException, ResourcesException, ConfigurationException {
		super.executeInternal();

		// see if it specifically supports virtual machines
        vmSupport = computeServices.getVirtualMachineSupport();

        if( vmSupport == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support virtual machines.");
	}
}
