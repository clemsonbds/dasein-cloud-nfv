package bds.clemson.nfv.workflow.etsi;

import java.io.IOException;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineSupport;

import bds.clemson.nfv.ProviderLoader;

public class ListVirtualMachines {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloudException, InternalException, IOException {
		String providerPropertiesFilename = args[0] + ".properties";
		ProviderLoader loader = new ProviderLoader(providerPropertiesFilename);

		ListVirtualMachines command = new ListVirtualMachines(loader.getConfiguredProvider());

        try {
            command.execute(args);
        }
        finally {
            command.provider.close();
        }
	}

    private CloudProvider provider;

    public ListVirtualMachines(CloudProvider provider) { this.provider = provider; }

    public void execute(String[] args) {
        	
    	// see if the cloud provider has any compute services
        ComputeServices compute = provider.getComputeServices();

        if( compute == null ) {
            System.out.println(provider.getCloudName() + " does not support any compute services.");
        }
        else {
            // see if it specifically supports virtual machines
            VirtualMachineSupport vmSupport = compute.getVirtualMachineSupport();

            if( vmSupport == null ) {
                System.out.println(provider.getCloudName() + " does not support virtual machines.");
            }
            else {
                //enumerate the VMs
                try {
                    int count = 0;

                    System.out.println("Virtual machines in " + provider.getCloudName() + ":");
                    for( VirtualMachine vm : vmSupport.listVirtualMachines() ) {
                        count++;
                        System.out.println("\t" + vm.getName() + "[" + vm.getProviderVirtualMachineId() + "] (" + vm.getCurrentState() + ")");
                    }
                    System.out.println("Total: " + count);
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
