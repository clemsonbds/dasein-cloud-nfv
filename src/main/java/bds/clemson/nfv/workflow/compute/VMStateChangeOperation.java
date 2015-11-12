package bds.clemson.nfv.workflow.compute;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VmState;

import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;

public abstract class VMStateChangeOperation extends VMOperation {
	protected String vmId;

	protected void mapProperties(Properties[] prop) throws UsageException {
		vmId = Configuration.map(prop, Configuration.Key.VM_ID, Requirement.REQUIRED);
	}

}
