/*
 * # Copyright 2024-2025 NetCracker Technology Corporation
 * #
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * # you may not use this file except in compliance with the License.
 * # You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * # Unless required by applicable law or agreed to in writing, software
 * # distributed under the License is distributed on an "AS IS" BASIS,
 * # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * # See the License for the specific language governing permissions and
 * # limitations under the License.
 */

package org.qubership.atp.ei.node.exceptions;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class ExportException extends RuntimeException {
    public ExportException(String message, Throwable e) {
        super(message, e);
    }

    public ExportException(String message) {
        super(message);
    }

    /**
     * Throw exception with text format.
     */
    public static void throwException(String message, Object... vars) throws ExportException {
        throw build(message, vars);
    }

    /**
     * Build export exception.
     *
     * @param message the message
     * @param vars    the vars
     * @return the export exception
     * @throws ExportException the export exception
     */
    public static ExportException build(String message, Object... vars) throws ExportException {
        FormattingTuple formattedMessage = MessageFormatter.arrayFormat(message, vars);
        if (formattedMessage.getThrowable() != null) {
            return new ExportException(formattedMessage.getMessage(), formattedMessage.getThrowable());
        } else {
            return new ExportException(formattedMessage.getMessage());
        }
    }
}
