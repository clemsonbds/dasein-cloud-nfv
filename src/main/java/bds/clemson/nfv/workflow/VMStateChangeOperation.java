package bds.clemson.nfv.workflow;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VmState;

import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.UsageException;

public abstract class VMStateChangeOperation extends VMOperation {
	private String vmId;

	protected void mapProperties(Properties prop) throws UsageException {
		vmId = Configuration.map(prop, "DSN_CMD_VMID", Configuration.Requirement.REQUIRED);
	}

	public void changeState(VmState targetState) throws InternalException, CloudException, ExecutionException {

        // find the vm and change its state
        VirtualMachine vm = vmSupport.getVirtualMachine(vmId);
 
        VmState currentState = vm.getCurrentState();

        if (currentState.equals(targetState))
        	throw new ExecutionException("VM is already in state " + targetState);

        boolean canChangeState = false;
        
        switch (targetState) {
        case REBOOTING:		canChangeState = vmSupport.getCapabilities().canReboot(currentState);		break;
        case TERMINATED:	canChangeState = vmSupport.getCapabilities().canTerminate(currentState);	break;
        case RUNNING:		canChangeState = vmSupport.getCapabilities().canStart(currentState);		break;
        case STOPPED:		canChangeState = vmSupport.getCapabilities().canStop(currentState);			break;
		default:
			throw new UnsupportedOperationException();
        }

        if (!canChangeState)
        	throw new ExecutionException("VM cannot change to state " + targetState + " from state " + currentState);

        switch (targetState) {
        case REBOOTING:		vmSupport.reboot(vmId);		break;
        case TERMINATED:	vmSupport.terminate(vmId);	break;
        case RUNNING:		vmSupport.start(vmId);		break;
        case STOPPED:		vmSupport.stop(vmId);		break;
		default:
			throw new UnsupportedOperationException();
        }
	}
}
