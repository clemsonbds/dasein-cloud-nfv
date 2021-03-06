package bds.clemson.nfv.workflow.network;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.network.VLANSupport;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.workflow.compute.ComputeOperation;

public abstract class NetworkOperation extends ComputeOperation {
	protected VLANSupport vlanSupport;
	
	protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
		super.executeInternal();
	    NetworkServices network = provider.getNetworkServices();
		
		vlanSupport = network.getVlanSupport();
		// see if it specifically supports vlan

        if( vlanSupport == null )
            throw new UnsupportedOperationException(provider.getCloudName() + " does not support Vlan capabilities.");
	}
}
