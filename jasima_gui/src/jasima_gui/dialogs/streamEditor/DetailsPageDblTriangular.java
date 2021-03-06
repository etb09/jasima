/*******************************************************************************
 * Copyright (c) 2010-2015 Torsten Hildebrandt and jasima contributors
 *
 * This file is part of jasima, v1.2.
 *
 * jasima is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jasima is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jasima.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package jasima_gui.dialogs.streamEditor;

import static jasima_gui.dialogs.streamEditor.util.Constraints.smallerOrEqualThan;

public class DetailsPageDblTriangular extends DetailsPageBase {

	public static final String INPUT_TYPE = "jasima.shopSim.util.modelDef.streams.DblTriangularDef";
	public static final String TITLE = "Triangular distribution";
	public static final String DESCRIPTION = "Defines a triangular destribution by its minimum and "
			+ "maximum value as well as its mode (most frequent value).";

	public DetailsPageDblTriangular() {
		super();

		FormProperty min = addDoubleProperty("minValue", "minimum value");
		FormProperty mode = addDoubleProperty("modeValue", "mode value");
		FormProperty max = addDoubleProperty("maxValue", "maximum value");

		addConstraint(smallerOrEqualThan(min, mode));
		addConstraint(smallerOrEqualThan(mode, max));
	}

	@Override
	public String getInputType() {
		return INPUT_TYPE;
	}

	@Override
	protected String getDescription() {
		return DESCRIPTION;
	}

	@Override
	protected String getTitle() {
		return TITLE;
	}
}
