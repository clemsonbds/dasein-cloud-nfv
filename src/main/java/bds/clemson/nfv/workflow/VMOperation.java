package bds.clemson.nfv.workflow;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VirtualMachineSupport;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;

public abstract class VMOperation extends ComputeOperation {
	protected VirtualMachineSupport vmSupport;
	
	protected void executeInternal() throws UnsupportedOperationException, InternalException, CloudException, ExecutionException, ResourcesException, ConfigurationException {
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
