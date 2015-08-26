package bds.clemson.nfv.workflow;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.VolumeSupport;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;

public abstract class VolumeOperation extends ComputeOperation {
	protected VolumeSupport volumeSupport;
	
	protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
		super.executeInternal();

		// see if it specifically supports virtual machines
    	volumeSupport = computeServices.getVolumeSupport();

        if( volumeSupport == null )
            throw new UnsupportedOperationException(provider.getCloudName() + " does not support volumes.");
	}
}
