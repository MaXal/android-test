/*
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.test.platform.io;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link PlatformTestStorage} that reads and writes test data to the test
 * process's local storage.
 *
 * <p>Typically the test runner infrastructure will configure where test data is stored and
 * retrieved from by passing 'additionalTestOutputDir' and 'testInputDir' instrumentation arguments.
 * If these arguments are not provided, the implementation will choose an appropriate location based
 * on android API level that minimizes the need for any extra permissions. See TestDirCalculator.
 *
 * @hide
 */
@RestrictTo(Scope.LIBRARY_GROUP)
public final class FileTestStorage implements PlatformTestStorage {

  private static final String TAG = FileTestStorage.class.getSimpleName();
  private final TestDirCalculator testDirCalculator;

  public FileTestStorage() {
    testDirCalculator = new TestDirCalculator();
  }

  /**
   * Provides an InputStream to a test file dependency.
   *
   * @param pathname relative path to the test file dependency. Should not be null.
   */
  @Override
  public InputStream openInputFile(String pathname) throws FileNotFoundException {
    File inputFile = new File(testDirCalculator.getInputDir(), pathname);
    return new FileInputStream(inputFile);
  }

  /**
   * Provides an OutputStream to a test output file.
   *
   * @param pathname path to the test file dependency. Should not be null. Can be either a relative
   *     or absolute path. If relative, the implementation will make a best effort attempt to a
   *     writable output dir based on API level.
   */
  @Override
  public OutputStream openOutputFile(String pathname) throws FileNotFoundException {
    return openOutputFile(pathname, false);
  }

  @Override
  public OutputStream openOutputFile(String pathname, boolean append) throws FileNotFoundException {
    File outputFile = new File(testDirCalculator.getOutputDir(), pathname);
    Log.d("FileTestStorage", "openOutputFile from " + outputFile.getAbsolutePath());
    if (!outputFile.getParentFile().exists()) {
      if (!outputFile.getParentFile().mkdirs()) {
        throw new FileNotFoundException(
            "Failed to create output dir " + outputFile.getParentFile().getAbsolutePath());
      }
    }
    return new FileOutputStream(outputFile, append);
  }

  /** Implementation of input arguments that reads from InstrumentationRegistry.getArguments */
  @Override
  public String getInputArg(String argName) {
    return InstrumentationRegistry.getArguments().getString(argName);
  }

  /** Implementation of input arguments that reads from InstrumentationRegistry.getArguments */
  @Override
  public Map<String, String> getInputArgs() {
    Map<String, String> argMap = new HashMap<>();
    Bundle bundle = InstrumentationRegistry.getArguments();
    for (String key : bundle.keySet()) {
      argMap.put(key, bundle.getString(key));
    }
    return argMap;
  }

  /** Test output properties is not supported when raw file I/O is used. */
  @Override
  public void addOutputProperties(Map<String, Serializable> properties) {
    Log.w(TAG, "Output properties is not supported.");
  }

  /**
   * Test output properties is not supported when raw file I/O is used.
   *
   * <p>An empty map is always returned.
   */
  @Override
  public Map<String, Serializable> getOutputProperties() {
    Log.w(TAG, "Output properties is not supported.");
    return Collections.emptyMap();
  }

  /**
   * Provides an InputStream to an internal file used by the testing infrastructure.
   *
   * @param pathname path to the internal input file. Should not be null. This is an absolute file
   *     path on the device, and it's the infrastructure/client's responsibility to make sure the
   *     file path is readable.
   */
  @Override
  public InputStream openInternalInputFile(String pathname) throws FileNotFoundException {
    return openInputFile(pathname);
  }

  /**
   * Provides an OutputStream to an internal file used by the testing infrastructure.
   *
   * @param pathname path to the test file dependency. Should not be null. Can be either a relative
   *     or absolute path. If relative, the implementation will read the input file from the test
   *     apk's asset directory
   */
  @Override
  public OutputStream openInternalOutputFile(String pathname) throws FileNotFoundException {
    return openOutputFile(pathname);
  }

  @Override
  public Uri getInputFileUri(@NonNull String pathname) {
    File inputFile = new File(testDirCalculator.getInputDir(), pathname);
    return Uri.fromFile(inputFile);
  }

  @Override
  public Uri getOutputFileUri(@NonNull String pathname) {
    File outputFile = new File(testDirCalculator.getOutputDir(), pathname);
    return Uri.fromFile(outputFile);
  }

  @Override
  public boolean isTestStorageFilePath(@NonNull String pathname) {
    String outputDir = testDirCalculator.getOutputDir().getAbsolutePath();
    return pathname.startsWith(outputDir);
  }
}
