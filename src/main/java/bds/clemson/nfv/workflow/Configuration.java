package bds.clemson.nfv.workflow;

import java.util.Properties;

import bds.clemson.nfv.exception.UsageException;

public class Configuration {
	public enum Requirement {
		REQUIRED,
		OPTIONAL
	}

	public static String map(Properties properties, String key, Requirement required) throws UsageException {
		String ret = properties.getProperty(key);

		if (ret == null && required == Requirement.REQUIRED)
			throw new UsageException("The property '" + key + "' is required.");

		return ret;
	}
}
