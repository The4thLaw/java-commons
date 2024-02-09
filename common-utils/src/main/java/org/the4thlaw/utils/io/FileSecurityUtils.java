package org.the4thlaw.utils.io;

import java.nio.file.Path;

/**
 * Utilities to manage security related to files.
 */
public final class FileSecurityUtils {
    private FileSecurityUtils() {
        // No instance
    }

    /**
	 * Ensures that a child path is indeed child to the provided parent path.
	 * 
	 * @param parent The parent path.
	 * @param child The child path.
	 * @throws SecurityException if the assertion fails.
	 */
	public static void assertChildOf(Path parent, Path child) {
		Path absoluteParent = parent.toAbsolutePath().normalize();
		Path absoluteChild = child.toAbsolutePath().normalize();
		if (!absoluteChild.startsWith(absoluteParent)) {
			throw new SecurityException("Attempted directory traversal: " + parent + " is not a parent of " + child);
		}
	}
}
