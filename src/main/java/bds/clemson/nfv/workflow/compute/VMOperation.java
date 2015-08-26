package bds.clemson.nfv.workflow.compute;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.VirtualMachineSupport;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.workflow.compute.ComputeOperation;

public abstract class VMOperation extends ComputeOperation {
	protected VirtualMachineSupport vmSupport;
	
	protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
		super.executeInternal();

		// see if it specifically supports virtual machines
        vmSupport = computeServices.getVirtualMachineSupport();

        if( vmSupport == null )
            throw new UnsupportedOperationException(provider.getCloudName() + " does not support virtual machines.");
	}

	public Architecture getSupportedArchitecture(String architectureName) throws InternalException, CloudException {
        for( Architecture architecture : vmSupport.getCapabilities().listSupportedArchitectures() )
        	if (architectureName.equals(architecture.toString()))
        		return architecture;
        
        return null;
	}

}
