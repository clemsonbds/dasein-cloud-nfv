package bds.clemson.nfv.workflow.compute;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.ComputeServices;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.workflow.Operation;

public abstract class ComputeOperation extends Operation {
	protected ComputeServices computeServices;

	protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
		// see if the cloud provider has any compute services
        computeServices = provider.getComputeServices();

        if( computeServices == null )
            throw new UnsupportedOperationException(provider.getCloudName() + " does not support any compute services.");
	}
}
