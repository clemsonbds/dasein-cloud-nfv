package bds.clemson.nfv.workflow.etsi;

import java.util.Iterator;
import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.ImageClass;
import org.dasein.cloud.compute.ImageFilterOptions;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.MachineImageState;
import org.dasein.cloud.compute.MachineImageSupport;
import org.dasein.cloud.compute.Platform;
import org.dasein.cloud.compute.VMLaunchOptions;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.compute.VolumeProduct;
import org.dasein.cloud.compute.VolumeSupport;
import org.dasein.cloud.identity.IdentityServices;
import org.dasein.cloud.identity.SSHKeypair;
import org.dasein.cloud.identity.ShellKeySupport;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.network.Subnet;
import org.dasein.cloud.network.SubnetState;
import org.dasein.cloud.network.VLAN;
import org.dasein.cloud.network.VLANState;
import org.dasein.cloud.network.VLANSupport;
import org.dasein.util.uom.storage.Gigabyte;
import org.dasein.util.uom.storage.Storage;

import bds.clemson.nfv.exception.CapabilitiesException;
import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ExecutionException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;
import bds.clemson.nfv.workflow.Operation;
import bds.clemson.nfv.workflow.VMOperation;

public class CreateVirtualMachine extends VMOperation {

	protected CreateVirtualMachine() throws CapabilitiesException {
		super();
	}

	private String hostName;
	private String friendlyName;
	private String architectureName;
	private String productName;

	private VirtualMachine launched;

	protected void mapProperties(Properties prop) throws UsageException {
		hostName = Configuration.map(prop, "DSN_CMD_HOSTNAME", Configuration.Requirement.REQUIRED);
		friendlyName = Configuration.map(prop, "DSN_CMD_FRIENDLYNAME", Configuration.Requirement.REQUIRED);
		architectureName = Configuration.map(prop, "DSN_CMD_ARCHITECTURE", Configuration.Requirement.REQUIRED);
		productName = Configuration.map(prop, "DSN_CMD_PRODUCT", Configuration.Requirement.REQUIRED);
	}
	
	public static void main(String[] args) throws CapabilitiesException {
		CreateVirtualMachine operation = new CreateVirtualMachine();
		operation.execute();

//    	System.out.println("Launched: " + command.getLaunched().getName() + "[" + command.getLaunched().getProviderVirtualMachineId() + "] (" + command.getLaunched().getCurrentState() + ")");
//      System.out.println("Launch complete (" + command.getLaunched().getCurrentState() + ")");
		System.out.println(operation.getLaunched().getProviderVirtualMachineId());
	}

    protected void executeInternal() throws InternalException, CloudException, CapabilitiesException, ExecutionException, ResourcesException, ConfigurationException {
    	super.executeInternal();
    	MachineImageSupport imgSupport = computeServices.getImageSupport();

        if( imgSupport == null )
            throw new CapabilitiesException(provider.getCloudName() + " does not support machine images.");

        // find a target architecture and VM product
        Architecture targetArchitecture = null;

        for( Architecture architecture : vmSupport.getCapabilities().listSupportedArchitectures() ) {
        	if (architectureName.equals(architecture.toString())) {
                targetArchitecture = architecture;
                break;
        	}
        }

        if (targetArchitecture == null)
        	throw new CapabilitiesException(provider.getCloudName() + " does not support the " + architectureName + " architecture.");

        VirtualMachineProduct product = null;

        for (VirtualMachineProduct p: vmSupport.listProducts(targetArchitecture)) {
        	if (productName.equals(p.getProviderProductId())) {
        		product = p;
        		break;
        	}
        }

        if (product == null)
            throw new CapabilitiesException(provider.getCloudName() + " does not have a '" + productName + "' product for the " + targetArchitecture + " architecture.");

        Platform platform = Platform.UNKNOWN;
        String machineImageId = null;

        for( MachineImage image : imgSupport.listImages(ImageFilterOptions.getInstance(ImageClass.MACHINE)) ) {
            if( image.getCurrentState().equals(MachineImageState.ACTIVE) && image.getArchitecture().equals(targetArchitecture)) {
                machineImageId = image.getProviderMachineImageId();
                platform = image.getPlatform();
                break;
            }
        }
        if( machineImageId == null ) {
            throw new ResourcesException("No active machine images exist for " + targetArchitecture);
        }

        VMLaunchOptions options = VMLaunchOptions.getInstance(product.getProviderProductId(), machineImageId, hostName, friendlyName, friendlyName);

        if( vmSupport.getCapabilities().identifyShellKeyRequirement(platform).equals(Requirement.REQUIRED) ) {
            // you must specify an SSH key when launching the VM
            // we'll look one up
            IdentityServices identity = provider.getIdentityServices();

            if( identity == null ) {
                throw new ConfigurationException("No identity services exist, but shell keys are required.");
            }
            ShellKeySupport keySupport = identity.getShellKeySupport();

            if( keySupport == null ) {
            	throw new CapabilitiesException("No shell key support exists, but shell keys are required.");
            }
            Iterator<SSHKeypair> keys = keySupport.list().iterator();
            String keyId = null;

            if( keys.hasNext() ) {
                keyId = keys.next().getProviderKeypairId();
            }
            if( keyId == null ) {
                // no keypair yet exists, so we'll create one
                if( keySupport.getCapabilities().identifyKeyImportRequirement().equals(Requirement.REQUIRED) ) {
                    // hmm, this cloud doesn't create them for you; it requires you to import them
                    // importing keys is beyond the scope of this example
                	throw new CapabilitiesException("Importing key pairs NYI, and thus won't work against " + provider.getCloudName() + ".");
                }

                // create the sample keypair
                keyId = keySupport.createKeypair("dsnex" + System.currentTimeMillis()).getProviderKeypairId();
            }
            if( keyId != null ) {
                options.withBootstrapKey(keyId);
            }
        }
        if( vmSupport.getCapabilities().identifyPasswordRequirement(platform).equals(Requirement.REQUIRED) ) {
            // you must specify a password when launching a VM
            options.withBootstrapUser("dsnexample", "pw" + System.currentTimeMillis());
        }
        if( vmSupport.getCapabilities().identifyRootVolumeRequirement().equals(Requirement.REQUIRED) ) {
            // let's look for the product with the smallest volume size
            VolumeSupport volumeSupport = computeServices.getVolumeSupport();

            if( volumeSupport == null ) {
            	throw new CapabilitiesException("A root volume product definition is required, but no volume support exists.");
            }

            boolean findSmallest = volumeSupport.getCapabilities().isVolumeSizeDeterminedByProduct();
            VolumeProduct vp = null;
            long vpSize = 0L;

            for( VolumeProduct prd : volumeSupport.listVolumeProducts() ) {
                Storage<Gigabyte> size = prd.getMinVolumeSize();

                if( vp == null || (size != null && size.getQuantity().longValue() > 0L && prd.getMinVolumeSize().getQuantity().longValue() < vpSize) ) {
                    vp = prd;
                    size = vp.getMinVolumeSize();
                    if( size != null ) {
                        vpSize = size.getQuantity().longValue();
                    }
                    if( !findSmallest ) { // size is not included in the product definition
                        break;
                    }
                }
            }
            if( vp == null ) {
            	throw new ResourcesException("Unable to identify any volume products.");
            }
            options.withRootVolumeProduct(vp.getProviderProductId());
        }
        if( vmSupport.getCapabilities().identifyVlanRequirement().equals(Requirement.REQUIRED) ) {
            NetworkServices network = provider.getNetworkServices();

            if( network == null ) {
            	throw new CapabilitiesException("No network services exist even though a VLAN is required for launching a VM.");
            }

            VLANSupport vlanSupport = network.getVlanSupport();

            if( vlanSupport == null ) {
            	throw new CapabilitiesException("No VLANs are supported in " + provider.getCloudName() + " event though a VLAN is required to launch a VM.");
            }
            VLAN vlan = null;

            for( VLAN v : vlanSupport.listVlans() ) {
                if( v.getCurrentState().equals(VLANState.AVAILABLE) ) {
                    vlan = v;
                    break;
                }
            }
            if( vlan == null ) {
            	throw new ResourcesException("VLAN support is required, but was not able to identify a VLAN in an available state");
            }
            if( vlanSupport.getCapabilities().getSubnetSupport().equals(Requirement.REQUIRED) ) {
                Subnet subnet = null;

                for( Subnet s : vlanSupport.listSubnets(vlan.getProviderVlanId()) ) {
                    if( s.getCurrentState().equals(SubnetState.AVAILABLE) ) {
                        subnet = s;
                    }
                }
                if( subnet != null ) { // let's just hope it works if no active subnet exists, probably won't
                    options.inVlan(null, vlan.getProviderDataCenterId(), subnet.getProviderSubnetId());
                }
                options.inVlan(null, vlan.getProviderDataCenterId(), vlan.getProviderVlanId());
            }
            else {
                options.inVlan(null, vlan.getProviderDataCenterId(), vlan.getProviderVlanId());
            }
        }

        VirtualMachine launching = null;
//        launching = vmSupport.launch(options);

        while( launching != null && launching.getCurrentState().equals(VmState.PENDING) ) {
            try { Thread.sleep(5000L); }
            catch( InterruptedException ignore ) { }

            launching = vmSupport.getVirtualMachine(launching.getProviderVirtualMachineId());
        }

        if( launching == null ) {
            throw new ExecutionException("VM self-terminated before entering a usable state");
        }

        this.launched = launching;
    }

	public VirtualMachine getLaunched() {
		return launched;
	}
}
