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
import bds.clemson.nfv.workflow.ListVMProducts;
import bds.clemson.nfv.workflow.Operation;

/**
 * will stop the VM instance 
 * example provider name "AWS"
 * example input arguments are virtual machine id "i-790cb7bc"
 * 
 * @author uagarwa
 */

public class StopVirtualMachine extends Operation {

	private String vmId;
	 String newState= "stop";
	
	protected void mapArguments(String[] args) {
		vmId = args[0];
	}
	
	protected void usage() {
		System.out.println("usage: "
				+ ListVMProducts.class.getName()
				+ " <cloud name>"
				+ " <Virtual Machine Id>"
		);
	}
	
	public static void main(String[] args) {
		StopVirtualMachine operation = new StopVirtualMachine();
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
                    
                    if( newState.equalsIgnoreCase("stop") || newState.equalsIgnoreCase("stopped")) {
                        if( capabilities.canStop(vm.getCurrentState()) ) {
                            targetState = VmState.STOPPED;
                            if( currentState.equals(targetState) ) {
                                System.err.println("VM is already " + targetState);
                                return;
                            }
                            System.out.print("Stopping " + vm.getProviderVirtualMachineId() + "...");
                            vmSupport.stop(vmId);
                        }
                        else {
                            System.err.println("Cloud does not support stopping of virtual machines in the " + currentState + " state");
                        }
                    }
             }
        }
    }
