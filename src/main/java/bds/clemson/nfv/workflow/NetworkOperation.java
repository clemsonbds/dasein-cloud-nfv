package bds.clemson.nfv.workflow;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.network.VLANSupport;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;

public abstract class NetworkOperation extends ComputeOperation {
	protected VLANSupport vlanSupport;
	
	protected void executeInternal() throws CapabilitiesException, InternalException, CloudException, ExecutionException, ResourcesException, ConfigurationException {
		super.executeInternal();
	    NetworkServices network = provider.getNetworkServices();
		
		vlanSupport = network.getVlanSupport();
		// see if it specifically supports vlan

        if( vlanSupport == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support Vlan capabilities.");
	}
}
