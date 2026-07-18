package org.the4thlaw.commons.services.io;

/**
 * All standard directories supported by the directory service.
 */
public enum StandardDirectory {
	/** Defines the base directory relative to which user-specific non-essential data files should be stored. */
	CACHE,
	/** Defines the base directory relative to which user-specific configuration files should be stored. */
	CONFIGURATION,
	/** Defines the base directory relative to which user-specific data files should be stored. */
	DATA,
	/** Defines the base directory relative to which user-specific export files should be stored. */
	EXPORT,
	/** Defines the base directory relative to which user-specific image files should be stored. */
	IMAGES,
	/**
	 * Defines the base directory relative to which user-specific non-essential runtime files and other file objects
	 * (such as sockets, named pipes, ...) should be stored.
	 */
	RUNTIME,
	/** Defines the base directory relative to which user-specific state files should be stored */
	STATE,
	/** Defines the base directory relative to which user-specific temporary files should be stored */
	TEMP,
	/** Defines the base directory relative to which user-specific non-essential thumbnail files should be stored. */
	THUMBNAILS
}
