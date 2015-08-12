package bds.clemson.nfv.workflow.etsi;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineCapabilities;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.dasein.cloud.compute.VmState;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.workflow.Operation;

/**
 * will terminate the VM instance without saving any data 
 * example provider name "AWS"
 * example input arguments are virtual machine id "i-790cb7bc"
 * 
 * @author uagarwa
 */

public class DestroyVirtualMachine extends Operation {

	private String vmId;
	 String newState= "terminate";
	
	protected void mapArguments(String[] args) {
		vmId = args[0];
	}
	
	protected void usage() {
		System.out.println("usage: "
				+ this.getClass().getName()
				+ " <cloud name>"
				+ " <Virtual Machine Id>"
		);
	}
	
	public static void main(String[] args) {
		DestroyVirtualMachine operation = new DestroyVirtualMachine();
		operation.execute(args);
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ConfigurationException, ResourcesException, ExecutionException {
    	ComputeServices compute = provider.getComputeServices();

            if( compute == null ) 
            System.out.println(provider.getCloudName() + " does not support any compute services.");
        
        
            // see if it specifically supports virtual machines
            VirtualMachineSupport vmSupport = compute.getVirtualMachineSupport();

            if( vmSupport == null ) {
                System.out.println(provider.getCloudName() + " does not support virtual machines.");
            }
            else {	
                // find the vm and change its state
               
                    VirtualMachine vm = vmSupport.getVirtualMachine(vmId);

                    if( vm == null ) {
                        System.err.println("No such VM: " + vmId);
                        return;
                    }
                    VirtualMachineCapabilities capabilities = vmSupport.getCapabilities();
                    VmState currentState = vm.getCurrentState();
                    VmState targetState = null;

                    if( newState.equalsIgnoreCase("terminate") || newState.equalsIgnoreCase("terminated")) {
                        if( capabilities.canTerminate(vm.getCurrentState()) ) {
                            targetState = VmState.TERMINATED;
                            if( currentState.equals(targetState) ) {
                                System.err.println("VM is already " + targetState);
                                return;
                            }
                            System.out.print("Terminating " + vm.getProviderVirtualMachineId() + "...");
                            vmSupport.terminate(vmId);
                        }
    
                    }
            
             }
        }
    }