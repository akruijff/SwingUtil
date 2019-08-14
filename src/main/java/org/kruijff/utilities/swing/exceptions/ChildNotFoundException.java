/*
 * Copyright (c) Alex de Kruijff {@literal <swingutil@akruijff.dds.nl>}
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.kruijff.utilities.swing.exceptions;

import org.awaitility.core.ConditionTimeoutException;

/**
 * @author Alex de Kruijff {@literal <swingutil@akruijff.dds.nl>}
 */
public class ChildNotFoundException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ChildNotFoundException(String name) {
        super("name = " + name);
    }

    public ChildNotFoundException(String name, ConditionTimeoutException ex) {
        super("name = " + name, ex);
    }

    public ChildNotFoundException(int index) {
        super("index = " + index);
    }

    public ChildNotFoundException(int index, ConditionTimeoutException ex) {
        super("index = " + index, ex);
    }
}
