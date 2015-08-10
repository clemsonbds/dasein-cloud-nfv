package bds.clemson.nfv.workflow;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VirtualMachineSupport;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;

public class ListVMProducts extends Operation {

	private String architectureName;
	
	protected void mapArguments(String[] args) {
		providerName = args[0];
		architectureName = args[1];
	}
	
	protected void usage() {
		System.out.println("usage: "
				+ ListVMProducts.class.getName()
				+ " <cloud name>"
				+ " <architecture>"
		);
	}
	
	public static void main(String[] args) {
		ListVMProducts operation = new ListVMProducts();
		operation.execute(args);
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
        // see if the cloud provider has any compute services
        ComputeServices compute = provider.getComputeServices();

        if( compute == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support any compute services.");

        // see if it specifically supports virtual machines
        VirtualMachineSupport vmSupport = compute.getVirtualMachineSupport();

        if( vmSupport == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support virtual machines.");

        // find a target architecture and VM product
        Architecture targetArchitecture = null;

        for( Architecture architecture : vmSupport.getCapabilities().listSupportedArchitectures() ) {
        	if (architectureName.equals(architecture.toString())) {
                targetArchitecture = architecture;
                break;
        	}
        }

        if (targetArchitecture == null)
        	throw new CapabilitiesException(provider.getCloudName() + " does not support the " + architectureName + " architecture.");

        for (VirtualMachineProduct product: vmSupport.listProducts(targetArchitecture)) {
        	System.out.println(product.getProviderProductId() + ", " + product.getName() + ", " + product.getDescription());
        }
    }
}
