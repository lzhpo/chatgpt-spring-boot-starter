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

package com.lzhpo.chatgpt.entity.finetunes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lzhpo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateFineTuneRequest {

    /**
     * The ID of an uploaded file that contains training data.
     *
     * <p>See <a href="https://platform.openai.com/docs/api-reference/files/upload">upload file</a> for how to upload a file.
     *
     * <p>Your dataset must be formatted as a JSONL file, where each training example is a JSON object with the keys "prompt" and "completion".
     * Additionally, you must upload your file with the purpose fine-tune. See the <a href="https://platform.openai.com/docs/guides/fine-tuning/creating-training-data">fine-tuning guide</a> for more details.
     */
    @NotBlank
    @JsonProperty("training_file")
    private String trainingFile;

    /**
     * The ID of an uploaded file that contains validation data.
     *
     * <p>If you provide this file, the data is used to generate validation metrics periodically during fine-tuning.
     * These metrics can be viewed in <a href="https://platform.openai.com/docs/guides/fine-tuning/analyzing-your-fine-tuned-model">the fine-tuning results file</a>.
     * Your train and validation data should be mutually exclusive.
     *
     * <p>Your dataset must be formatted as a JSONL file, where each validation example is a JSON object with the keys "prompt" and "completion".
     * Additionally, you must upload your file with the purpose fine-tune.
     * See the <a href="https://platform.openai.com/docs/guides/fine-tuning/creating-training-data">fine-tuning guide</a> for more details.
     */
    @JsonProperty("validation_file")
    private String validationFile;

    /**
     * The name of the base model to fine-tune.
     *
     * <p>You can select one of "ada", "babbage", "curie", "davinci", or a fine-tuned model created after 2022-04-21.
     * To learn more about these models, see the <a href="https://platform.openai.com/docs/models">Models documentation</a>.
     */
    private String model;

    /**
     * The number of epochs to train the model for.
     *
     * <p>An epoch refers to one full cycle through the training dataset.
     */
    @JsonProperty("n_epochs")
    private Integer nEpochs;

    /**
     * The batch size to use for training.
     *
     * <p>The batch size is the number of training examples used to train a single forward and backward pass.
     *
     * <p>By default, the batch size will be dynamically configured to be ~0.2% of the number of examples in the training set,
     * capped at 256 - in general, we've found that larger batch sizes tend to work better for larger datasets.
     */
    @JsonProperty("batch_size")
    private Integer batchSize;

    /**
     * The learning rate multiplier to use for training.
     *
     * <p>The fine-tuning learning rate is the original learning rate used for pretraining multiplied by this value.
     *
     * <p>By default, the learning rate multiplier is the 0.05, 0.1, or 0.2 depending on final batch_size (larger learning rates tend to perform better with larger batch sizes).
     * We recommend experimenting with values in the range 0.02 to 0.2 to see what produces the best results.
     */
    @JsonProperty("learning_rate_multiplier")
    private Number learningRateMultiplier;

    /**
     * The weight to use for loss on the prompt tokens.
     *
     * <p>This controls how much the model tries to learn to generate the prompt (as compared to the completion which always has a weight of 1.0),
     * and can add a stabilizing effect to training when completions are short.
     *
     * <p>If prompts are extremely long (relative to completions),
     * it may make sense to reduce this weight so as to avoid over-prioritizing learning the prompt.
     */
    @JsonProperty("prompt_loss_weight")
    private Number promptLossWeight;

    /**
     * If set, we calculate classification-specific metrics such as accuracy and F-1 score using the validation set at the end of every epoch.
     * These metrics can be viewed in the <a href="https://platform.openai.com/docs/guides/fine-tuning/analyzing-your-fine-tuned-model">results file</a>.
     *
     * <p>In order to compute classification metrics, you must provide a validation_file.
     * Additionally, you must specify classification_n_classes for multiclass classification or classification_positive_class for binary classification.
     */
    @JsonProperty("compute_classification_metrics")
    private Boolean computeClassificationMetrics;

    /**
     * The number of classes in a classification task.
     *
     * <p>This parameter is required for multiclass classification.
     */
    @JsonProperty("classification_n_classes")
    private Integer classificationNClasses;

    /**
     * The positive class in binary classification.
     *
     * <p>This parameter is needed to generate precision, recall, and F1 metrics when doing binary classification.
     */
    @JsonProperty("classification_positive_class")
    private String classificationPositiveClass;

    /**
     * If this is provided, we calculate F-beta scores at the specified beta values.
     * The F-beta score is a generalization of F-1 score. This is only used for binary classification.
     *
     * <p>With a beta of 1 (i.e. the F-1 score), precision and recall are given the same weight.
     * A larger beta score puts more weight on recall and less on precision.
     * A smaller beta score puts more weight on precision and less on recall.
     */
    @JsonProperty("classification_betas")
    private List<String> classificationBetas;

    /**
     * A string of up to 40 characters that will be added to your fine-tuned model name.
     *
     * <p>For example, a suffix of "custom-model-name" would produce a model name like ada:ft-your-org:custom-model-name-2022-02-15-04-21-04.
     */
    private String suffix;
}
