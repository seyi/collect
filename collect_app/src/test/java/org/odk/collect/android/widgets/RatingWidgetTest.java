package org.odk.collect.android.widgets;

import android.view.View;

import org.javarosa.core.model.RangeQuestion;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.collect.android.databinding.RatingWidgetAnswerBinding;
import org.odk.collect.android.formentry.questions.QuestionDetails;
import org.odk.collect.android.listeners.WidgetValueChangedListener;
import org.robolectric.RobolectricTestRunner;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.mockValueChangedListener;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.promptWithAnswer;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.promptWithReadOnly;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.widgetTestActivity;

@RunWith(RobolectricTestRunner.class)
public class RatingWidgetTest {

    private RatingWidgetAnswerBinding binding;
    private RangeQuestion rangeQuestion;

    @Before
    public void setup() {
        rangeQuestion = mock(RangeQuestion.class);
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(5));
    }

    @Test
    public void getAnswer_whenPromptAnswerDoesNotHaveAnswer_returnsNull() {
        assertThat(createWidget(promptWithAnswer(null)).getAnswer(), nullValue());
    }

    @Test
    public void getAnswer_whenPromptHasAnswer_returnsAnswer() {
        RatingWidget widget = createWidget(promptWithAnswer(new StringData("2")));
        assertThat(widget.getAnswer().getValue(), equalTo(1));
    }

    @Test
    public void clearAnswer_clearsWidgetAnswer() {
        RatingWidget widget = createWidget(promptWithAnswer(new StringData("2")));

        widget.clearAnswer();
        assertThat(widget.getAnswer(), nullValue());
    }

    @Test
    public void clearAnswer_callsValueChangeListeners() {
        RatingWidget widget = createWidget(promptWithAnswer(new StringData("2")));
        WidgetValueChangedListener valueChangedListener = mockValueChangedListener(widget);
        widget.setValueChangedListener(valueChangedListener);

        widget.clearAnswer();
        verify(valueChangedListener).widgetValueChanged(widget);
    }

    private RatingWidget createWidget(FormEntryPrompt prompt) {
        return new RatingWidget(widgetTestActivity(), new QuestionDetails(prompt, "formAnalyticsID"), rangeQuestion);
    }
}
