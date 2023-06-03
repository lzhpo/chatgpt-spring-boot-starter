/*
 * Copyright 2023 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.chatgpt.properties;

import java.net.Proxy;
import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class OpenAiProxy {

    /**
     * The proxy host.
     */
    private String host;

    /**
     * The proxy port.
     */
    private int port;

    /**
     * The proxy type.
     */
    private Proxy.Type type;

    /**
     * The username.
     */
    private String username;

    /**
     * The password.
     */
    private String password;

    /**
     * The header name to provide to proxy server.
     */
    private String headerName = "Proxy-Authorization";
}
