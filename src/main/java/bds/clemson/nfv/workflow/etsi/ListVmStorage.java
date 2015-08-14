package bds.clemson.nfv.workflow.etsi;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.Volume;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.VolumeOperation;

public class ListVmStorage extends VolumeOperation {
	
	protected void mapProperties(Properties prop) throws UsageException {
		// none
	}
	
	public static void main(String[] args) {
		ListVmStorage operation = new ListVmStorage();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
    	super.executeInternal();

    	for (Volume vol : volumeSupport.listVolumes()) {
        	System.out.println(vol.getName() + " [" + vol.getProviderVolumeId() + "] (" + vol.getCurrentState() + ")");
        }
    }
}

