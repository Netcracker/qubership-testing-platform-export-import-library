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

package org.qubership.atp.ei.ntt.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.Data;

@Data
public class UserInfo {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;

    /**
     * Add a new role to roles. If roles is empty, initialize it.
     *
     * @param role to add to roles.
     */
    public void addRole(String role) {
        if (Objects.isNull(this.roles)) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
    }
}
