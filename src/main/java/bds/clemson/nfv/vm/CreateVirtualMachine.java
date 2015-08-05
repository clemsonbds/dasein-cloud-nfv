package bds.clemson.nfv.vm;

import java.io.IOException;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;

import bds.clemson.nfv.ProviderLoader;

public class CreateVirtualMachine {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, CloudException, InternalException, IOException {
		String providerPropertiesFilename = args[0] + ".properties";
		ProviderLoader loader = new ProviderLoader(providerPropertiesFilename);

        CreateVirtualMachine command = new CreateVirtualMachine(loader.getConfiguredProvider());

        try {
            command.execute(args);
        }
        finally {
            command.provider.close();
        }
	}

    private CloudProvider provider;

    public CreateVirtualMachine(CloudProvider provider) { this.provider = provider; }

    public void execute(String[] args) {
    	System.out.println("Executing command.");
    	// execute command here
    }
}
