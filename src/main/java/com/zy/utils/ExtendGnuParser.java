package com.zy.utils;

import java.util.ListIterator;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

public class ExtendGnuParser extends GnuParser {
	private boolean ignoreUnrecognizedOption;

	public ExtendGnuParser(final boolean ignoreUnrecognizedOption) {
		this.ignoreUnrecognizedOption = ignoreUnrecognizedOption;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void processOption(final String arg, final ListIterator iter)
			throws ParseException {
		boolean hasOption = getOptions().hasOption(arg);

		if (hasOption || !ignoreUnrecognizedOption) {
			super.processOption(arg, iter);
		}
	}

}
