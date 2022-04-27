/*
 * Copyright (c)  2021 Daniel Fiala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.server.request;

public enum RequestType {

    GET(true),
    HEAD(false),
    POST(true),
    PUT(false),
    DELETE(true),
    CONNECT(true),
    OPTIONS(true),
    TRACE(false),
    PATCH(true);

    private final boolean bodyAllowed;

    RequestType(boolean bodyAllowed) {
        this.bodyAllowed = bodyAllowed;
    }

    public boolean hasBody() {
        return bodyAllowed;
    }
}
