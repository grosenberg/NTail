/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package net.certiv.ntail.util.log;

import java.util.Locale;

import org.eclipse.core.runtime.IStatus;

/**
 * Levels used for identifying the severity of an event. Levels are organized from most specific to
 * least:
 * <table>
 * <tr>
 * <th>Name</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link #OFF}</td>
 * <td>No events will be logged.</td>
 * </tr>
 * <tr>
 * <td>{@link #FATAL}</td>
 * <td>A fatal event that will prevent the application from continuing.</td>
 * </tr>
 * <tr>
 * <td>{@link #ERROR}</td>
 * <td>An error in the application, possibly recoverable.</td>
 * </tr>
 * <tr>
 * <td>{@link #WARN}</td>
 * <td>An event that might possible lead to an error.</td>
 * </tr>
 * <tr>
 * <td>{@link #INFO}</td>
 * <td>An event for informational purposes.</td>
 * </tr>
 * <tr>
 * <td>{@link #DEBUG}</td>
 * <td>A general debugging event.</td>
 * </tr>
 * <tr>
 * <td>{@link #TRACE}</td>
 * <td>A fine-grained debug message, typically capturing the flow through the application.</td>
 * </tr>
 * <tr>
 * <td>{@link #ALL}</td>
 * <td>All events should be logged.</td>
 * </tr>
 * </table>
 * <p>
 * Typically, configuring a level in a filter or on a logger will cause logging events of that level
 * and those that are more specific to pass through the filter. A special level, {@link #ALL}, is
 * guaranteed to capture all levels when used in logging configurations.
 * </p>
 */
public enum Level {

	/** No events will be logged. */
	OFF(IStatus.OK),

	/** Events that will prevent the application from continuing. */
	FATAL(IStatus.ERROR),

	/** A possibly recoverable error event in the application. */
	ERROR(IStatus.ERROR),

	/** A potential error event. */
	WARN(IStatus.WARNING),

	/** An informational event. */
	INFO(IStatus.INFO),

	/** A general debugging event. */
	DEBUG(IStatus.INFO),

	/** Fine-grained debug message. */
	TRACE(IStatus.INFO);

	private int severity;

	Level(int severity) {
		this.severity = severity;
	}

	/**
	 * Compares this level against the levels passed as arguments and returns true if this level is
	 * in between the given levels.
	 *
	 * @param minLevel The minimum level to test.
	 * @param maxLevel The maximum level to test.
	 * @return {@code true} if this level is in between the given levels
	 */
	public boolean inRange(Level min, Level max) {
		return this.ordinal() >= min.ordinal() && this.ordinal() <= max.ordinal();
	}

	/**
	 * Compares this level against the level passed as an argument and returns true if this level is
	 * the same or is less specific.
	 * <p>
	 * Concretely, {@link #ALL} is less specific than {@link #TRACE}, which is less specific than
	 * {@link #DEBUG}, which is less specific than {@link #INFO}, which is less specific than
	 * {@link #WARN}, which is less specific than {@link #ERROR}, which is less specific than
	 * {@link #FATAL}, and finally {@link #OFF}, which is the most specific standard level.
	 * </p>
	 *
	 * @param level The level to test.
	 * @return True if this level Level is less specific or the same as the given Level.
	 */
	public boolean isLessSpecificThan(Level level) {
		return this.ordinal() >= level.ordinal();
	}

	/**
	 * Compares this level against the level passed as an argument and returns true if this level is
	 * the same or is more specific.
	 * <p>
	 * Concretely, {@link #FATAL} is more specific than {@link #ERROR}, which is more specific than
	 * {@link #WARN}, etc., until {@link #TRACE}, and finally {@link #ALL}, which is the least
	 * specific standard level. The most specific level is {@link #OFF}.
	 * </p>
	 *
	 * @param level The level to test.
	 * @return True if this level Level is more specific or the same as the given Level.
	 */
	public boolean isMoreSpecificThan(Level level) {
		return this.ordinal() <= level.ordinal();
	}

	/**
	 * Retrieves an existing Level by name.
	 *
	 * @param name The name of the level.
	 * @return the Level
	 * @throws java.lang.IllegalArgumentException if the name is unknown.
	 */
	public Level forName(String name) {
		try {
			return valueOf(Level.class, name.trim().toUpperCase(Locale.ENGLISH));
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Illegal Level name [%s].", name));
		}
	}

	/**
	 * Converts the string passed as argument to a level. If the conversion fails, then this method
	 * returns {@link #DEBUG}.
	 *
	 * @param level The name of the desired Level.
	 * @return The Level associated with the String.
	 */
	public Level toLevel(String level) {
		return toLevel(level, Level.DEBUG);
	}

	/**
	 * Converts the string passed as argument to a level. If the conversion fails, then this method
	 * returns the value of <code>defaultLevel</code>.
	 *
	 * @param name         The name of the desired Level.
	 * @param defaultLevel The Level to use if the String is invalid.
	 * @return The Level associated with the String.
	 */
	public Level toLevel(String name, Level defaultLevel) {
		try {
			return forName(name);
		} catch (IllegalArgumentException e) {
			return defaultLevel;
		}
	}

	/** Returns the associated IStatus#severity. */
	public int severity() {
		return severity;
	}
}
