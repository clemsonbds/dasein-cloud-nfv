package bds.clemson.nfv.etsi.storage;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.Volume;
import org.dasein.cloud.compute.VolumeCreateOptions;
import org.dasein.cloud.compute.VolumeState;
import org.dasein.util.uom.storage.Gigabyte;
import org.dasein.util.uom.storage.Storage;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;
import bds.clemson.nfv.workflow.storage.VolumeOperation;

public class CreateVolume extends VolumeOperation {

	private String productId;
	private String VolumeName;
	private String VolumeDes;
	private Integer capacity;
    private Integer iops;
	
	protected void mapProperties(Properties prop) throws UsageException {
		productId = Configuration.map(prop, "DSN_CMD_PRODUCT", Configuration.Requirement.REQUIRED);
		VolumeName = Configuration.map(prop, "DSN_CMD_VOLUMENAME", Configuration.Requirement.REQUIRED);
		VolumeDes = Configuration.map(prop, "DSN_CMD_VOLUMEDES", Configuration.Requirement.REQUIRED);
		capacity = Integer.parseInt(Configuration.map(prop, "DSN_CMD_CAPACITY", Configuration.Requirement.REQUIRED));
		iops = Integer.parseInt(Configuration.map(prop, "DSN_CMD_IOPS", Configuration.Requirement.REQUIRED));
		
	}
	
	public static void main(String[] args) {
		CreateVolume operation = new CreateVolume();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
    	super.executeInternal();
         
      
		Storage<Gigabyte> size = new Storage<Gigabyte>(capacity, Storage.GIGABYTE); 
        VolumeCreateOptions options = VolumeCreateOptions.getInstance(productId, size, VolumeName, VolumeDes, iops);
        String volumeId = volumeSupport.createVolume(options); 
        Volume vol = volumeSupport.getVolume(volumeId);

        System.out.println("Created: " + vol.getName() + "[" + vol.getProviderVolumeId() + "] (" + vol.getCurrentState() + ")");
        while( vol != null && vol.getCurrentState().equals(VolumeState.PENDING) ) {
            System.out.print(".");
            try { Thread.sleep(5000L); }
            catch( InterruptedException ignore ) { }
            vol = volumeSupport.getVolume(vol.getProviderVolumeId());
            //getVolume(@Nonnull String volumeId) throws InternalException, CloudException;
        }
        if( vol == null ) {
            System.out.println("Volume not created");
        }
        else {
            System.out.println("Volume created (" + vol.getCurrentState() + ")");
        }
    }
}
