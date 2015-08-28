package bds.clemson.nfv.etsi.network;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.network.NICCreateOptions;
import org.dasein.cloud.network.NICState;
import org.dasein.cloud.network.NetworkInterface;

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;
import bds.clemson.nfv.workflow.network.NetworkOperation;


public class CreateNIC extends NetworkOperation {
	
	private String subnetID;
	private String name;
	private String description;
	
	
	protected void mapProperties(Properties[] prop) throws UsageException {
		subnetID = Configuration.map(prop, Configuration.Key.SUBNET_ID, Configuration.Requirement.REQUIRED);
		name = Configuration.map(prop, Configuration.Key.NAME, Configuration.Requirement.REQUIRED);
		description = Configuration.map(prop, Configuration.Key.DESCRIPTION, Configuration.Requirement.REQUIRED);
				
	}
	
	public static void main(String[] args) {
		CreateNIC operation = new CreateNIC();
		operation.execute();
	}

    
	protected void executeInternal() throws InternalException, CloudException, ConfigurationException, ResourcesException, OperationNotSupportedException {
    	super.executeInternal();
         
       	NICCreateOptions options = NICCreateOptions.getInstanceForSubnet(subnetID, name, description);
        NetworkInterface NIC = vlanSupport.createNetworkInterface(options); 
       
        System.out.println("Created: " + NIC.getName() + "[" + NIC.getProviderVlanId() + "] (" + NIC.getCurrentState() + ")");
        while( NIC != null && NIC.getCurrentState().equals(NICState.PENDING) ) {
            System.out.print(".");
            try { Thread.sleep(5000L); }
            catch( InterruptedException ignore ) { }
           
        }
  
            System.out.println("Network Interface created (" + NIC.getCurrentState() + ")");
     }
}
