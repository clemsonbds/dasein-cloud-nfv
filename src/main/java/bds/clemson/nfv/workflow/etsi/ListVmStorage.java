package bds.clemson.nfv.workflow.etsi;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.Volume;
import org.dasein.cloud.compute.VolumeSupport;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Operation;

public class ListVmStorage extends Operation {
	
	protected void mapProperties(Properties prop) throws UsageException {
		// none
	}

	protected void mapArguments(String[] args) {
		// none
	}
	
	protected void usage() {
		System.out.println("usage: "
				+ this.getClass().getName()
				+ " <cloud name>"
		);
	}
	
	public static void main(String[] args) {
		ListVmStorage operation = new ListVmStorage();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
        // see if the cloud provider has any compute services
        ComputeServices compute = provider.getComputeServices();

        if( compute == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support any compute services.");

        // see if it specifically supports volume support
        VolumeSupport volSupport = compute.getVolumeSupport();

        if( volSupport == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support Volume.");

         for(Volume vol:volSupport.listVolumes()){
        System.out.println(vol.getName() + " [" + vol.getProviderVolumeId() + "] (" + vol.getCurrentState() + ")");
        }
       
    }
  }

