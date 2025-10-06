package org.odk.collect.android.formentry.questions;

import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.widgets.QuestionWidget;

import java.util.Objects;

/**
 * Data class representing a "question" for use with {@link QuestionWidget}
 * and its subclasses
 */
public class QuestionDetails {

    private final FormEntryPrompt prompt;
    private final boolean isReadOnly;

    public QuestionDetails(FormEntryPrompt prompt) {
        this(prompt, false);
    }

    public QuestionDetails(FormEntryPrompt prompt, boolean readOnlyOverride) {
        this.prompt = prompt;
        // Check if the question label matches any of the predefined fields
        String questionLabel = this.prompt.getQuestion().getLabelInnerText();
        this.isReadOnly = readOnlyOverride || prompt.isReadOnly();

//        if(Objects.equals(questionLabel, "ACTIVITY ID") ||
//                Objects.equals(questionLabel, "LGA") ||
//                Objects.equals(questionLabel, "STRATEGIC CATCHMENT") ||
//                Objects.equals(questionLabel, "SITE NAME") ||
//                Objects.equals(questionLabel, "INTERVENTION TYPE") ||
//                Objects.equals(questionLabel, "PDO INDICATOR") ||
//                Objects.equals(questionLabel, "PERCENTAGE NDVI") ||
//                Objects.equals(questionLabel, "COMMENT") ||
//                Objects.equals(questionLabel, "IMPLIMENTER") ||
//                Objects.equals(questionLabel, "START DATE") ||
//                Objects.equals(questionLabel, "END DATE") ||
//                Objects.equals(questionLabel, "COMPONENT") ||
//                Objects.equals(questionLabel, "SUB SOMPONENT") ||
//                Objects.equals(questionLabel, "STATE ID") ||
//                Objects.equals(questionLabel, "GENERAL CAT INTERVENTION") ||
//                Objects.equals(questionLabel, "ID TYPE")
//
//            ) {
//            this.isReadOnly = true;
//        } else {
//            this.isReadOnly = readOnlyOverride || prompt.isReadOnly();
//        }

    }

    public FormEntryPrompt getPrompt() {
        return prompt;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }
}
