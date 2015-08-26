package bds.clemson.nfv.workflow;

import java.util.Properties;

import bds.clemson.nfv.exception.UsageException;

public class Configuration {
	public enum Field {
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
		SHELL_KEY,
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

	public static String map(Properties properties, Field field, Requirement required) throws UsageException {
		String ret = properties.getProperty(field.toString());

		if (ret == null && required == Requirement.REQUIRED)
			throw new UsageException("The property '" + field.toString() + "' is required.");

		return ret;
	}
}
