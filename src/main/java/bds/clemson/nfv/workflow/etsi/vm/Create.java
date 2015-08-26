package bds.clemson.nfv.workflow.etsi.vm;

import java.util.Properties;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.MachineImage;
import org.dasein.cloud.compute.MachineImageState;
import org.dasein.cloud.compute.MachineImageSupport;
import org.dasein.cloud.compute.Platform;
import org.dasein.cloud.compute.VMLaunchOptions;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineProduct;
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

import bds.clemson.nfv.exception.ConfigurationException;
import bds.clemson.nfv.exception.ResourcesException;
import bds.clemson.nfv.exception.UsageException;
import bds.clemson.nfv.workflow.Configuration;
import bds.clemson.nfv.workflow.VMOperation;

public class Create extends VMOperation {

	protected Create() throws UnsupportedOperationException {
		super();
	}

	private String hostName;
	private String friendlyName;
	private String description;
	private String architectureName;
	private String productName;
	private String machineImageId;
	private String shellUsername;
	private String shellKeyId;
	private String shellPassword;
	private String rootVolumeProductId;
	private String vlanId;
	private String subnetId;
	

	private VirtualMachine launched;

	protected void mapProperties(Properties prop) throws UsageException {
		hostName = Configuration.map(prop, "DSN_CMD_HOSTNAME", Configuration.Requirement.OPTIONAL);
		friendlyName = Configuration.map(prop, "DSN_CMD_FRIENDLYNAME", Configuration.Requirement.REQUIRED);
		description = Configuration.map(prop, "DSN_CMD_DESCRIPTION", Configuration.Requirement.OPTIONAL);
		architectureName = Configuration.map(prop, "DSN_CMD_ARCHITECTURE", Configuration.Requirement.REQUIRED);
		productName = Configuration.map(prop, "DSN_CMD_PRODUCT", Configuration.Requirement.REQUIRED);
		machineImageId = Configuration.map(prop, "DSN_CMD_IMAGE", Configuration.Requirement.REQUIRED);
		shellUsername = Configuration.map(prop, "DSN_CMD_SHELL_USERNAME", Configuration.Requirement.OPTIONAL);
		shellKeyId = Configuration.map(prop, "DSN_CMD_SHELL_KEY", Configuration.Requirement.OPTIONAL);
		shellPassword = Configuration.map(prop, "DSN_CMD_SHELL_PASSWORD", Configuration.Requirement.OPTIONAL);
		rootVolumeProductId = Configuration.map(prop, "DSN_CMD_ROOT_VOLUME_PRODUCT", Configuration.Requirement.OPTIONAL);
		vlanId = Configuration.map(prop, "DSN_CMD_VLAN", Configuration.Requirement.OPTIONAL);
		subnetId = Configuration.map(prop, "DSN_CMD_SUBNET", Configuration.Requirement.OPTIONAL);
	}
	
	public static void main(String[] args) throws UnsupportedOperationException {
		Create operation = new Create();
		operation.execute();

//    	System.out.println("Launched: " + command.getLaunched().getName() + "[" + command.getLaunched().getProviderVirtualMachineId() + "] (" + command.getLaunched().getCurrentState() + ")");
//      System.out.println("Launch complete (" + command.getLaunched().getCurrentState() + ")");
		System.out.println(operation.getLaunched().getProviderVirtualMachineId());
	}

    protected void executeInternal() throws InternalException, CloudException, ResourcesException, ConfigurationException, OperationNotSupportedException {
    	super.executeInternal();

    	MachineImageSupport imageSupport = computeServices.getImageSupport();

        if( imageSupport == null )
            throw new UnsupportedOperationException(provider.getCloudName() + " does not support machine images.");

        // find a target architecture and VM product
        Architecture targetArchitecture = this.getSupportedArchitecture(architectureName);

        if (targetArchitecture == null)
        	throw new UnsupportedOperationException(provider.getCloudName() + " does not support the '" + architectureName + "' architecture.");

        VirtualMachineProduct product = vmSupport.getProduct(productName);

        if (product == null)
        	throw new ResourcesException(provider.getCloudName() + " does not have a '" + productName + "' VM product.");
        
        boolean supported = false;
        
        for (Architecture architecture : product.getArchitectures())
        	if (architecture.equals(targetArchitecture)) {
        		supported = true;
        		break;
        	}

        if (supported == false)
        	throw new ResourcesException("The '" + productName + "' product is not available for the " + targetArchitecture + " architecture.");
        
        MachineImage image = imageSupport.getImage(machineImageId);

        if (image == null)
        	throw new ResourcesException("No such image '" + machineImageId + "'.");

        if (!image.getCurrentState().equals(MachineImageState.ACTIVE))
        	throw new ResourcesException("Image '" + machineImageId + "' is not active.");
        
        if (!image.getArchitecture().equals(targetArchitecture))
        	throw new ResourcesException("Image '" + machineImageId + "' is meant for the " + image.getArchitecture() + " architecture.");
        
        Platform platform = image.getPlatform();

        VMLaunchOptions options = VMLaunchOptions.getInstance(
        	product.getProviderProductId(),
        	machineImageId,
        	hostName != null ? hostName : friendlyName,
        	friendlyName,
        	description != null ? description : friendlyName
        );

        Requirement req = vmSupport.getCapabilities().identifyShellKeyRequirement(platform);
        
    	if (req.equals(Requirement.REQUIRED)
    	 || (req.equals(Requirement.OPTIONAL) && shellKeyId != null)) {

            if (shellKeyId == null)
            	throw new ConfigurationException("No shell key ID provided, but shell keys are required.");

            IdentityServices identity = provider.getIdentityServices();

            if( identity == null )
                throw new UnsupportedOperationException("No identity services exist, but shell keys are required.");

            ShellKeySupport keySupport = identity.getShellKeySupport();

            if( keySupport == null )
            	throw new UnsupportedOperationException("No shell key support exists, but shell keys are required.");
            
            SSHKeypair keyPair = keySupport.getKeypair(shellKeyId);
            
            if (keyPair == null)
            	throw new ConfigurationException("The shell key ID '" + shellKeyId + "' is invalid.");

            options.withBootstrapKey(keyPair.getProviderKeypairId());
        }

    	req = vmSupport.getCapabilities().identifyPasswordRequirement(platform);
    	
        if (req.equals(Requirement.REQUIRED)
         || (req.equals(Requirement.OPTIONAL) && shellUsername != null && shellPassword != null)) {

        	if (shellUsername == null)
        		throw new ConfigurationException("No shell username provided, but shell username is required.");
        	
        	// you must specify a password when launching a VM
        	if (shellPassword == null)
        		throw new ConfigurationException("No shell password provided, but shell password is required.");

        	options.withBootstrapUser(shellUsername, shellPassword);
        }

        req = vmSupport.getCapabilities().identifyRootVolumeRequirement();
        
        if (req.equals(Requirement.REQUIRED)
         || (req.equals(Requirement.OPTIONAL) && rootVolumeProductId != null)) {

            VolumeSupport volumeSupport = computeServices.getVolumeSupport();

            if( volumeSupport == null )
            	throw new UnsupportedOperationException("A root volume product is required, but no volume support exists.");

            VolumeProduct rootVolumeProduct = null;
            
        	if (rootVolumeProductId == null) { // if none is specified, try to find the minimum requirement
                long vpSize = 0L;

                for( VolumeProduct prd : volumeSupport.listVolumeProducts() ) {
                    Storage<Gigabyte> size = prd.getMinVolumeSize();

                    if (rootVolumeProduct == null || (size != null && size.getQuantity().longValue() > 0L && prd.getMinVolumeSize().getQuantity().longValue() < vpSize) ) {
                    	rootVolumeProduct = prd;
                        size = rootVolumeProduct.getMinVolumeSize();

                        if (size != null)
                            vpSize = size.getQuantity().longValue();

                        if (!volumeSupport.getCapabilities().isVolumeSizeDeterminedByProduct()) // size is not included in the product definition
                            break;
                    }
                }
        	}
        	else {
        		for (VolumeProduct prd : volumeSupport.listVolumeProducts()) {
        			if (prd.getProviderProductId().equals(rootVolumeProductId)) {
        				rootVolumeProduct = prd;
        				break;
        			}
        		}
        	}

            if (rootVolumeProduct == null)
            	throw new ResourcesException("Unable to identify any volume products.");
            
            options.withRootVolumeProduct(rootVolumeProduct.getProviderProductId());
        }

        req = vmSupport.getCapabilities().identifyVlanRequirement();
        
        if (req.equals(Requirement.REQUIRED)
         || (req.equals(Requirement.OPTIONAL) && vlanId != null)) {

        	if (vlanId == null)
        		throw new ConfigurationException("A VLAN is required for launching a VM, but no VLAN ID was provided.");
        	
            NetworkServices network = provider.getNetworkServices();

            if (network == null)
            	throw new UnsupportedOperationException("No network services exist even though a VLAN is required for launching a VM.");

            VLANSupport vlanSupport = network.getVlanSupport();

            if (vlanSupport == null)
            	throw new UnsupportedOperationException("No VLANs are supported in " + provider.getCloudName() + " event though a VLAN is required to launch a VM.");

            VLAN vlan = vlanSupport.getVlan(vlanId);

            if (vlan == null)
            	throw new ResourcesException("VLAN '" + vlanId + "' does not exist.");

            if (!vlan.getCurrentState().equals(VLANState.AVAILABLE))
            	throw new ResourcesException("VLAN " + vlanId + " is not available.");

            req = vlanSupport.getCapabilities().getSubnetSupport();
            
            if (req.equals(Requirement.REQUIRED)
             || (req.equals(Requirement.OPTIONAL) && subnetId != null)) {
            	
            	if (subnetId == null)
            		throw new ConfigurationException("A subnet is required for launching a VM, but no subnet ID was provided.");

            	Subnet subnet = vlanSupport.getSubnet(subnetId);

                if (subnet == null)
                	throw new ResourcesException("Subnet '" + subnetId + "' does not exist.");
                	
                if (!subnet.getCurrentState().equals(SubnetState.AVAILABLE))
                	throw new ResourcesException("Subnet " + subnetId + " is not available.");

                options.inSubnet(null, vlan.getProviderDataCenterId(), vlan.getProviderVlanId(), subnet.getProviderSubnetId());
            }
            else {
                options.inVlan(null, vlan.getProviderDataCenterId(), vlan.getProviderVlanId());
            }
        }

        VirtualMachine launching = null;
        launching = vmSupport.launch(options);

        while( launching != null && launching.getCurrentState().equals(VmState.PENDING) ) {
            try { Thread.sleep(1000L); }
            catch( InterruptedException ignore ) { }

            launching = vmSupport.getVirtualMachine(launching.getProviderVirtualMachineId());
        }

        if( launching == null ) {
            throw new CloudException("VM self-terminated before entering a usable state.");
        }

        this.launched = launching;
    }

	public VirtualMachine getLaunched() {
		return launched;
	}
}
