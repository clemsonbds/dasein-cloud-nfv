package bds.clemson.nfv.workflow;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;

import bds.clemson.nfv.ProviderLoader;
import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;

public abstract class Operation {
	protected CloudProvider provider;

    protected abstract void mapArguments(String[] args);
    protected abstract void usage();
    protected abstract void executeInternal() throws UsageException, InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException;

    protected void execute(String[] args) {
		try {
	    	// parse args to instance variables
			try {
				mapArguments(args);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new UsageException("Not enough arguments.");
			}

			Properties providerProperties = new Properties();

			String providerPropertiesPath = System.getProperty("DSN_PROPERTIES", null);
			if (providerPropertiesPath == null)
				throw new UsageException("DSN_PROPERTIES not set.");

			try {
				FileReader reader = new FileReader(providerPropertiesPath);
				providerProperties.load(reader);
			} catch (FileNotFoundException e) {
				throw new ConfigurationException("Cannot find properties file.");
			} catch (IOException e) {
				throw new ConfigurationException("Unable to read properties file.");
			}
				
	    	try {
	    		ProviderLoader loader = new ProviderLoader(providerProperties);
				provider = loader.getConfiguredProvider();
			}
	    	catch (ClassNotFoundException e) {
				throw new UsageException("Provider " + providerProperties.getProperty("DSN_PROVIDER_CLASS", "<null>") + " not found, is the JAR in the classpath?");
	    	}
			catch (IllegalAccessException e) {
				throw new InternalException(e);
			}
			catch (InstantiationException e) {
				throw new InternalException(e);
			}
	    	catch (UnsupportedEncodingException e) {
				throw new ConfigurationException(e.getMessage());
			}
	    	
	    	// execute command
	    	executeInternal();
	    }
	    catch (UsageException e) {
	    	usage();
			if (e.getMessage() != null)
				System.out.println(e.getMessage());
			e.printStackTrace();
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
	    	if (provider != null)
	    		provider.close();
	    }
	}
}
