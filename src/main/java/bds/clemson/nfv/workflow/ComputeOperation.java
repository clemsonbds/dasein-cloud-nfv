package bds.clemson.nfv.workflow;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.ComputeServices;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;

public abstract class ComputeOperation extends Operation {
	protected ComputeServices computeServices;

	protected void executeInternal() throws CapabilitiesException, InternalException, CloudException, ExecutionException, ResourcesException, ConfigurationException {
		// see if the cloud provider has any compute services
        computeServices = provider.getComputeServices();

        if( computeServices == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support any compute services.");
	}
}
