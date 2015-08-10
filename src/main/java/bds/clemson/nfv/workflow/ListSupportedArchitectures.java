package bds.clemson.nfv.workflow;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.VirtualMachineSupport;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;

public class ListSupportedArchitectures extends Operation {

	public static void main(String[] args) {
		ListSupportedArchitectures command = null;
		
        try {
        	// parse args to instance variables
        	if (args.length != 1)
        		throw new UsageException();

        	CloudProvider provider = configureProvider(args[0]);
            command = new ListSupportedArchitectures(provider);

        	// execute command
        	command.execute();

        	// deal with results
        }
        catch (UsageException e) {
    		System.out.println("usage: "
    				+ ListSupportedArchitectures.class.getName()
    				+ " <cloud name>"
    		);
    		System.out.println(e.getMessage());
		}
        catch (ConfigurationException e) {
            System.err.println("An error occurred with the provider configuration: " + e.getMessage());
			e.printStackTrace();
		}
        catch (CapabilitiesException e) {
            System.err.println("An error occurred with the expected capabilities: " + e.getMessage());
            e.printStackTrace();
		}
        catch (ResourcesException e) {
            System.err.println("An error occurred with the provider resources: " + e.getMessage());
			e.printStackTrace();
		}
        catch (ExecutionException e) {
            System.err.println("An error occurred with the execution: " + e.getMessage());
			e.printStackTrace();
		}
        catch( CloudException e ) {
            System.err.println("An error occurred with the cloud provider: " + e.getMessage());
            e.printStackTrace();
        }
        catch( InternalException e ) {
            System.err.println("An error occurred inside Dasein Cloud: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
        	if (command != null)
        		command.provider.close();
        }
	}

    
    public ListSupportedArchitectures(CloudProvider provider) { this.provider = provider; }

    public void execute() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
        // see if the cloud provider has any compute services
        ComputeServices compute = provider.getComputeServices();

        if( compute == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support any compute services.");

        // see if it specifically supports virtual machines
        VirtualMachineSupport vmSupport = compute.getVirtualMachineSupport();

        if( vmSupport == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support virtual machines.");

        for( Architecture architecture : vmSupport.getCapabilities().listSupportedArchitectures() ) {
        	System.out.println(architecture);
        }
    }
}
