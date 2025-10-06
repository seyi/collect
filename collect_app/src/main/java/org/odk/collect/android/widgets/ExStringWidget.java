/*
 * Copyright (C) 2012 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import static org.odk.collect.android.utilities.ApplicationConstants.RequestCodes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.databinding.ExStringQuestionTypeBinding;
import org.odk.collect.android.dynamicpreload.ExternalAppsUtils;
import org.odk.collect.android.R;
import org.odk.collect.android.formentry.questions.QuestionDetails;
import org.odk.collect.android.utilities.AcresalCSVMapping;
import org.odk.collect.android.utilities.Appearances;
import org.odk.collect.android.utilities.CSVDataProvider; // Our new CSV provider
import org.odk.collect.android.widgets.interfaces.WidgetDataReceiver;
import org.odk.collect.android.widgets.utilities.QuestionFontSizeUtils;
import org.odk.collect.android.widgets.utilities.StringRequester;
import org.odk.collect.android.widgets.utilities.StringWidgetUtils;
import org.odk.collect.android.widgets.utilities.WaitingForDataRegistry;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

@SuppressLint("ViewConstructor")
public class ExStringWidget extends QuestionWidget implements WidgetDataReceiver {
    public ExStringQuestionTypeBinding binding;
    private final WaitingForDataRegistry waitingForDataRegistry;

    private boolean hasExApp = true;
    private final StringRequester stringRequester;
    private CSVDataProvider csvDataProvider; // Add CSV data provider

    public ExStringWidget(Context context, QuestionDetails questionDetails, WaitingForDataRegistry waitingForDataRegistry, StringRequester stringRequester) {
        super(context, questionDetails);
        this.csvDataProvider = CSVDataProvider.getInstance(context); // Initialize CSV provider
        render();

        this.waitingForDataRegistry = waitingForDataRegistry;
        this.stringRequester = stringRequester;
    }

    @Override
    protected View onCreateAnswerView(@NonNull Context context, @NonNull FormEntryPrompt prompt, int answerFontSize) {
        binding = ExStringQuestionTypeBinding.inflate(LayoutInflater.from(context));

        // === CSV INTEGRATION LOGIC ===
        // Check if this is a CSV lookup instead of external app
        String appearance = prompt.getAppearanceHint();
        if (appearance != null && appearance.contains("ex:com.acresal.gis")) {
            return handleCSVLookup(context, prompt, answerFontSize);
        }

        // === ORIGINAL EXTERNAL APP LOGIC ===
        binding.launchAppButton.setText(getButtonText());
        binding.launchAppButton.setOnClickListener(v -> {
            waitingForDataRegistry.waitForData(getFormEntryPrompt().getIndex());
            stringRequester.launch((Activity) getContext(), getRequestCode(), getFormEntryPrompt(), getAnswerForIntent(), (String errorMsg) -> {
                onException(errorMsg);
                return null;
            });
        });

        if (questionDetails.isReadOnly()) {
            binding.launchAppButton.setVisibility(GONE);
        }

        binding.widgetAnswerText.init(
                QuestionFontSizeUtils.getFontSize(settings, QuestionFontSizeUtils.FontSize.HEADLINE_6),
                false,
                StringWidgetUtils.getNumberOfRows(questionDetails.getPrompt()),
                Appearances.isMasked(prompt),
                this::widgetValueChanged
        );
        binding.widgetAnswerText.setAnswer(getFormEntryPrompt().getAnswerText());

        return binding.getRoot();
    }

    /**
     * Handle CSV lookup for ACRESAL GIS pattern
     * Appearance format: ex:com.acresal.gis('ActivityID=ACTIVITY ID')
     */
    private View handleCSVLookup(@NonNull Context context, @NonNull FormEntryPrompt prompt, int answerFontSize) {
        try {
            // Parse intent key from appearance
            String appearance = prompt.getAppearanceHint();
            String intentKey = parseAcresalGISParameterWithMapping(appearance);

            if (intentKey == null) {
                Timber.e("ACRESAL GIS lookup: could not parse intent key from appearance: %s", appearance);
                return createErrorView("CSV configuration error: could not parse intent key");
            }

            // Get CSV value using the mapping
            String csvValue = getCsvValue(intentKey, null, prompt);

            // Create view without launch button - direct CSV population
            binding.launchAppButton.setVisibility(GONE); // Hide launch button completely

            binding.widgetAnswerText.init(
                    QuestionFontSizeUtils.getFontSize(settings, QuestionFontSizeUtils.FontSize.HEADLINE_6),
                    false, // Make read-only since value comes from CSV
                    StringWidgetUtils.getNumberOfRows(questionDetails.getPrompt()),
                    Appearances.isMasked(prompt),
                    this::widgetValueChanged
            );

            // Set CSV value directly
            binding.widgetAnswerText.setAnswer(csvValue);

            // Add indicator that this value came from CSV
            if (csvValue != null && !csvValue.isEmpty()) {
                String csvColumn = AcresalCSVMapping.INSTANCE.getCSVColumnForIntentKey(intentKey);
               // binding.widgetAnswerText.setHint("Value loaded from CSV column: " + csvColumn);
                // Make field background slightly different to indicate CSV source
                binding.widgetAnswerText.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.colorPrimary) // Light blue background
                );
            } else {
                String csvColumn = AcresalCSVMapping.INSTANCE.getCSVColumnForIntentKey(intentKey);
               // binding.widgetAnswerText.setHint("No CSV value found for column: " + csvColumn);
                // Allow manual entry if no CSV value found
                binding.widgetAnswerText.init(
                        QuestionFontSizeUtils.getFontSize(settings, QuestionFontSizeUtils.FontSize.HEADLINE_6),
                        true, // Make editable if no CSV value
                        StringWidgetUtils.getNumberOfRows(questionDetails.getPrompt()),
                        Appearances.isMasked(prompt),
                        this::widgetValueChanged
                );
            }

            Timber.d("ACRESAL GIS CSV lookup successful: intentKey=%s, csvValue=%s", intentKey, csvValue);

        } catch (Exception e) {
            Timber.e(e, "Error in ACRESAL GIS CSV lookup");
            return createErrorView("CSV lookup error: " + e.getMessage());
        }

        return binding.getRoot();
    }

    /**
     * Parse ACRESAL GIS parameter from appearance string
     * Format: ex:com.acresal.gis('ActivityID=ACTIVITY ID')
     * Extracts the CSV column name (ActivityID in this example)
     */
//    private String parseAcresalGISParameter(String appearance) {
//        try {
//            // Pattern to match: ex:com.acresal.gis('KEY=VALUE')
//            // We want to extract the KEY part as the CSV column name
//            Pattern pattern = Pattern.compile("ex:com\\.acresal\\.gis\\('([^=]+)=([^']+)'\\)");
//            Matcher matcher = pattern.matcher(appearance);
//
//            if (matcher.find()) {
//                String csvColumnName = matcher.group(1).trim(); // The part before =
//                String displayLabel = matcher.group(2).trim();  // The part after = (for reference)
//
//                Timber.d("Parsed ACRESAL GIS parameter: csvColumn=%s, displayLabel=%s", csvColumnName, displayLabel);
//                return csvColumnName;
//            }
//
//            // Fallback: try simpler pattern without full validation
//            Pattern simplePattern = Pattern.compile("'([^=]+)=");
//            Matcher simpleMatcher = simplePattern.matcher(appearance);
//
//            if (simpleMatcher.find()) {
//                String csvColumnName = simpleMatcher.group(1).trim();
//                Timber.d("Parsed ACRESAL GIS parameter (simple): csvColumn=%s", csvColumnName);
//                return csvColumnName;
//            }
//
//            Timber.w("Could not parse ACRESAL GIS parameter from: %s", appearance);
//            return null;
//
//        } catch (Exception e) {
//            Timber.e(e, "Error parsing ACRESAL GIS parameter from: %s", appearance);
//            return null;
//        }
//    }

    /**
     * Get CSV value for the specified column and row selector
     */
    private String getCsvValue(String intentKey, String rowSelectorRef, FormEntryPrompt prompt) {
        if (!csvDataProvider.isCSVLoaded()) {
            // Try to load CSV from SharedPreferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String csvData = prefs.getString("csv_data_json", null);

            if (csvData != null) {
                csvDataProvider.loadCSVFromJson(csvData);
            } else {
                Timber.w("No CSV data found in SharedPreferences");
                return null;
            }
        }

        // Get the CSV column name for this intent key using the mapping
        String csvColumn = AcresalCSVMapping.INSTANCE.getCSVColumnForIntentKey(intentKey);

        if (csvColumn == null) {
            Timber.w("No CSV column mapping found for intent key: %s", intentKey);
            return null;
        }

        Timber.d("Intent key '%s' maps to CSV column '%s'", intentKey, csvColumn);

        // Get row selector value if specified
        String rowSelectorValue = null;
        if (rowSelectorRef != null) {
            try {
                // This would need access to FormController to evaluate XPath
                // For now, use a simple approach - get from SharedPreferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                rowSelectorValue = prefs.getString("current_row_selector", null);
            } catch (Exception e) {
                Timber.w(e, "Error getting row selector value");
            }
        }

        // Get value from CSV using the mapped column name
        String csvValue = csvDataProvider.getCSVValue(csvColumn, rowSelectorValue);

        // If no row selector match, try first row
        if (csvValue == null && csvDataProvider.getRowCount() > 0) {
            csvValue = csvDataProvider.getCSVValue(csvColumn, 0);
        }

        // Apply default value handling using the mapping
        String finalValue = AcresalCSVMapping.INSTANCE.getValueWithDefault(intentKey, csvValue);

        Timber.d("CSV lookup result: intentKey=%s, csvColumn=%s, csvValue=%s, finalValue=%s",
                intentKey, csvColumn, csvValue, finalValue);

        return finalValue;
    }


    private String parseAcresalGISParameterWithMapping(String appearance) {
        if (appearance == null || appearance.trim().isEmpty()) {
            Timber.w("Appearance is null or empty");
            return null;
        }

        try {
            String intentKey = null;

            // Try multiple patterns to handle different formats
            String[] patterns = {
                    "ex:com\\.acresal\\.gis\\(([^=]+)='([^']+)'\\)",     // Format: ex:com.acresal.gis(key='value')
                    "ex:com\\.acresal\\.gis\\('([^=]+)=([^']+)'\\)",     // Format: ex:com.acresal.gis('key=value')
                    "ex:com\\.acresal\\.gis\\(([^=)]+)\\)",              // Format: ex:com.acresal.gis(key)
                    "'([^=]+)=([^']+)'",                                 // Fallback: 'key=value'
                    "([A-Za-z_][A-Za-z0-9_]*)"                          // Fallback: just the key name
            };

            for (String patternStr : patterns) {
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(appearance);

                if (matcher.find()) {
                    intentKey = matcher.group(1).trim();
                    Timber.d("Parsed intent key '%s' using pattern: %s", intentKey, patternStr);
                    break;
                }
            }

            if (intentKey != null) {
                // Validate that the intent key exists in our mapping
                if (AcresalCSVMapping.INSTANCE.isValidIntentKey(intentKey)) {
                    Timber.d("Valid intent key found: %s", intentKey);
                    return intentKey;
                } else {
                    // Try to find a similar key
                    Set<String> availableKeys = AcresalCSVMapping.INSTANCE.getAllIntentKeys();
                    String similarKey = findSimilarKey(intentKey, availableKeys);

                    if (similarKey != null) {
                        Timber.d("Using similar key '%s' for '%s'", similarKey, intentKey);
                        return similarKey;
                    } else {
                        String availableKeysStr = String.join(", ", availableKeys);
                        Timber.w("Intent key '%s' not found in mapping. Available keys: %s",
                                intentKey, availableKeysStr);
                        return null;
                    }
                }
            }

            Timber.w("Could not parse any intent key from appearance: %s", appearance);
            return null;

        } catch (Exception e) {
            Timber.e(e, "Error parsing ACRESAL GIS parameter from: %s", appearance);
            return null;
        }
    }

    private String findSimilarKey(String inputKey, Set<String> availableKeys) {
        String normalizedInput = inputKey.toLowerCase().replace("_", "").replace("-", "");

        for (String availableKey : availableKeys) {
            String normalizedAvailable = availableKey.toLowerCase().replace("_", "").replace("-", "");

            // Check for exact match after normalization
            if (normalizedInput.equals(normalizedAvailable)) {
                return availableKey;
            }

            // Check for contains relationship
            if (normalizedInput.contains(normalizedAvailable) || normalizedAvailable.contains(normalizedInput)) {
                return availableKey;
            }
        }

        return null;
    }




    /**
     * Create error view when CSV lookup fails
     */
    private View createErrorView(String errorMessage) {
        binding.launchAppButton.setVisibility(GONE);
        binding.widgetAnswerText.init(
                QuestionFontSizeUtils.getFontSize(settings, QuestionFontSizeUtils.FontSize.HEADLINE_6),
                true, // Allow manual entry on error
                StringWidgetUtils.getNumberOfRows(questionDetails.getPrompt()),
                false,
                this::widgetValueChanged
        );
        binding.widgetAnswerText.setAnswer("");
        //binding.widgetAnswerText.setHint(errorMessage);
        binding.widgetAnswerText.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.colorError) // Light red background for error
        );
        return binding.getRoot();
    }

    // === REST OF THE ORIGINAL METHODS UNCHANGED ===
    private String getButtonText() {
        String v = getFormEntryPrompt().getSpecialFormQuestionText("buttonText");
        return v != null ? v : getContext().getString(org.odk.collect.strings.R.string.launch_app);
    }

    protected Serializable getAnswerForIntent() {
        return getFormEntryPrompt().getAnswerText();
    }

    protected int getRequestCode() {
        return RequestCodes.EX_STRING_CAPTURE;
    }

    @Nullable
    @Override
    public IAnswerData getAnswer() {
        String answer = binding.widgetAnswerText.getAnswer();
        return !answer.isEmpty() ? new StringData(answer) : null;
    }

    @Override
    public void clearAnswer() {
        binding.widgetAnswerText.clearAnswer();
    }

    @Override
    public void setData(Object answer) {
        StringData stringData = ExternalAppsUtils.asStringData(answer);
        binding.widgetAnswerText.setAnswer(stringData == null ? null : stringData.getValue().toString());
    }

    @Override
    public void setFocus(Context context) {
        if (hasExApp) {
            binding.widgetAnswerText.setFocus(false);
            // focus on launch button
            binding.launchAppButton.requestFocus();
        } else {
            if (!getFormEntryPrompt().isReadOnly()) {
                binding.widgetAnswerText.setFocus(true);
            /*
             * If you do a multi-question screen after a "add another group" dialog, this won't
             * automatically pop up. It's an Android issue.
             *
             * That is, if I have an edit text in an activity, and pop a dialog, and in that
             * dialog's button's OnClick() I call edittext.requestFocus() and
             * showSoftInput(edittext, 0), showSoftinput() returns false. However, if the
             * edittext
             * is focused before the dialog pops up, everything works fine. great.
             */
            } else {
                binding.widgetAnswerText.setFocus(false);
            }
        }
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        binding.widgetAnswerText.setOnLongClickListener(l);
        binding.launchAppButton.setOnLongClickListener(l);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        binding.widgetAnswerText.cancelLongPress();
        binding.launchAppButton.cancelLongPress();
    }

    /**
     * Registers all subviews except for the answer_container (which contains the EditText) to clear on long press.
     * This makes it possible to long-press to paste or perform other text editing functions.
     */
    @Override
    protected void registerToClearAnswerOnLongPress(Activity activity, ViewGroup viewGroup) {
        ViewGroup view = findViewById(R.id.question_widget_container);
        for (int i = 0; i < view.getChildCount(); i++) {
            View childView = view.getChildAt(i);
            if (childView.getId() != R.id.answer_container) {
                childView.setTag(childView.getId());
                childView.setId(getId());
                activity.registerForContextMenu(childView);
            }
        }
    }

    private void focusAnswer() {
        binding.widgetAnswerText.setFocus(true);
    }

    private void onException(String toastText) {
        hasExApp = false;
        if (!getFormEntryPrompt().isReadOnly()) {
            binding.widgetAnswerText.updateState(false);
        }
        binding.launchAppButton.setEnabled(false);
        binding.launchAppButton.setFocusable(false);
        waitingForDataRegistry.cancelWaitingForData();

        Toast.makeText(getContext(),
                toastText, Toast.LENGTH_SHORT)
                .show();
        Timber.d(toastText);
        focusAnswer();
    }

    @Override
    public void hideError() {
        super.hideError();
        binding.widgetAnswerText.setError(null);
    }

    @Override
    public void displayError(String errorMessage) {
        hideError();

        if (binding.widgetAnswerText.isEditableState()) {
            binding.widgetAnswerText.setError(errorMessage);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.question_with_error_border));
        } else {
            ((TextView) errorLayout.findViewById(R.id.error_message)).setText(errorMessage);
            errorLayout.setVisibility(VISIBLE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.question_with_error_border));
        }
    }
}
