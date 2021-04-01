// SPDX-License-Identifier: MIT
package com.daimler.sechub.model;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileLocationExplorer {

	private static final Pattern PATTERN_ALL_SLASHES=Pattern.compile("\\/");
	
	private List<Path> searchFolders = new ArrayList<>();

	public List<Path> getSearchFolders() {
		return searchFolders;
	}

	/**
	 * Searches for given location string
	 * 
	 * @param location represents known location to search for inside defined search folders
	 * @return list of matching files, never <code>null</code>
	 * @throws IOException
	 */
	public List<Path> searchFor(String location) throws IOException {
		List<Path> result = new ArrayList<>();
		
		FileSystem defaultFileSystem = FileSystems.getDefault();
		String separator = defaultFileSystem.getSeparator();
		
		String osSpecificPathLocationRegexp = convertLocationOSSpecificRegExp(location, separator);
		
		PathMatcher matcher = defaultFileSystem.getPathMatcher("regex:.*" + osSpecificPathLocationRegexp);
		
		for (Path searchFolder : searchFolders) {
			searchFilesRecursive(matcher, searchFolder,result);
		}

		return result;
	}

	/**
	 * Converts given location to OS specific location java regexp. E.g. when seperator is '\' (windows), a location
	 * like 'src/main/java/Test1.java' will be transformed to 'src\\main\\java\\Test1.java'.
	 * @param location
	 * @param separator
	 * @return OS specific location
	 */
	String convertLocationOSSpecificRegExp(String location, String separator) {
		if ("\\".equals(separator) ) {
			Matcher matcher = PATTERN_ALL_SLASHES.matcher(location);
			if (matcher.find()) {
				return matcher.replaceAll("\\\\\\\\");
			}
		}
		return location;
	}

	void searchFilesRecursive(PathMatcher matcher, Path searchFolder, List<Path> result) throws IOException {
		
		Collection<Path> found = find(searchFolder, matcher);
		for (Path path: found) {
			result.add(path);
		}
	}

	protected static Collection<Path> find(Path searchDirectory, PathMatcher matcher) throws IOException {
		try (Stream<Path> files = Files.walk(searchDirectory)) {
			return files.filter(matcher::matches).collect(Collectors.toList());
		}

	}

}
