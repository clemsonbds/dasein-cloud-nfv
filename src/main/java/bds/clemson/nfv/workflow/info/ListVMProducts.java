package bds.clemson.nfv.workflow.info;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.VirtualMachineProduct;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;
import bds.clemson.nfv.workflow.compute.VMOperation;

public class ListVMProducts extends VMOperation {

	private String architectureName;
	
	protected void mapProperties(Properties prop) throws UsageException {
		architectureName = Configuration.map(prop, "DSN_CMD_ARCHITECTURE", Configuration.Requirement.REQUIRED);
	}
	
	public static void main(String[] args) {
		ListVMProducts operation = new ListVMProducts();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
    	super.executeInternal();

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

        for (VirtualMachineProduct product: vmSupport.listProducts(targetArchitecture)) {
        	System.out.println(product.getProviderProductId() + ", " + product.getName() + ", " + product.getDescription());
        }
    }
}
