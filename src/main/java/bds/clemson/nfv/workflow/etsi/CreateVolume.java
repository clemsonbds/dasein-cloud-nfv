package bds.clemson.nfv.workflow.etsi;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.compute.Volume;
import org.dasein.cloud.compute.VolumeCreateOptions;
import org.dasein.cloud.compute.VolumeProduct;
import org.dasein.util.uom.storage.Gigabyte;
import org.dasein.util.uom.storage.Storage;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;
import bds.clemson.nfv.workflow.VolumeOperation;

public class CreateVolume extends VolumeOperation {

	private String productName;
	
	protected void mapProperties(Properties prop) throws UsageException {
		productName = Configuration.map(prop, "DSN_CMD_PRODUCT", Configuration.Requirement.REQUIRED);
	}
	
	public static void main(String[] args) {
		CreateVolume operation = new CreateVolume();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
    	super.executeInternal();
    	
        // launch a vm
    	VolumeProduct product = null;
    	/**
                     * Provides options for creating a block volume of a specific size based on a specific product. This method makes no sense
                     * when {@link VolumeSupport#getVolumeProductRequirement()}  is {@link Requirement#NONE}.
                     * @param volumeProductId the ID of the volume product from {@link VolumeSupport#listVolumeProducts()} to use in creating the volume
                     * @param size the size of the volume to be created (or to be resized to)
                     * @param name the name of the new volume
                     * @param description a friendly description of the purpose of the new volume
                     * @param iops the minimum guaranteed iops or 0 if no guarantees are sought
                     * @return an object representing the options for creating the new volume
 **/
        
        // get product from productName first? this will be null
        String volumeProductId = product.getProviderProductId();
		Storage<Gigabyte> size = new Storage<Gigabyte>(2, Storage.GIGABYTE); // how to specify size, what would be the format
        String volname="test";
        String voldescription="might work";
        int voliops=300;

        VolumeCreateOptions options = VolumeCreateOptions.getInstance(volumeProductId, size, volname, voldescription, voliops);

        
                          
        String volumeId = volumeSupport.createVolume(options); // NOt sure whether to change to string or change to volume
        Volume vol = volumeSupport.getVolume(volumeId);

        System.out.println("Created: " + vol.getName() + "[" + vol.getProviderVolumeId() + "] (" + vol.getCurrentState() + ")");
        while( vol != null && vol.getCurrentState().equals(VmState.PENDING) ) {
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
