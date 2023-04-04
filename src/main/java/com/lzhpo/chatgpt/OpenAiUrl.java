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

package com.lzhpo.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzhpo
 */
@Getter
@AllArgsConstructor
public enum OpenAiUrl {

    /**
     * Create moderation API.
     */
    MODERATIONS("POST", "https://api.openai.com/v1/moderations"),

    /**
     * Create completion API.
     */
    COMPLETIONS("POST", "https://api.openai.com/v1/completions"),

    /**
     * Create edit API.
     */
    EDITS("POST", "https://api.openai.com/v1/edits"),

    /**
     * Create chat completion API.
     */
    CHAT_COMPLETIONS("POST", "https://api.openai.com/v1/chat/completions"),

    /**
     * List models API.
     */
    LIST_MODELS("GET", "https://api.openai.com/v1/models"),

    /**
     * Retrieve model by {@code modelId} API.
     */
    RETRIEVE_MODEL("GET", "https://api.openai.com/v1/models/{model}"),

    /**
     * Create embeddings API.
     */
    EMBEDDINGS("POST", "https://api.openai.com/v1/embeddings"),

    /**
     * List files API.
     */
    LIST_FILES("GET", "https://api.openai.com/v1/files"),

    /**
     * Upload file API.
     */
    UPLOAD_FILE("POST", "https://api.openai.com/v1/files"),

    /**
     * Delete file API.
     */
    DELETE_FILE("DELETE", "https://api.openai.com/v1/files/{file_id}"),

    /**
     * Retrieve file API.
     */
    RETRIEVE_FILE("GET", "https://api.openai.com/v1/files/{file_id}"),

    /**
     * Retrieve file content API.
     */
    RETRIEVE_FILE_CONTENT("GET", "https://api.openai.com/v1/files/{file_id}/content"),

    /**
     * Create fine-tune API.
     */
    CREATE_FINE_TUNE("POST", "https://api.openai.com/v1/fine-tunes"),

    /**
     * List fine-tune API.
     */
    LIST_FINE_TUNE("GET", "https://api.openai.com/v1/fine-tunes"),

    /**
     * Retrieve fine-tune API.
     */
    RETRIEVE_FINE_TUNE("GET", "https://api.openai.com/v1/fine-tunes/{fine_tune_id}"),

    /**
     * Cancel fine-tune API.
     */
    CANCEL_FINE_TUNE("POST", "https://api.openai.com/v1/fine-tunes/{fine_tune_id}/cancel"),

    /**
     * List fine-tune events API.
     */
    LIST_FINE_TUNE_EVENTS("GET", "https://api.openai.com/v1/fine-tunes/{fine_tune_id}/events"),

    /**
     * Delete fine-tune events API.
     */
    DELETE_FINE_TUNE_EVENTS("DELETE", "https://api.openai.com/v1/models/{model}"),

    /**
     * Create transcription API.
     */
    CREATE_TRANSCRIPTION("POST", "https://api.openai.com/v1/audio/transcriptions"),

    /**
     * Create translation API.
     */
    CREATE_TRANSLATION("POST", "https://api.openai.com/v1/audio/translations"),

    /**
     * Create image API.
     */
    CREATE_IMAGE("POST", "https://api.openai.com/v1/images/generations"),

    /**
     * Create image edit API.
     */
    CREATE_IMAGE_EDIT("POST", "https://api.openai.com/v1/images/edits"),

    /**
     * Create image variation API.
     */
    CREATE_IMAGE_VARIATION("POST", "https://api.openai.com/v1/images/variations"),

    /**
     * Query billing credit grants API.
     */
    BILLING_CREDIT_GRANTS("GET", "https://api.openai.com/dashboard/billing/credit_grants"),

    /**
     * Query credit grants from openAi API.
     */
    USERS("GET", "https://api.openai.com/v1/organizations/{organizationId}/users"),

    /**
     * Query billing subscription API.
     */
    BILLING_SUBSCRIPTION("GET", "https://api.openai.com/v1/dashboard/billing/subscription"),

    /**
     * Query billing usage API.
     */
    BILLING_USAGE("GET", "https://api.openai.com/v1/dashboard/billing/usage?start_date={start_date}&end_date={end_date}");

    /**
     * Request http method.
     */
    private final String httpMethod;

    /**
     * Request url.
     */
    private final String defaultUrl;
}
