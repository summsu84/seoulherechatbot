package com.chat.seoul.here.module.lib.conf;

/***********************************************************************************************************************
 *
 * API.AI Android SDK -  API.AI libraries usage example
 * =================================================
 *
 * Copyright (C) 2014 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************/

public abstract class Config {
    // copy this keys from your developer dashboard
    public static final String ACCESS_TOKEN = "c538e89975d34248bd4948e9e2fb1ad7";

    public static final LanguageConfig[] languages = new LanguageConfig[]{
            new LanguageConfig("en", "c538e89975d34248bd4948e9e2fb1ad7"),       //a11ea1d839e3446d84e402cb97cdadfb
            new LanguageConfig("ko", "c538e89975d34248bd4948e9e2fb1ad7")
    };

    public static final String[] events = new String[]{
            "hello_event",
            "goodbye_event",
            "how_are_you_event"
    };
}
