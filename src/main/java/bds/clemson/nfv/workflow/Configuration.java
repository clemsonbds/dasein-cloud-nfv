package bds.clemson.nfv.workflow;

import java.util.Properties;

import org.dasein.cloud.Requirement;

import bds.clemson.nfv.exception.UsageException;

public class Configuration {
	public enum Key {
		HOSTNAME,
		NAME,
		DESCRIPTION,
		ARCHITECTURE_NAME,

		VM_PRODUCT_NAME,
		IMAGE_NAME,

		VOLUME_PRODUCT_NAME,
		VOLUME_CAPACITY,
		VOLUME_IOPS,

		SHELL_USERNAME,
		SHELL_KEY_NAME,
		SHELL_PASSWORD,

		VM_ID,
		VLAN_ID,
		SUBNET_ID,
		DEVICE_ID,
		DATACENTER_ID,

		OPERATION;

		public String toString() {
			return "DSN_CMD_" + this.name();
		}
	}
	
	public static String map(Properties[] propertiesSets, Key key, Requirement requirement) throws UsageException {
		String value = null;
		
		for (Properties properties : propertiesSets)
			if (properties.containsKey(key.toString()))
				value = properties.getProperty(key.toString());
		
		if (value == null && requirement == Requirement.REQUIRED)
			throw new UsageException("The property '" + key.toString() + "' is required.");

		return value;
	}
}
