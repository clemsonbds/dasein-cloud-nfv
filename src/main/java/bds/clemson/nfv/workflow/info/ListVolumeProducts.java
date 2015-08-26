package bds.clemson.nfv.workflow.info;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.VolumeProduct;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;

import bds.clemson.nfv.workflow.VolumeOperation;

public class ListVolumeProducts extends VolumeOperation {

		
	protected void mapProperties(Properties prop) throws UsageException {
	//none
	}
	
	public static void main(String[] args) {
		ListVolumeProducts operation = new ListVolumeProducts();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
    	super.executeInternal();

        for (VolumeProduct product: volumeSupport.listVolumeProducts()) {
        	System.out.println(product.getProviderProductId() + ", " + product.getName() + ", " + product.getDescription());
        }
    }
}
