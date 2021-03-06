// SPDX-License-Identifier: MIT
package com.daimler.sechub.report;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

public class SecHubStandardReportNamePatternPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "com.daimler.sechub";
	public static final String PROPERTY_IS_BASHFILE_WITHOUT_EXTENSION = "isSecHubReportFile";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!PROPERTY_IS_BASHFILE_WITHOUT_EXTENSION.equals(property)) {
			return false;
		}
		if (!(receiver instanceof IFile)) {
			/* not supported */
			return false;
		}
		IResource resource = (IResource) receiver;
		String extension = resource.getFileExtension();
		if (!"json".equalsIgnoreCase(extension)) {
			return false;
		}
		String fileName = resource.getName();
		if (fileName != null && fileName.startsWith("sechub_report")) {
			return true;
		}
		return false;
	}

}