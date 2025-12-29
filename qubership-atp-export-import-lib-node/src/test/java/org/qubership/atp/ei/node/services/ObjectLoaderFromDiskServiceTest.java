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

package org.qubership.atp.ei.node.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.qubership.atp.ei.ntt.dto.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class ObjectLoaderFromDiskServiceTest {

    private ObjectLoaderFromDiskService objectLoaderFromDiskService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        objectLoaderFromDiskService = new ObjectLoaderFromDiskService();
    }

    @Test
    public void loadFileAsObject_shouldReturnMacro_WhenReadFromFilesystem() {
        Macros macroFromFile =
                objectLoaderFromDiskService
                        .loadFileAsObject(Paths.get("src/test/resources/ei/import/atp-macros"
                                + "/Macros/75a5c284-f86b-4edd-9343-3086732b5dad.json"), Macros.class);

        Macros macrosStandard = new Macros();
        macrosStandard.setUuid(UUID.fromString("75a5c284-f86b-4edd-9343-3086732b5dad"));
        macrosStandard.setEngine("javascript");
        macrosStandard.setContent("function main(date, format, timeZone) {     var calendar = Packages.java.util"
                + ".Calendar.getInstance();          if(timeZone != null){            var timezone = Packages.java"
                + ".util.TimeZone.getTimeZone(timeZone);            calendar.setTimeZone(timezone);        }       if"
                + "(format.equals(\"millis\")){        calendar.setTimeInMillis(Packages.java.lang.Long.parseLong"
                + "(date));            }    else {        var sdf = new Packages.java.text.SimpleDateFormat(format); "
                + "       var date1 = sdf.parse(date);        calendar.setTimeInMillis(date1.getTime());        }    "
                + "return Packages.java.lang.Integer.parseInt(calendar.get(Packages.java.util.Calendar.MONTH)+1);}");
        macrosStandard.setName("GET_MONTH");
        macrosStandard.setDescription("Returns month of the reported date with reported format in some timezone"
                + "(optional argument), e.g: $GET_MONTH('9.06.2013', 'dd.MM.yyyy')");
        macrosStandard.setModifiedBy(UUID.fromString("dcd1a899-09d6-42b8-bb59-8764d2912c10"));
        macrosStandard.setModifiedWhen(null);

        List<MacrosParameter> parameters = new ArrayList<>();
        MacrosParameter date = new MacrosParameter();
        date.setUuid(UUID.fromString("42b3d05c-577f-4063-8a98-f90f362d3014"));
        date.setName("date");
        date.setDescription("Date in specified format, e.g: '2015-01-12 10:30:00'");
        date.setOptional(false);
        MacrosParameter format = new MacrosParameter();
        format.setUuid(UUID.fromString("87bc11de-387c-4870-96b8-54eade2f7884"));
        format.setName("format");
        format.setDescription("Date and time pattern string, e.g: 'yyyy-MM-dd HH:mm:ss'");
        format.setOptional(false);
        MacrosParameter timezone = new MacrosParameter();
        timezone.setUuid(UUID.fromString("72516900-a561-4bf7-83ed-234717da6851"));
        timezone.setName("timezone");
        timezone.setDescription("General timezone for displaying the date, e.g: 'GMT+8'");
        timezone.setOptional(true);

        parameters.add(date);
        parameters.add(format);
        parameters.add(timezone);

        macrosStandard.setParameters(parameters);

        Assert.assertEquals("Macro from file should be equal to standard.", macrosStandard, macroFromFile);

    }

    @Test
    public void getListOfObjects_ShouldReturnMapFolderToMacroId_WhenInvokesWithCorrectFiles() {
        Map<UUID, Path> fileToMacroId = objectLoaderFromDiskService
                .getListOfObjects(Paths.get("src/test/resources/ei/import/atp-macros"), "Macros", null);

        Map<UUID, Path> fileToMacroIdStandard = new HashMap<>();
        fileToMacroIdStandard.put(UUID.fromString("75a5c284-f86b-4edd-9343-3086732b5dad"),
                Paths.get("src/test/resources/ei/import/atp-macros/Macros/75a5c284-f86b-4edd-9343-3086732b5dad"
                        + ".json"));

        assertEquals("Macro from file should be equal to standard.", fileToMacroIdStandard, fileToMacroId);
    }

    @Test
    public void getListOfObjects_resultIsEmpty_whenGetNonExistFolderWithObjects() {
        Map<UUID, Path> result = objectLoaderFromDiskService.getListOfObjects(Paths.get(""), "Macros",
                null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void givenReplacementMap_loadObjectFromFile_successReplacingId() {
        HashMap<UUID, UUID> map = new HashMap<>();
        UUID initId = UUID.fromString("75a5c284-f86b-4edd-9343-3086732b5dad");
        UUID expectedId = UUID.fromString("75a5c284-f86b-4edd-9343-5086732b5dad");
        map.put(initId, expectedId);

        Path path =
                Paths.get("src/test/resources/ei/import/atp-macros/Macros/75a5c284-f86b-4edd-9343-3086732b5dad.json");
        Macros result = objectLoaderFromDiskService.loadFileAsObjectWithReplacementMap(path, Macros.class, map);
        assertEquals(result.getUuid(), expectedId);
        assertNull(result.getModifiedBy());
    }

    @Test
    public void givenReplacementMap_loadObjectFromFileWithListOfUUID_andNoReplacmentForList_successReplacingId_noListOfNull() {
        HashMap<UUID, UUID> map = new HashMap<>();
        UUID initId = UUID.fromString("0eddae84-fc29-4000-b4b1-bd704c848366");
        UUID expectedId = UUID.fromString("0eddae84-fc29-4000-b4b1-bd704c848365");
        map.put(initId, expectedId);

        Path path =
                Paths.get(
                        "src/test/resources/ei/import/atp-catalogue/TestCase/0eddae84-fc29-4000-b4b1-bd704c848366.json");
        TestCase result = objectLoaderFromDiskService.loadFileAsObjectWithReplacementMap(path, TestCase.class, map);
        assertEquals(result.getUuid(), expectedId);
        assertTrue(CollectionUtils.isEmpty(result.getLabelIds()));
    }
}
