package bds.clemson.nfv.workflow;

import java.util.Properties;

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

		OPERATION;

		public String toString() {
			return "DSN_CMD_" + this.name();
		}
	}
	
	public enum Requirement {
		REQUIRED,
		OPTIONAL
	}

	public static String map(Properties[] propertiesSets, Key key, Requirement required) throws UsageException {
		String value = null;
		
		for (Properties properties : propertiesSets)
			if (properties.containsKey(key))
				value = properties.getProperty(key.toString());
		
		if (value == null && required == Requirement.REQUIRED)
			throw new UsageException("The property '" + key.toString() + "' is required.");

		return value;
	}
}
