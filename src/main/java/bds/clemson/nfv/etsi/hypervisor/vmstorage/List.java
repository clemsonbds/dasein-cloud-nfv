package bds.clemson.nfv.etsi.hypervisor.vmstorage;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.Volume;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.storage.VolumeOperation;

public class List extends VolumeOperation {
	
	protected void mapProperties(Properties[] prop) throws UsageException {
		// none
	}
	
	public static void main(String[] args) {
		List operation = new List();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
    	super.executeInternal();

    	for (Volume vol : volumeSupport.listVolumes()) {
        	System.out.println(vol.getName() + " [" + vol.getProviderVolumeId() + "] (" + vol.getCurrentState() + ")");
        }
    }
}

