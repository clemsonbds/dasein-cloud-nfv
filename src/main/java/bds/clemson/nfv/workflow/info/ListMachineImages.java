package bds.clemson.nfv.workflow.info;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.ImageClass;
import org.dasein.cloud.compute.ImageFilterOptions;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.MachineImageState;
import org.dasein.cloud.compute.MachineImageSupport;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;
import bds.clemson.nfv.workflow.compute.VMOperation;

public class ListMachineImages extends VMOperation {

	private String architectureName;
	
	protected void mapProperties(Properties[] prop) throws UsageException {
		architectureName = Configuration.map(prop, Configuration.Key.ARCHITECTURE_NAME, Requirement.REQUIRED);
	}
	
	public static void main(String[] args) {
		ListMachineImages operation = new ListMachineImages();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
    	super.executeInternal();

        MachineImageSupport imgSupport = computeServices.getImageSupport();

        if( imgSupport == null )
            throw new UnsupportedOperationException(provider.getCloudName() + " does not support machine images.");

        // find a target architecture and VM product
        Architecture targetArchitecture = null;

        for( Architecture architecture : vmSupport.getCapabilities().listSupportedArchitectures() ) {
        	if (architectureName.equals(architecture.toString())) {
                targetArchitecture = architecture;
                break;
        	}
        }

        if (targetArchitecture == null)
        	throw new UnsupportedOperationException(provider.getCloudName() + " does not support the " + architectureName + " architecture.");

        for( MachineImage image : imgSupport.listImages(ImageFilterOptions.getInstance(ImageClass.MACHINE)) ) {
            if( image.getCurrentState().equals(MachineImageState.ACTIVE) && image.getArchitecture().equals(targetArchitecture)) {
            	System.out.println(image.getProviderMachineImageId() + " , " + image.getName() + " , " + image.getDescription());
            }
        }

    }
}
