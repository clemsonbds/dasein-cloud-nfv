package bds.clemson.nfv.workflow;

import java.io.IOException;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;

import bds.clemson.nfv.ProviderLoader;
import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;

public abstract class Operation {
    protected CloudProvider provider;

    public abstract void execute() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException;

    public static CloudProvider configureProvider(String providerName) throws CloudException, InternalException, ConfigurationException {
		String providerPropertiesFilename = providerName + ".properties";
		ProviderLoader loader;

		try {
			loader = new ProviderLoader(providerPropertiesFilename);
		}
		catch (CloudException e) {
			throw e;
		}
		catch (InternalException e) {
			throw e;
		}
		catch (IOException e) {
			throw new ConfigurationException(e.getMessage());
		}
		catch (ClassNotFoundException e) {
			throw new ConfigurationException(providerName + " is not a valid provider, is the JAR in the classpath?");
		}
		catch (IllegalAccessException e) {
			throw new InternalException(e);
		}
		catch (InstantiationException e) {
			throw new InternalException(e);
		}

        return loader.getConfiguredProvider();
    }
}
