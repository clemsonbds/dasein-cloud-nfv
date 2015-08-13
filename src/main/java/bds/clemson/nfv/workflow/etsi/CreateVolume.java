package bds.clemson.nfv.workflow.etsi;

import java.util.Iterator;
import java.util.Properties;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.MachineImageSupport;
import org.dasein.cloud.compute.Platform;
import org.dasein.cloud.compute.VMLaunchOptions;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.compute.Volume;
import org.dasein.cloud.compute.VolumeCreateOptions;
import org.dasein.cloud.compute.VolumeProduct;
import org.dasein.cloud.compute.VolumeSupport;
import org.dasein.cloud.identity.IdentityServices;
import org.dasein.cloud.identity.SSHKeypair;
import org.dasein.cloud.identity.ShellKeySupport;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.network.Subnet;
import org.dasein.cloud.network.SubnetState;
import org.dasein.cloud.network.VLAN;
import org.dasein.cloud.network.VLANState;
import org.dasein.cloud.network.VLANSupport;
import org.dasein.util.uom.storage.Gigabyte;
import org.dasein.util.uom.storage.Storage;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Operation;

public class CreateVolume extends Operation {

	protected void mapProperties(Properties prop) throws UsageException {
		// none
	}
	
	public static void main(String[] args) {
		CreateVolume operation = new CreateVolume();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
    	// see if the cloud provider has any compute services
        ComputeServices compute = provider.getComputeServices();

        if( compute == null ) {
            System.out.println(provider.getCloudName() + " does not support any compute services.");
        }
        else {
            // see if it specifically supports virtual machines
        	VolumeSupport volSupport = compute.getVolumeSupport();

            if( volSupport == null ) {
                System.out.println(provider.getCloudName() + " does not support volume.");
            }
            else {
                // launch a vm
                try {
                	VolumeProduct product;
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
 					Storage<Gigabyte>="2"; // how to specify size, what would be the format
                    String volname="test";
                    String voldescription="might work";
                    int voliops=300;
                    
                    VolumeCreateOptions options = VolumeCreateOptions.getInstance(product.getProviderProductId(), machineImageId, volname, voldescription, voliops);

                    
                                      
                    Volume vol = volSupport.createVolume(options); // NOt sure whether to change to string or change to volume

                    System.out.println("Created: " + vol.getName() + "[" + vol.getProviderVolumeId() + "] (" + vol.getCurrentState() + ")");
                    while( vol != null && vol.getCurrentState().equals(VmState.PENDING) ) {
                        System.out.print(".");
                        try { Thread.sleep(5000L); }
                        catch( InterruptedException ignore ) { }
                        vol = volSupport.getVolume(vol.getProviderVolumeId());
                        //getVolume(@Nonnull String volumeId) throws InternalException, CloudException;
                    }
                    if( vol == null ) {
                        System.out.println("Volume not created");
                    }
                    else {
                        System.out.println("Volume created (" + vol.getCurrentState() + ")");
                    }
                }
                catch( CloudException e ) {
                    System.err.println("An error occurred with the cloud provider: " + e.getMessage());
                    e.printStackTrace();
                }
                catch( InternalException e ) {
                    System.err.println("An error occurred inside Dasein Cloud: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
