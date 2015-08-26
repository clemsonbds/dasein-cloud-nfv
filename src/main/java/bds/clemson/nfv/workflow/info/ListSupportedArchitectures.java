package bds.clemson.nfv.workflow.info;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.compute.Architecture;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.VMOperation;

public class ListSupportedArchitectures extends VMOperation {

	protected void mapProperties(Properties prop) throws UsageException {
		// none
	}
	
	public static void main(String[] args) {
		ListSupportedArchitectures operation = new ListSupportedArchitectures();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
    	super.executeInternal();
    	
        for( Architecture architecture : vmSupport.getCapabilities().listSupportedArchitectures() ) {
        	System.out.println(architecture);
        }
    }
}
