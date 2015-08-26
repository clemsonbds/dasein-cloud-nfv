package bds.clemson.nfv.workflow.info;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;

import org.dasein.cloud.network.NetworkInterface;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.NetworkOperation;


public class ListVirtualNetworkDevice extends NetworkOperation {

		
	protected void mapProperties(Properties prop) throws UsageException {
	//none
	}
	
	public static void main(String[] args) {
		ListVirtualNetworkDevice operation = new ListVirtualNetworkDevice();
		operation.execute();
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
    	super.executeInternal();

        for (NetworkInterface NIC: vlanSupport.listNetworkInterfaces()) {
        	System.out.println(NIC.getProviderVlanId() + ", " + NIC.getName() + ", " + NIC.getDescription());
        }
    }
}