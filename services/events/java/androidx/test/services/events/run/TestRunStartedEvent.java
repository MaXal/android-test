/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.test.services.events.run;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.test.services.events.TestCaseInfo;

/**
 * Denotes that the test ended with a TEST_RUN_STARTED event. It has the {@link TestCaseInfo} object
 * to denote which case this event is associated to.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class TestRunStartedEvent extends TestRunEventWithTestCase {
  /**
   * Constructor to create {@link TestRunStartedEvent}.
   *
   * @param testCase the test case that this event is for.
   */
  public TestRunStartedEvent(@NonNull TestCaseInfo testCase) {
    super(testCase);
  }

  TestRunStartedEvent(Parcel source) {
    super(source);
  }

  @Override
  EventType instanceType() {
    return EventType.STARTED;
  }
}
